package arr.armuriii.ccl.script.json;

import arr.armuriii.ccl.script.json.components.JsonComponent;
import arr.armuriii.ccl.script.json.components.JsonComponentFactory;
import arr.armuriii.ccl.script.json.components.JsonCompound;
import arr.armuriii.ccl.script.json.components.JsonNull;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class JsonScriptReader {
    public static Object evaluate(JsonObject jsonObject, @Nullable AbstractExtraData extraData) {
        Objects.requireNonNull(jsonObject);
        Map<String, JsonComponent<?>> variables = new HashMap<>();
        if (jsonObject.has(JsonComponentsImpl.VARIABLES_KEY))
            for (Map.Entry<String, JsonElement> entry : jsonObject.getAsJsonObject(JsonComponentsImpl.VARIABLES_KEY).asMap().entrySet())
                variables.put(entry.getKey(),getComponent(entry.getValue()));
        return getComponent(jsonObject.get(JsonComponentsImpl.SCRIPT_KEY))
                .apply(variables,extraData);
    }

    public static JsonComponent<?> getComponent(JsonElement element) {
        if (element == null)
            return new JsonNull();
        ArrayList<JsonComponent<?>> components = new java.util.ArrayList<>(List.of());
        for (JsonComponentFactory<? extends JsonComponent<?>> component : JsonComponentsImpl.REGISTERED_COMPONENTS.values().stream().toList())
            if (component.isCorrect(element))
                components.add(component.create(element));
        if (components.size() == 1)
            return components.get(0);
        if (components.size() > 1)
            return new JsonCompound(components);
        return new JsonNull();
    }

    public static JsonComponent<?> getComponent(JsonElement element, JsonComponent<?> jsonComponent) {
        return getComponent(element) instanceof JsonNull ? jsonComponent : getComponent(element);
    }
}
