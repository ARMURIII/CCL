package arr.armuriii.ccl.script.json.components;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import arr.armuriii.ccl.util.BooleanOperator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;

public class JsonBooleanLogic extends JsonComponent<Boolean> {

    protected final JsonComponent<?> left;
    protected final BooleanOperator logic;
    protected final JsonComponent<?> right;

    public JsonBooleanLogic(JsonComponent<?> left, BooleanOperator logic, JsonComponent<?> right) {
        this.left = left;
        this.logic = logic;
        this.right = right;
    }

    @Override
    public Boolean apply(Map<String, JsonComponent<?>> variables, AbstractExtraData extraData) {
        boolean n1 = left.apply(variables, extraData,Boolean.class);
        boolean n2 = right.apply(variables, extraData,Boolean.class);
        return logic.execute(n1, n2);
    }

    @Override
    public JsonElement write() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("logic", logic.toString());
        jsonObject.add("left",left.write());
        jsonObject.add("right",right.write());
        return jsonObject;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonBooleanLogic> {

        @Override
        public Class<Number> getReturnType() {
            return Number.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return (jsonElement instanceof JsonObject jsonObject &&
                    jsonObject.has("left") &&
                    jsonObject.has("logic") &&
                    allowsLogic(jsonObject.get("logic")) &&
                    jsonObject.has("right")) ||
                    (jsonElement instanceof JsonArray jsonArray && jsonArray.size() == 3 && allowsLogic(jsonArray.get(1)));
        }

        public static boolean allowsLogic(JsonElement element) {
            try{
                BooleanOperator.valueOf(element.getAsString().replace(' ','_').toUpperCase());
                return true;
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }

        @Override
        public JsonBooleanLogic create(JsonElement jsonElement) {
            if (jsonElement instanceof JsonObject jsonObject)
                return new JsonBooleanLogic(JsonScriptReader.getComponent(jsonObject.get("left")),
                        BooleanOperator.valueOf(jsonObject.get("logic").getAsString().replace(' ','_').toUpperCase()),
                        JsonScriptReader.getComponent(jsonObject.get("right")));
            if (jsonElement instanceof JsonArray jsonArray)
                return new JsonBooleanLogic(JsonScriptReader.getComponent(jsonArray.get(0)),
                        BooleanOperator.valueOf(jsonArray.get(1).getAsString().replace(' ','_').toUpperCase()),
                        JsonScriptReader.getComponent(jsonArray.get(2)));
            return null;
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("logic");
        }
    }
}
