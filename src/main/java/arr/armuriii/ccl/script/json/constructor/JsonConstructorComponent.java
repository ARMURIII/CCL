package arr.armuriii.ccl.script.json.constructor;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.components.JsonComponent;
import arr.armuriii.ccl.script.json.components.JsonComponentFactory;
import arr.armuriii.ccl.script.json.components.JsonLiteral;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import arr.armuriii.ccl.util.JsonConversionUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;

public class JsonConstructorComponent extends JsonComponent<Object> {

    public final String name;
    protected final Object value;

    public JsonConstructorComponent(String name, JsonConstructor<?> value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Object apply(Map<String, JsonComponent<?>> variables, AbstractExtraData extraData) {
        return value;
    }

    @Override
    public JsonElement write() {
        return null;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return null;
    }

    public static class Factory implements JsonComponentFactory<JsonConstructorComponent> {

        @Override
        public Class<Object> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("new");
        }

        @Override
        public JsonConstructorComponent create(JsonElement jsonElement) {
            if (!(jsonElement instanceof JsonObject jsonObject))
                return null;
            //return new JsonConstructorComponent("a",jsonObject.getAsJsonPrimitive("new").getAsString());
            return null;
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("literal");
        }
    }
}
