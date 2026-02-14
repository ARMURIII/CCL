package arr.armuriii.ccl.script.json.components.statement;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonComponentsImpl;
import arr.armuriii.ccl.script.json.components.JsonComponent;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.components.JsonComponentFactory;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;

public class JsonIfStatement extends JsonComponent<Object> {

    protected final JsonComponent<?> condition;
    protected final JsonComponent<?> action;
    protected final JsonComponent<?> fallback;

    public JsonIfStatement(JsonComponent<?> condition, JsonComponent<?> action, JsonComponent<?> fallback) {
        this.condition = condition;
        this.action = action;
        this.fallback = fallback;
    }

    @Override
    public Object apply(Map<String,JsonComponent<?>> variables, AbstractExtraData extraData) {
        return condition.apply(variables,extraData,Boolean.class) ?
                action.apply(variables,extraData) :
                fallback.apply(variables,extraData);
    }

    @Override
    public JsonElement write() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(JsonComponentsImpl.STATEMENT_KEY,"if");
        jsonObject.add("condition",condition.write());
        jsonObject.add("then",action.write());
        jsonObject.add("else",fallback.write());
        return jsonObject;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonIfStatement> {

        @Override
        public Class<Object> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return jsonElement instanceof JsonObject jsonObject && jsonObject.has(JsonComponentsImpl.STATEMENT_KEY) &&
                    Objects.equals(jsonObject.get(JsonComponentsImpl.STATEMENT_KEY).getAsString(), "if") &&
                    jsonObject.has("condition") &&
                    jsonObject.has("then") &&
                    jsonObject.has("else");
        }

        @Override
        public JsonIfStatement create(JsonElement jsonElement) {
            if (jsonElement instanceof JsonObject jsonObject)
                return new JsonIfStatement(
                        JsonScriptReader.getComponent(jsonObject.get("condition")),
                        JsonScriptReader.getComponent(jsonObject.get("then")),
                        JsonScriptReader.getComponent(jsonObject.get("else"))
                );
            return null;
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("if");
        }
    }
}
