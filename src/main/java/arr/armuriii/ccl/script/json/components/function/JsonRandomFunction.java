package arr.armuriii.ccl.script.json.components.function;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonComponentsImpl;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.components.JsonComponent;
import arr.armuriii.ccl.script.json.components.JsonComponentFactory;
import arr.armuriii.ccl.script.json.components.JsonLiteral;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class JsonRandomFunction extends JsonComponent<Object> {

    protected final JsonComponent<?> min;
    protected final JsonComponent<?> max;

    public JsonRandomFunction(JsonComponent<?> min, JsonComponent<?> max) {
        this.min = min;
        this.max = max;
    }


    @Override
    public Object apply(Map<String,JsonComponent<?>> variables, AbstractExtraData extraData) {
        return new Random().nextInt(min.apply(variables, extraData, Integer.class),max.apply(variables, extraData, Integer.class)+1);
    }

    @Override
    public JsonElement write() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(JsonComponentsImpl.FUNCTION_KEY,"random");
        jsonObject.add("min",min.write());
        jsonObject.add("max",max.write());
        return jsonObject;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonRandomFunction> {

        @Override
        public Class<Object> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return jsonElement instanceof JsonObject jsonObject && jsonObject.has(JsonComponentsImpl.FUNCTION_KEY) &&
                    Objects.equals(jsonObject.get(JsonComponentsImpl.FUNCTION_KEY).getAsString(), "random");
        }

        @Override
        public JsonRandomFunction create(JsonElement jsonElement) {
            if (jsonElement instanceof JsonObject jsonObject)
                return new JsonRandomFunction(
                        JsonScriptReader.getComponent(jsonObject.get("min"),new JsonLiteral(0)),
                        JsonScriptReader.getComponent(jsonObject.get("max"),new JsonLiteral(1))
                );
            return null;
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("random");
        }
    }
}
