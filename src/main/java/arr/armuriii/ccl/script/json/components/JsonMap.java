package arr.armuriii.ccl.script.json.components;

import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Map;

public class JsonMap extends JsonComponent<Object> {
    public final Map<String,JsonComponent<?>> components;

    public JsonMap(Map<String,JsonComponent<?>> components) {
        this.components = components;
    }

    @Override
    public Object apply(Map<String, JsonComponent<?>> variables, AbstractExtraData extraData) {
        for (JsonComponent<?> component : components.values()) {
            Object obj = component.apply(variables, extraData);
            if (obj != null)
                return obj;
        }
        return null;
    }

    @Override
    public <T> T apply(Map<String,JsonComponent<?>> variables, AbstractExtraData extraData, Class<T> clazz) {
        for (JsonComponent<?> component : components.values()) {
            T obj = component.apply(variables, extraData,clazz);
            if (obj != null)
                return obj;
        }
        return null;
    }

    @Override
    public JsonElement write() {
        return null;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return null;
    }
}
