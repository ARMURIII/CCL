package arr.armuriii.ccl.resources.converter;

import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JsonConverter {
    static ConcurrentMap<Identifier, Map<Object,Object>> converter = new ConcurrentHashMap<>();

    public static void reload(ConcurrentMap<Identifier, Map<Object,Object>> newConverters) {
        converter.clear();
        converter = newConverters;
    }

    public static Optional<Object> convert(Identifier location,Object key) {
        for (var entry : converter.getOrDefault(location,Map.of()).entrySet())
            if (key.equals(entry.getKey()))
                return Optional.of(entry.getValue());
        return Optional.empty();
    }
}
