package arr.armuriii.ccl.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.*;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class TagConversionUtils {
    public static Object getValue(NbtElement tag) {
        if (tag instanceof AbstractNbtNumber numeric)
            return numeric.doubleValue();
        if (tag instanceof NbtString string)
            return string.asString();
        if (tag instanceof NbtCompound nbt) {
            Map<String,Object> map = Maps.newHashMap();
            for (String key : nbt.getKeys())
                map.putIfAbsent(key,getValue(nbt.get(key)));
            return map;
        }
        if (tag instanceof NbtIntArray intTags)
            if (intTags.getIntArray().length == 4)
                return NbtHelper.toUuid(intTags);
        if (tag instanceof NbtList tags) {
            List<Object> list = Lists.newArrayList();
            for (NbtElement value : tags) list.add(getValue(value));
            return list;
        }
        if (tag instanceof NbtIntArray tags)
            return tags.getIntArray();
        if (tag instanceof NbtByteArray tags)
            return tags.getByteArray();
        if (tag instanceof NbtLongArray tags)
            return tags.getLongArray();
        return null;
    }
    public static Map<String,Object> getStrictValue(NbtCompound nbt) {
        Map<String,Object> map = Maps.newHashMap();
        for (String key : nbt.getKeys())
            map.putIfAbsent(key,getValue(nbt.get(key)));
        return map;
    }
    public static NbtElement getTag(Object object) {
        if (object instanceof Integer n)
            return NbtInt.of(n);
        if (object instanceof Double n)
            return NbtDouble.of(n);
        if (object instanceof Float n)
            return NbtFloat.of(n);
        if (object instanceof Short n)
            return NbtShort.of(n);
        if (object instanceof Long n)
            return NbtLong.of(n);
        if (object instanceof String n)
            return NbtString.of(n);
        if (object instanceof Map<?,?> map) {
            NbtCompound compoundTag = new NbtCompound();
            for (Map.Entry<?, ?> entry : map.entrySet())
                if (entry.getKey() instanceof String key)
                    compoundTag.put(key,getTag(entry.getValue()));
            return compoundTag;
        }
        if (object instanceof UUID uuid)
            return NbtHelper.fromUuid(uuid);
        if (object instanceof List<?> list) {
            NbtList listTag = new NbtList();
            for (Object entry : list)
                listTag.add(getTag(entry));
            return listTag;
        }
        if (object instanceof Integer[] array)
            return new NbtIntArray(List.of(array));
        if (object instanceof Long[] array)
            return new NbtLongArray(List.of(array));
        if (object instanceof Byte[] array)
            return new NbtByteArray(List.of(array));
        return null;
    }
}
