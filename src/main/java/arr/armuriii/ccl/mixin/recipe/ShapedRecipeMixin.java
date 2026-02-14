package arr.armuriii.ccl.mixin.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static arr.armuriii.ccl.util.JsonConversionUtils.readNbt;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {

    @ModifyReturnValue(method = "outputFromJson", at = @At("RETURN"))
    private static ItemStack exporting$addNbt(ItemStack original, JsonObject jsonObject) {
        if (jsonObject.has("nbt"))
            original.setNbt(readNbt(jsonObject.get("nbt")));
        return original;
    }
}
