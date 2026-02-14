package arr.armuriii.ccl.resources.script;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonScriptDataLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
    public static final Identifier ID = CustomCraftingLibrary.id("scripts");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String DIRECTORY = "scripts";

    public JsonScriptDataLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        HashMap<Identifier, JsonObject> scripts = JsonScript.scripts;

        CustomCraftingLibrary.LOGGER.info("Loading Json Scripts:");

        for (Map.Entry<Identifier, JsonElement> file : prepared.entrySet()) {
            try {
                if (!(file.getValue() instanceof JsonObject obj))
                    throw new JsonSyntaxException("Json File is not a JsonObject");
                scripts.put(file.getKey(), obj);
                CustomCraftingLibrary.LOGGER.info("Json Script [{}] added.", file.getKey());
            }
            catch (Exception e) {
                CustomCraftingLibrary.LOGGER.error(e.toString());
            }
        }

        if (scripts.isEmpty())
            CustomCraftingLibrary.LOGGER.info("No scripts have been loaded.");

        JsonScript.reload(scripts);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
