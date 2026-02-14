package arr.armuriii.ccl.util;

import arr.armuriii.ccl.script.json.Token;
import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonConversionUtils {
    /*
    * this is for JSON Scripts, do not use it anywhere else
    * if the JsonElement is a String,
    * it will either:
    * be a String if the String has \" at start and end
    * or a Token if the String does not have \"
    */
    public static Object getValue(JsonElement element) {
        if (element instanceof JsonObject object) {
            try{
                return ShapedRecipe.outputFromJson(object);
            } catch (Exception ignored) {}

            try {
                return ShapedRecipe.getItem(object);
            } catch (Exception ignored) {}

            try {
                return Ingredient.fromJson(object);
            } catch (Exception ignored) {}

            Map<String, Object> map = new HashMap<>();
            for (var entry : object.asMap().entrySet())
                map.putIfAbsent(entry.getKey(),getValue(entry.getValue()));
            return map;
        }
        if (element instanceof JsonPrimitive primitive)
            if (primitive.isBoolean())
                return primitive.getAsBoolean();
            else if (primitive.isString())
                return reformatString(primitive.getAsString());
            else
                return getNumberType(primitive.getAsNumber());
        if (element instanceof JsonArray array)
                return array.asList().stream().map(JsonConversionUtils::getValue);
        return null;
    }

    //since JsonPrimitive gives a LazilyParsedNumber, we need to correct it to the correct number
    public static Object getNumberType(Number number) {
        if (number.toString().contains("."))
            return number.floatValue();
        return number.intValue();
    }

    public static Object reformatString(String s) {
        if (s.startsWith("\"") && s.endsWith("\""))
            return s.replaceAll("(?<!\\\\)\"","");
        else
            return new Token(s);
    }

    public static Map<String, Object> getStrictValue(JsonObject object) {
        Map<String, Object> map = new HashMap<>();
        for (var entry : object.asMap().entrySet())
            map.putIfAbsent(entry.getKey(),getValue(entry.getValue()));
        return map;
    }

    public static JsonElement getJson(Object o) {
        if (o instanceof Number n)
            return new JsonPrimitive(n);
        if (o instanceof String s)
            return new JsonPrimitive(s);
        if (o instanceof Map<?,?> map) {
            JsonObject object = new JsonObject();
            for (var entry : map.entrySet())
                if (entry.getKey() instanceof String key)
                    object.add(key, getJson(entry.getValue()));
            return object;
        }
        if (o instanceof List<?> list) {
            JsonArray array = new JsonArray();
            for (var entry : list)
                array.add(getJson(entry));
            return array;
        }
        return JsonNull.INSTANCE;
    }

    // from net.fabricmc.fabric.impl.recipe.ingredient.builtin.NbtIngredient.Serializer.readNbt
    public static NbtCompound readNbt(@Nullable JsonElement json) {
        // Process null
        if (json == null || json.isJsonNull()) {
            return null;
        }

        try {
            if (json.isJsonObject()) {
                // We use a normal .toString() to convert the json to string, and read it as SNBT.
                // Using DynamicOps would mess with the type of integers and cause things like damage comparisons to fail...
                return StringNbtReader.parse(json.toString());
            } else {
                // Assume it's a string representation of the NBT
                return StringNbtReader.parse(JsonHelper.asString(json, "nbt"));
            }
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
        }
    }
}
