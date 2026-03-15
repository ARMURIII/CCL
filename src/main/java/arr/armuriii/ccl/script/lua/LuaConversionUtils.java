package arr.armuriii.ccl.script.lua;

import arr.armuriii.ccl.util.TagConversionUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.ast.Str;

import java.util.*;

public class LuaConversionUtils {
    public static LuaTable fromItemStack(ItemStack stack) {
        ArrayList<LuaValue> values = new ArrayList<>();
        values.add(LuaValue.valueOf("id"));
        values.add(LuaValue.valueOf(Registries.ITEM.getId(stack.getItem()).toString()));

        values.add(LuaValue.valueOf("count"));
        values.add(LuaValue.valueOf(stack.getCount()));

        values.add(LuaValue.valueOf("nbt"));
        values.add(fromNbt(stack.getOrCreateNbt()));
        return new LuaTable(values.toArray(new LuaValue[0]),new LuaValue[0],null);
    }
    public static ItemStack toItemStack(LuaTable table) {
        ItemStack stack = new ItemStack(Registries.ITEM.get(Identifier.splitOn(table.get("id").checkjstring(),':')),table.get("count").checkint());
        NbtCompound nbt = toNbt(table.get("nbt").checktable());
        return stack;
    }

    public static LuaValue fromNbt(NbtCompound nbt) {
        HashMap<String,Object> map = new HashMap<>();
        for (String key : nbt.getKeys())
            map.put(key, TagConversionUtils.getValue(nbt.get(key)));

        return getLua(map);
    }

    public static NbtCompound toNbt(LuaTable table) {
        Map<Object,Object> map = (Map<Object, Object>) getValue(table);
        NbtCompound tags = new NbtCompound();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String s))
                return null;
            tags.put(s,TagConversionUtils.getTag(entry.getValue()));
        }
        return tags;
    }

    public static LuaValue getLua(Object object) {
        if (object instanceof Boolean v)
            return LuaValue.valueOf(v);
        if (object instanceof Integer v)
            return LuaValue.valueOf(v);
        if (object instanceof Float v)
            return LuaValue.valueOf(v);
        if (object instanceof Double v)
            return LuaValue.valueOf(v);
        if (object instanceof Long v)
            return LuaValue.valueOf(v);
        if (object instanceof Short v)
            return LuaValue.valueOf(v);
        if (object instanceof String v)
            return LuaValue.valueOf(v);
        if (object instanceof Byte v)
            return LuaValue.valueOf(v);
        if (object instanceof Map<?,?> map) {
            var list = new ArrayList<LuaValue>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
               list.add(getLua(entry.getKey()));
               list.add(getLua(entry.getValue()));
            }
            return new LuaTable(
                    list.toArray(new LuaValue[0]),
                    new LuaValue[0],
                    null
            );
        }
        if (object instanceof List<?> list) {
            return new LuaTable(
                    new LuaValue[0],
                    list.stream().map(LuaConversionUtils::getLua).toList().toArray(new LuaValue[0]),
                    null
            );
        }
        if (object instanceof Integer[] array)
            return new LuaTable(
                    new LuaValue[0],
                    Arrays.stream(array).map(LuaConversionUtils::getLua).toList().toArray(new LuaValue[0]),
                    null
            );
        if (object instanceof Long[] array)
            return new LuaTable(
                    new LuaValue[0],
                    Arrays.stream(array).map(LuaConversionUtils::getLua).toList().toArray(new LuaValue[0]),
                    null
            );
        if (object instanceof Byte[] array)
            return new LuaTable(
                    new LuaValue[0],
                    Arrays.stream(array).map(LuaConversionUtils::getLua).toList().toArray(new LuaValue[0]),
                    null
            );

        return LuaValue.NIL;
    }

    public static Object getValue(LuaValue value) {
        try {
            return value.checkboolean();
        } catch (Exception ignored) {}
        try {
            return value.checkint();
        } catch (Exception ignored) {}
        try {
            return value.checkjstring();
        } catch (Exception ignored) {}
        try {
            return value.checkdouble();
        } catch (Exception ignored) {}
        try {
            return value.checklong();
        } catch (Exception ignored) {}
        try {
            HashMap<Object,Object> map = new HashMap<>();
            LuaTable table = value.checktable();
            LuaValue key = LuaValue.NIL;
            while ( true ) {
                Varargs n = table.next(key);
                if ( (key = n.arg1()).isnil() )
                    break;
                LuaValue val = n.arg(2);
                map.put(getValue(key),getValue(val));
            }
            // the code below is the check if the table is a list, a map, or both
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                if (!(entry.getKey() instanceof Integer))
                    return map; // if the table is a map or both, directly return the map
            }
            return map.values(); // if the table is strictly a list, return only the values
        } catch (Exception ignored) {}
        return null;
    }
}
