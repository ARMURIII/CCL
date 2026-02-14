package arr.armuriii.ccl.script.json.components;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonComponentsImpl;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.Token;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import arr.armuriii.ccl.util.JsonConversionUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;

public class JsonVariable extends JsonComponent<Object> {

    protected final JsonComponent<?> key;

    public JsonVariable(JsonComponent<?> key) {
        this.key = key;
    }

    @Override
    public Object apply(Map<String, JsonComponent<?>> variables, AbstractExtraData extraData) {
        if (key instanceof JsonNull)
            return null;
        return variables.get(key.apply(variables,extraData, Token.class).getString()).apply(variables,extraData);
    }

    @Override
    public JsonElement write() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(JsonComponentsImpl.VARIABLE_KEY,key.write());
        return jsonObject;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonVariable> {

        @Override
        public Class<Object> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return jsonElement.isJsonPrimitive() || (jsonElement instanceof JsonObject jsonObject && jsonObject.has(JsonComponentsImpl.VARIABLE_KEY));
        }

        @Override
        public JsonVariable create(JsonElement jsonElement) {
            if (jsonElement instanceof JsonObject obj)
                return new JsonVariable(JsonScriptReader.getComponent(obj.get(JsonComponentsImpl.VARIABLE_KEY)));
            else if (jsonElement.isJsonPrimitive() && jsonElement.getAsString().startsWith("@"))
                return new JsonVariable(new JsonLiteral(new Token(jsonElement.getAsString().replaceFirst("@",""))));
            return new JsonVariable(new JsonNull());
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("variable");
        }
    }
}
