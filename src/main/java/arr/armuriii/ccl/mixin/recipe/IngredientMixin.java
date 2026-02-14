package arr.armuriii.ccl.mixin.recipe;

import arr.armuriii.ccl.interfaces.ERange;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Ingredient.class)
public class IngredientMixin implements ERange {
    @Unique
    private int min = 1;
    @Unique
    private int max = 1;


    @WrapMethod(method = "fromJson(Lcom/google/gson/JsonElement;)Lnet/minecraft/recipe/Ingredient;")
    private static Ingredient exporting$getRange(JsonElement element, Operation<Ingredient> original) {
        Ingredient ingredient = original.call(element);
        if (element.isJsonObject()) {
            ingredient.CCL$min(JsonHelper.getInt(element.getAsJsonObject(),"min",1));
            ingredient.CCL$max(JsonHelper.getInt(element.getAsJsonObject(),"max",1));
        }
        return ingredient;
    }

    @WrapMethod(method = "fromJson(Lcom/google/gson/JsonElement;Z)Lnet/minecraft/recipe/Ingredient;")
    private static Ingredient exporting$getRange(JsonElement element, boolean bl, Operation<Ingredient> original) {
        Ingredient ingredient = original.call(element,bl);
        if (element.isJsonObject()) {
            ingredient.CCL$min(JsonHelper.getInt(element.getAsJsonObject(),"min",1));
            ingredient.CCL$max(JsonHelper.getInt(element.getAsJsonObject(),"max",1));
        }
        return ingredient;
    }

    @WrapMethod(method = "toJson")
    private JsonElement exporting$addRange(Operation<JsonElement> original) {
        JsonElement element = original.call();
        if (element.isJsonObject()) {
            element.getAsJsonObject().add("min", new JsonPrimitive(CCL$min()));
            element.getAsJsonObject().add("max", new JsonPrimitive(CCL$max()));
        }
        return element;
    }

    @WrapMethod(method = "fromPacket")
    private static Ingredient exporting$getRangeFromNetwork(PacketByteBuf friendlyByteBuf, Operation<Ingredient> original) {
        Ingredient ingredient = original.call(friendlyByteBuf);
        ingredient.CCL$min(friendlyByteBuf.readInt());
        ingredient.CCL$max(friendlyByteBuf.readInt());
        return ingredient;
    }

    @Inject(method = "write",at = @At("TAIL"))
    private void exporting$addRange(PacketByteBuf friendlyByteBuf, CallbackInfo ci) {
        friendlyByteBuf.writeInt(CCL$min());
        friendlyByteBuf.writeInt(CCL$max());
    }

    @Override
    public int CCL$min() {
        return this.min;
    }

    @Override
    public Ingredient CCL$min(int min) {
        this.min = min;
        return (Ingredient)(Object)this;
    }

    @Override
    public int CCL$max() {
        return this.max;
    }

    @Override
    public Ingredient CCL$max(int max) {
        this.max = max;
        return (Ingredient)(Object)this;
    }
}
