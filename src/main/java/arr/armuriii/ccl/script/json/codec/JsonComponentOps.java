package arr.armuriii.ccl.script.json.codec;

import arr.armuriii.ccl.script.json.components.*;
import arr.armuriii.ccl.script.json.constructor.JsonArgs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

public class JsonComponentOps implements DynamicOps<JsonComponent<?>> {
    public static final JsonComponentOps INSTANCE = new JsonComponentOps();

    @Override
    public JsonComponent<?> empty() {
        return new JsonNull();
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, JsonComponent<?> input) {
        if (input instanceof JsonCompound compound)
            return convertList(outOps, compound);
        if (input instanceof JsonMap map)
            return convertMap(outOps, map);
        Object applied = input instanceof JsonLiteral literal ? literal.value : input.apply(Map.of(),null);
        if (applied instanceof Map<?,?>)
            return convertMap(outOps, input);
        if (applied instanceof Collection<?>)
            return convertList(outOps, input);

        if (applied instanceof String s)
            return outOps.createString(s);
        if (applied instanceof Boolean b)
            return outOps.createBoolean(b);

        if (applied instanceof Number number) {
            final BigDecimal value = BigDecimal.valueOf(number.longValue());
            try {
                final long l = value.longValueExact();
                if ((byte) l == l)
                    return outOps.createByte((byte) l);
                if ((short) l == l)
                    return outOps.createShort((short) l);
                if ((int) l == l)
                    return outOps.createInt((int) l);
                return outOps.createLong(l);
            } catch (final ArithmeticException e) {
                final double d = value.doubleValue();
                if ((float) d == d) {
                    return outOps.createFloat((float) d);
                }
                return outOps.createDouble(d);
            }
        }
        if (applied instanceof ItemStack stack) {
            var result = ItemStack.CODEC.encodeStart(outOps,stack).result();
            if (result.isPresent())
                return result.get();
        }
        if (applied instanceof Item item) {
            var result = JsonCodec.ITEM_CODEC.encodeStart(outOps,item).result();
            if (result.isPresent())
                return result.get();
        }
        if (applied instanceof Ingredient ingredient) {
            var result = JsonCodec.INGREDIENT_CODEC.encodeStart(outOps,ingredient).result();
            if (result.isPresent())
                return result.get();
        }
        return null;
    }

    @Override
    public DataResult<Number> getNumberValue(JsonComponent<?> input) {
        Object applied = input instanceof JsonLiteral literal ? literal.value : input.apply(Map.of(),null);
        if (applied instanceof Number number) {
            return DataResult.success(number);
        } else if (applied instanceof Boolean bool) {
            return DataResult.success(bool ? 1 : 0);
        }
        if (applied instanceof String string) {
            try {
                return DataResult.success(Integer.parseInt(string));
            } catch (final NumberFormatException e) {
                return DataResult.error(() -> "Not a number: " + e + " " + input);
            }
        }
        return DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public JsonComponent<?> createNumeric(Number i) {
        return new JsonLiteral(i);
    }

    @Override
    public DataResult<String> getStringValue(JsonComponent<?> input) {
        Object applied = input instanceof JsonLiteral literal ? literal.value : input.apply(Map.of(),null);
        if (applied instanceof String string)
            return DataResult.success(string);
        return DataResult.error(() -> "Not a String: " + input);
    }

    @Override
    public JsonComponent<?> createString(String value) {
        return new JsonLiteral(value);
    }

    @Override
    public DataResult<JsonComponent<?>> mergeToList(JsonComponent<?> list, JsonComponent<?> value) {
        if (!(list instanceof JsonCompound compound))
            return DataResult.error(()->"Not a JsonCompound: " + list);
        ArrayList<JsonComponent<?>> arrayList = new ArrayList<>(compound.components);
        arrayList.add(value);
        return DataResult.success(new JsonCompound(arrayList));
    }

    @Override
    public DataResult<JsonComponent<?>> mergeToMap(JsonComponent<?> map, JsonComponent<?> key, JsonComponent<?> value) {
        if (!(map instanceof JsonMap jsonMap))
            return DataResult.error(()->"Not a JsonMap: " + map);
        var components = jsonMap.components;
        components.put(key.toString(),value);
        return DataResult.success(new JsonMap(components));
    }

    @Override
    public DataResult<Stream<Pair<JsonComponent<?>, JsonComponent<?>>>> getMapValues(JsonComponent<?> input) {
        if (!(input instanceof JsonMap jsonMap))
            return DataResult.error(()->"Not a JsonMap: " + input);
        ArrayList<Pair<JsonComponent<?>, JsonComponent<?>>> list = new ArrayList<>();
        for (Map.Entry<String, JsonComponent<?>> entry : jsonMap.components.entrySet()) {
            list.add(Pair.of(new JsonLiteral(entry.getKey()),entry.getValue()));
        }
        return DataResult.success(list.stream());
    }

    @Override
    public JsonComponent<?> createMap(Stream<Pair<JsonComponent<?>, JsonComponent<?>>> map) {
        Map<String,JsonComponent<?>> hashMap = new HashMap<>();
        for (Pair<JsonComponent<?>, JsonComponent<?>> pair : map.toList())
            hashMap.put(pair.getFirst().toString(), pair.getSecond());
        return new JsonMap(hashMap);
    }

    @Override
    public DataResult<Stream<JsonComponent<?>>> getStream(JsonComponent<?> input) {
        if (!(input instanceof JsonCompound compound))
            return DataResult.error(()->"Not a JsonCompound: " + input);
        return DataResult.success(compound.components.stream());
    }

    @Override
    public JsonComponent<?> createList(Stream<JsonComponent<?>> input) {
        return new JsonCompound(new ArrayList<>(input.toList()));
    }

    @Override
    public JsonComponent<?> remove(JsonComponent<?> input, String key) {
        if (!(input instanceof JsonMap jsonMap))
            return null;
        var map = jsonMap.components;
        map.remove(key);
        return new JsonMap(map);
    }
}
