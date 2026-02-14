package arr.armuriii.ccl.script;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.resources.script.JsonScript;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.data.CraftingExtraData;
import arr.armuriii.ccl.script.string.ScriptReader;
import arr.armuriii.ccl.util.TagConversionUtils;
import com.google.gson.JsonObject;
import net.minecraft.nbt.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class ScriptFinder {

    public static NbtElement checkScript(NbtCompound nbt, List<ItemStack> items) {
        if (nbt == null || nbt.isEmpty())
            return nbt;
        if (items.isEmpty())
            return null;
        NbtCompound result = nbt.copy();
        Optional<NbtElement> optionalNbt = checkScriptAsCompound(result,items);
        if (optionalNbt.isPresent()) {
            NbtElement checked = optionalNbt.get();
            if (checked instanceof NbtCompound compoundTag)
                result.copyFrom(compoundTag);
            else
                return checked;
        }
        for (String key : nbt.getKeys()) {
            if (nbt.contains(key, NbtElement.STRING_TYPE)) {
                String string = nbt.getString(key);
                Optional<NbtElement> optionalTag = checkScriptAsString(string,items);
                optionalTag.ifPresent(tag -> result.put(key, tag));
            }else if (nbt.contains(key, NbtElement.LIST_TYPE)) {
                NbtList listTag = (NbtList) nbt.get(key);
                if (listTag != null) {
                    NbtList tag = checkScript(listTag,items);
                    result.put(key,tag);
                }
            }else if (nbt.contains(key, NbtElement.COMPOUND_TYPE)) {
                NbtCompound compoundTag = nbt.getCompound(key);
                NbtElement tag = checkScript(compoundTag,items);
                result.put(key,tag);
            }
        }
        return result;
    }

    public static NbtList checkScript(NbtList listTag, List<ItemStack> items) {
        NbtList result = listTag.copy();
        Optional<NbtElement> optionalNbt = checkScriptAsList(result,items);
        if (optionalNbt.isPresent()) {
            NbtElement nbt = optionalNbt.get();
            if (nbt instanceof NbtList addedList)
                result.addAll(addedList);
            else
                result.add(0,nbt);
        }
        for (int i = 0; i < listTag.size(); i++) {
            NbtElement entry = listTag.get(i);
            if (entry instanceof NbtString stringTag) {
                String string = stringTag.asString();
                Optional<NbtElement> optionalTag = checkScriptAsString(string,items);
                final int transfer = i;// because you can't if it's not final...
                optionalTag.ifPresent((tag -> result.set(transfer, tag)));
            }else if (entry instanceof NbtList listTag1) {

                NbtList tag = checkScript(listTag1,items);
                result.set(i,tag);
            }else if (entry instanceof NbtCompound compoundTag) {
                NbtElement tag = checkScript(compoundTag,items);
                result.set(i,tag);
            }
        }
        return result;
    }

    public static Optional<NbtElement> checkScriptAsString(String string, List<ItemStack> stacks) {
        if (string.startsWith("@-")) {
            string = string.replace("@-","");
            char c = string.charAt(0);
            string = string.replaceFirst(String.valueOf(c),"");
            ScriptReader script = new ScriptReader(string,c,stacks);
            NbtElement tag = TagConversionUtils.getTag(script.replaceExpression());
            return Optional.ofNullable(tag);
        }
        return Optional.empty();
    }

    public static Optional<NbtElement> checkScriptAsCompound(NbtCompound nbt, List<ItemStack> stacks) {
        if (nbt.contains(CustomCraftingLibrary.id("script").toTranslationKey(), NbtElement.STRING_TYPE)) {
            return replaceScript(
                    List.of(
                            Optional.ofNullable(nbt.get(CustomCraftingLibrary.id("script").toTranslationKey())).orElseThrow(),
                            Optional.ofNullable(nbt.get(CustomCraftingLibrary.id("return-type").toTranslationKey())).orElse(NbtEnd.INSTANCE)
                    ),
                    false,stacks,nbt,(NbtCompound::remove));
        }
        return Optional.empty();
    }

    public static Optional<NbtElement> checkScriptAsList(NbtList listTag, List<ItemStack> stacks) {
        if (listTag.get(0) instanceof NbtString stringTag && stringTag.asString().startsWith(CustomCraftingLibrary.id("script:").toTranslationKey()))
            return replaceScript(List.of(listTag.get(0),listTag.get(1)),
                    true,stacks,
                    listTag,
                    (nbtElements, ignored) -> nbtElements.remove(0));
        return Optional.empty();
    }

    public static <T extends NbtElement> Optional<NbtElement> replaceScript(List<NbtElement> elements, boolean isCompact, List<ItemStack> stacks, T element, BiConsumer<T,String> deleting) {
        String script = elements.get(0).asString();
        if (script.endsWith(".json"))
            return getJsonScript(script,stacks);

        if (isCompact)
            script = script.replace(CustomCraftingLibrary.id("script:").toTranslationKey(),"");
        deleting.accept(element,CustomCraftingLibrary.id("script").toTranslationKey());

        if (script.startsWith("@-"))
            return checkScriptAsString(script,stacks);
        char c = 'U';

        if (elements.get(1) instanceof NbtString returnTag && returnTag.asString().startsWith(CustomCraftingLibrary.id("return-type:").toTranslationKey()))
            c = returnTag.asString().replace(CustomCraftingLibrary.id("return-type:").toTranslationKey(), "").charAt(0);
        deleting.accept(element,CustomCraftingLibrary.id("return-type").toTranslationKey());
        ScriptReader scriptReader = new ScriptReader(script,c,stacks);
        NbtElement tag = TagConversionUtils.getTag(scriptReader.replaceExpression());
        return Optional.ofNullable(tag);
    }

    public static Optional<NbtElement> getJsonScript(String script, List<ItemStack> stacks) {
        Optional<JsonObject> reader = JsonScript.getScript(new Identifier(script.replace(".json","")));
        if (reader.isPresent()) {
            NbtElement tag = TagConversionUtils.getTag(JsonScriptReader.evaluate(reader.get(), new CraftingExtraData(stacks)));
            return Optional.ofNullable(tag);
        }
        return Optional.empty();
    }
}
