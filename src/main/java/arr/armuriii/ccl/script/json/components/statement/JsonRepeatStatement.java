package arr.armuriii.ccl.script.json.components.statement;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonComponentsImpl;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.components.JsonComponent;
import arr.armuriii.ccl.script.json.components.JsonComponentFactory;
import arr.armuriii.ccl.script.json.components.JsonLiteral;
import arr.armuriii.ccl.script.json.components.util.RepeatingUtils;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Range;

import java.util.Map;
import java.util.Objects;

public class JsonRepeatStatement extends JsonComponent<Object> {

    protected final JsonComponent<?> range;
    protected final JsonComponent<?> action;

    public JsonRepeatStatement(JsonComponent<?> range, JsonComponent<?> action) {
        this.range = range;
        this.action = action;
    }

    @Override
    public Object apply(Map<String,JsonComponent<?>> variables, AbstractExtraData extraData) {
        Range<Integer> integerRange = range.apply(variables,extraData, Range.class);
        String key = RepeatingUtils.getNextVar(variables);
        for (int i = integerRange.minInclusive(); i <= integerRange.maxInclusive(); i++) {
            variables.put(key, new JsonLiteral(i));
            var result = action.apply(variables,extraData);
            if (result != null) {
                variables.remove(key);
                return result;
            }
        }
        variables.remove(key);
        return null;
    }

    @Override
    public JsonElement write() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(JsonComponentsImpl.STATEMENT_KEY,"repeat");
        jsonObject.add("range",range.write());
        jsonObject.add("do",action.write());
        return jsonObject;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonRepeatStatement> {

        @Override
        public Class<Object> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return jsonElement instanceof JsonObject jsonObject && jsonObject.has(JsonComponentsImpl.STATEMENT_KEY) &&
                    Objects.equals(jsonObject.get(JsonComponentsImpl.STATEMENT_KEY).getAsString(), "repeat") &&
                    jsonObject.has("range") &&
                    jsonObject.has("do");
        }

        @Override
        public JsonRepeatStatement create(JsonElement jsonElement) {
            if (jsonElement instanceof JsonObject jsonObject)
                return new JsonRepeatStatement(
                        JsonScriptReader.getComponent(jsonObject.get("range")),
                        JsonScriptReader.getComponent(jsonObject.get("do"))
                );
            return null;
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("repeat");
        }
    }
}
