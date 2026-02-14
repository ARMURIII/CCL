package arr.armuriii.ccl.script.json.components.crafting;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonComponentsImpl;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.components.JsonComponent;
import arr.armuriii.ccl.script.json.components.JsonComponentFactory;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import arr.armuriii.ccl.util.NbtPathUtils;
import arr.armuriii.ccl.util.TagConversionUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;

public class JsonNbt extends JsonComponent<Object> {

    protected final JsonComponent<?> stack;
    protected final JsonComponent<?> path;

    public JsonNbt(JsonComponent<?> stack, JsonComponent<?> path) {
        this.stack = stack;
        this.path = path;
    }

    @Override
    public Object apply(Map<String, JsonComponent<?>> variables, AbstractExtraData extraData) {
        return TagConversionUtils.getValue(
                NbtPathUtils.maybeGet(
                        path.apply(variables, extraData,String.class),
                        stack.apply(variables, extraData,ItemStack.class).getOrCreateNbt()
                ).orElse(NbtEnd.INSTANCE)
        );
    }

    @Override
    public JsonElement write() {
        return null;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonNbt> {

        @Override
        public Class<Object> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return jsonElement instanceof JsonObject jsonObject &&
                    Objects.equals(jsonObject.get(JsonComponentsImpl.FUNCTION_KEY).getAsString(), "nbt") &&
                    jsonObject.has(JsonComponentsImpl.RECEIVER_KEY) &&
                    jsonObject.has("path");
        }

        @Override
        public JsonNbt create(JsonElement jsonElement) {
            if (jsonElement instanceof JsonObject jsonObject)
                return new JsonNbt(
                        JsonScriptReader.getComponent(jsonObject.get(JsonComponentsImpl.RECEIVER_KEY)),
                        JsonScriptReader.getComponent(jsonObject.get("path"))
                );
            return null;
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("nbt");
        }
    }
}
