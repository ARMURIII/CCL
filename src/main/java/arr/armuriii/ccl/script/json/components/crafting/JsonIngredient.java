package arr.armuriii.ccl.script.json.components.crafting;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.components.JsonComponent;
import arr.armuriii.ccl.script.json.components.JsonComponentFactory;
import arr.armuriii.ccl.script.json.components.JsonLiteral;
import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import arr.armuriii.ccl.script.json.data.CraftingExtraData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.Map;

public class JsonIngredient extends JsonComponent<Ingredient> {

    protected final Ingredient ingredient;

    public JsonIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public Ingredient apply(Map<String, JsonComponent<?>> variables, AbstractExtraData extraData) {
        return ingredient;
    }

    @Override
    public JsonElement write() {
        return null;
    }

    @Override
    public boolean isCorrectData(AbstractExtraData extraData) {
        return extraData instanceof CraftingExtraData;
    }

    @Override
    public JsonComponentFactory<?> factory() {
        return new Factory();
    }

    public static class Factory implements JsonComponentFactory<JsonIngredient> {

        @Override
        public Class<Object> getReturnType() {
            return Object.class;
        }

        @Override
        public boolean isCorrect(JsonElement jsonElement) {
            return jsonElement instanceof JsonObject jsonObject && jsonObject.has("ingredient");
        }

        @Override
        public JsonIngredient create(JsonElement jsonElement) {
            if (!(jsonElement instanceof JsonObject jsonObject))
                return null;

            return new JsonIngredient(Ingredient.fromJson(jsonObject.get("ingredient")));
        }

        @Override
        public Identifier getId() {
            return CustomCraftingLibrary.id("ingredient");
        }
    }
}
