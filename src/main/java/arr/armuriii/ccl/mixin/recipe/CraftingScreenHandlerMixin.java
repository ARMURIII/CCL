package arr.armuriii.ccl.mixin.recipe;

import arr.armuriii.ccl.script.ScriptFinder;
import com.google.gson.JsonSyntaxException;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.registry.Registries;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

    @ModifyExpressionValue(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/CraftingRecipe;craft(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack exporting$scriptSupport(ItemStack original, @Local(argsOnly = true) RecipeInputInventory container) {
        try {
            NbtElement resultTag = ScriptFinder.checkScript(original.getOrCreateNbt(),container.getInputStacks());
            if (resultTag instanceof NbtCompound resultNbt && resultNbt.contains("id", NbtCompound.STRING_TYPE))
                original = new ItemStack(Registries.ITEM.get(new Identifier(resultNbt.getString("id"))));

            if (resultTag instanceof NbtCompound resultNbt)
                original.setNbt(resultNbt);

            if (resultTag instanceof NbtCompound resultNbt && resultNbt.contains("Count", NbtCompound.INT_TYPE))
                original.setCount(resultNbt.getInt("Count"));
            return original;
        } catch (Exception e) {
            throw new JsonSyntaxException("Script failed: "+e);
        }
    }
}
