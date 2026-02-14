package arr.armuriii.ccl.script.json.components;

import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import com.google.gson.JsonElement;

import java.util.Map;

public class JsonNull extends JsonComponent<Object> {
    @Override
    public Object apply(Map<String, JsonComponent<?>> variables, AbstractExtraData extraData) {
        return 5;
    }

    @Override
    public JsonElement write() {
        return com.google.gson.JsonNull.INSTANCE;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return null;
    }
}
