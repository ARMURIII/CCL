package arr.armuriii.ccl.script.json.codec;

import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.stream.Stream;

public class JsonCodec {

    public static Codec<Ingredient.Entry> INGREDIENT_ENTRY_CODEC = Codec.STRING.xmap(
            JsonCodec::entryFromString,
            JsonCodec::stringFromEntry
    ).stable();

    private static Ingredient.Entry entryFromString(String s) {
        if (s.startsWith("#")) {
            Identifier identifier = new Identifier(s.replace("#",""));
            TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, identifier);
            return new Ingredient.TagEntry(tagKey);
        } else {
            Item item = Registries.ITEM.getOrEmpty(Identifier.tryParse(s))
                    .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + s + "'"));
            return new Ingredient.StackEntry(new ItemStack(item));
        }
    }

    private static String stringFromEntry(Ingredient.Entry entry) {
        if (entry instanceof Ingredient.TagEntry tagEntry)
            return "#"+tagEntry.tag.id().toString();
        if (entry instanceof Ingredient.StackEntry stackEntry)
            return Registries.ITEM.getId(stackEntry.stack.getItem()).toString();
        return null;
    }


    public static Codec<Ingredient> INGREDIENT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            INGREDIENT_ENTRY_CODEC.listOf().fieldOf("entries")
                                    .forGetter(ingredient -> Arrays.stream(ingredient.entries).toList())
                    ).apply(instance, entries -> Ingredient.ofEntries(entries.stream()))
    );


    public static Codec<Item> ITEM_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("item").forGetter(item -> Registries.ITEM.getId(item).toString())
                    ).apply(instance, s -> Registries.ITEM.get(Identifier.splitOn(s,':')))
    );
}
