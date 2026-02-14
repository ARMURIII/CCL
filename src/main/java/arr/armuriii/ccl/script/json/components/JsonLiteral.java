package arr.armuriii.ccl.script.json.components;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.Token;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import arr.armuriii.ccl.util.JsonConversionUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;

public class JsonLiteral extends JsonComponent<Object> {

    protected final Object value;

    public JsonLiteral(Object value) {
        this.value = value;
    }

    @Override
    public Object apply(Map<String,JsonComponent<?>> variables, AbstractExtraData extraData) {
        return value instanceof Token ? null : value;
    }

    public <T> T apply(Map<String,JsonComponent<?>> variables, AbstractExtraData extraData, Class<T> clazz) {
        if (factory().getReturnType().isAssignableFrom(clazz))
            return (T) value;
        if (value instanceof Boolean bl && clazz == Boolean.class)
            return (T) bl;
        return null;
    }

    @Override
    public JsonElement write() {
        return JsonConversionUtils.getJson(value);
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonLiteral> {

        @Override
        public Class<Object> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return jsonElement.isJsonPrimitive() || (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("literal"));
        }

        @Override
        public JsonLiteral create(JsonElement jsonElement) {
            if (jsonElement instanceof JsonObject obj)
                return new JsonLiteral(JsonConversionUtils.getValue(obj.get("literal")));
            else
                return new JsonLiteral(JsonConversionUtils.getValue(jsonElement));
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("literal");
        }
    }
}
