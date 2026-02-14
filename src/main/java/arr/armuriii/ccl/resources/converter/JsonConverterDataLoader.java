package arr.armuriii.ccl.resources.converter;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.util.JsonConversionUtils;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class JsonConverterDataLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {
    public static final Identifier ID = CustomCraftingLibrary.id("converters");

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final String DIRECTORY = "converters";

    public JsonConverterDataLoader() {
        super(GSON, DIRECTORY);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ConcurrentMap<Identifier, Map<Object,Object>> converters = JsonConverter.converter;

        CustomCraftingLibrary.LOGGER.info("Loading converters:");

        for (Map.Entry<Identifier, JsonElement> file : prepared.entrySet()) {
            try {
                JsonObject jsonObject = file.getValue().getAsJsonObject();
                boolean replace = JsonHelper.getBoolean(jsonObject,"replace",false);

                Map<Object, Object> map = replace ?
                        new HashMap<>() :
                        converters.getOrDefault(file.getKey(),new HashMap<>());

                addEntries(jsonObject, map);

                converters.put(file.getKey(),map);
                CustomCraftingLibrary.LOGGER.info("Converter [{}] added.", file.getKey());
            }
            catch (Exception e) {
                CustomCraftingLibrary.LOGGER.error(e.toString());
            }
        }

        if (converters.isEmpty())
            CustomCraftingLibrary.LOGGER.info("No converters have been loaded.");

        JsonConverter.reload(converters);
    }

    private void addEntries(JsonObject jsonObject, Map<Object,Object> map) {
        if (jsonObject.has("compact"))
            addCompactEntries(jsonObject,map);
        else if (jsonObject.has("from") && jsonObject.has("to"))
            addFromToEntries(jsonObject,map);
        else
            throw new JsonSyntaxException("no Converter were found, an array list of either 'compact' or 'from' & 'to' are required.");
    }

    private void addFromToEntries(JsonObject jsonObject, Map<Object,Object> map) {
        JsonArray from = jsonObject.getAsJsonArray("from");
        JsonArray to = jsonObject.getAsJsonArray("to");
        List<Object> fromList = from.asList().stream().map(JsonConversionUtils::getValue).toList();
        List<Object> toList = to.asList().stream().map(JsonConversionUtils::getValue).toList();
        if (fromList.isEmpty())
            throw new IllegalArgumentException("'from' cannot be empty.");
        if (toList.isEmpty())
            throw new IllegalArgumentException("'to' cannot be empty.");
        if (fromList.size() != toList.size())
            throw new IllegalArgumentException("'from' and 'to' cannot have different sizes.");

        for (int i = 0; i < fromList.size(); i++)
            map.put(fromList.get(i),toList.get(i));
    }

    private void addCompactEntries(JsonObject jsonObject, Map<Object,Object> map) {
        JsonArray compact = jsonObject.getAsJsonArray("compact");
        List<Object> compactList = compact.asList().stream().map(JsonConversionUtils::getValue).toList();
        if (compactList.isEmpty())
            throw new IllegalArgumentException("'compact' cannot be empty.");
        if (compactList.size() % 2 != 0)
            throw new IllegalArgumentException("'compact' cannot have an odd size");

        for (int i = 0; i < compactList.size(); i+=2)
            map.put(compactList.get(i),compactList.get(i+1));
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
