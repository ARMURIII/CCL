package arr.armuriii.ccl.script.json;

import arr.armuriii.ccl.script.json.components.*;
import arr.armuriii.ccl.script.json.components.function.JsonRandomFunction;
import arr.armuriii.ccl.script.json.components.statement.JsonIfStatement;
import arr.armuriii.ccl.util.JsonConversionUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class JsonComponentInit {

    public static void registerComponents() {



        //primitives
        JsonComponentsImpl.registerComponent(new JsonLiteral.Factory());
        JsonComponentsImpl.registerComponent(new JsonVariable.Factory());
        JsonComponentsImpl.registerComponent(new JsonOperation.Factory());
        JsonComponentsImpl.registerComponent(new JsonBooleanLogic.Factory());

        //statements
        JsonComponentsImpl.registerComponent(new JsonIfStatement.Factory());

        //functions
        JsonComponentsImpl.registerComponent(new JsonRandomFunction.Factory());

        //constructors
        //JsonComponentsImpl.registerConstructor(new JsonConstructor<>(Ingredient::fromJson, CustomCraftingLibrary.id("Ingredient")));
        //JsonComponentsImpl.registerConstructor(new JsonConstructor<>(JsonComponentInit::ItemStackFromJson,CustomCraftingLibrary.id("ItemStack")));
    }

    private static ItemStack ItemStackFromJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject jsonObject) {
            ItemStack stack = new ItemStack(
                    Registries.ITEM.get(new Identifier(jsonObject.getAsJsonPrimitive("item").getAsString())),
                    JsonHelper.getInt(jsonObject,"count",1)
            );
            if (jsonObject.has("nbt")) {
                var nbt = JsonConversionUtils.readNbt(jsonObject.get("nbt"));
                stack.getItem().postProcessNbt(nbt);
                stack.setNbt(nbt);
            }
            if (stack.getItem().isDamageable())
                stack.setDamage(stack.getDamage());
            return stack;
        }
        return ItemStack.EMPTY;
    }
}
