package arr.armuriii.ccl.resources.script;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Optional;

public class JsonScript {
    public static HashMap<Identifier, JsonObject> scripts = new HashMap<>();

    public static void reload(HashMap<Identifier, JsonObject> newScripts) {
        scripts = newScripts;
        CustomCraftingLibrary.LOGGER.info(scripts.toString());
    }

    public static Optional<JsonObject> getScript(Identifier location) {
        return Optional.ofNullable(scripts.get(location));
    }
}
