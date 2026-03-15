package arr.armuriii.ccl.script.json.components.util;

import arr.armuriii.ccl.script.json.components.JsonComponent;
import net.minecraft.util.NameGenerator;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RepeatingUtils {
    public static final List<String> REPEAT_KEYS = List.of("i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z");

    public static String getNextVar(Map<String, JsonComponent<?>> variables) {
        for (String key : REPEAT_KEYS) {
            if (!variables.containsKey(key))
                return key;
        }
        return NameGenerator.name(UUID.randomUUID()); // we do a little trolling, I just hate null default value
    }
}
