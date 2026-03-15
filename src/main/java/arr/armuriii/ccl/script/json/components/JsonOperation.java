package arr.armuriii.ccl.script.json.components;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import arr.armuriii.ccl.util.BooleanOperator;
import arr.armuriii.ccl.util.ObjectOperator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;

public class JsonOperation extends JsonComponent<Object> {

    protected final JsonComponent<?> left;
    protected final ObjectOperator operator;
    protected final JsonComponent<?> right;

    public JsonOperation(JsonComponent<?> left, ObjectOperator operator, JsonComponent<?> right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object apply(Map<String, JsonComponent<?>> variables, AbstractExtraData extraData) {
        Object n1 = left.apply(variables, extraData);
        Object n2 = right.apply(variables, extraData);
        if (n1 instanceof Integer i1 && n2 instanceof Integer i2 && operator.execute(i1, i2) instanceof Number number)
            return Math.round(number.floatValue());
        return operator.execute(n1, n2);
    }

    @Override
    public JsonElement write() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operator",operator.toString());
        jsonObject.add("left",left.write());
        jsonObject.add("right",right.write());
        return jsonObject;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonOperation> {

        @Override
        public Class<Number> getReturnType() {
            return Number.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return (jsonElement instanceof JsonObject jsonObject &&
                    jsonObject.has("left") &&
                    jsonObject.has("operator") &&
                    allowsOperator(jsonObject.getAsJsonPrimitive("operator")) &&
                    jsonObject.has("right")) ||
                    (jsonElement instanceof JsonArray jsonArray && jsonArray.size() == 3 && allowsOperator(jsonArray.get(1)));
        }

        public static boolean allowsOperator(JsonElement element) {
            try{
                ObjectOperator.valueOf(element.getAsString().replace(' ','_').toUpperCase());
                return true;
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }

        @Override
        public JsonOperation create(JsonElement jsonElement) {
            if (jsonElement instanceof JsonObject jsonObject)
                return new JsonOperation(JsonScriptReader.getComponent(jsonObject.get("left")),
                        ObjectOperator.valueOf(jsonObject.get("operator").getAsString().replace(' ','_').toUpperCase()),
                        JsonScriptReader.getComponent(jsonObject.get("right")));
            if (jsonElement instanceof JsonArray jsonArray)
                return new JsonOperation(JsonScriptReader.getComponent(jsonArray.get(0)),
                        ObjectOperator.valueOf(jsonArray.get(1).getAsString().replace(' ','_').toUpperCase()),
                        JsonScriptReader.getComponent(jsonArray.get(2)));
            return null;
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("operation");
        }
    }
}
