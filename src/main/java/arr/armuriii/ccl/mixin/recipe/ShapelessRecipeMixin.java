package arr.armuriii.ccl.mixin.recipe;

import arr.armuriii.ccl.util.ShapelessMatchingLogic;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin implements CraftingRecipe {
    @Shadow public abstract @NotNull DefaultedList<Ingredient> getIngredients();

    @WrapMethod(method = "matches(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/world/World;)Z")
    private boolean exporting$changeMatchingLogic(RecipeInputInventory container, World level, Operation<Boolean> original) {
        if (ShapelessMatchingLogic.shouldApply(this.getIngredients()))
            return ShapelessMatchingLogic.matches(container,this,level);
        return original.call(container, level);
    }
}
