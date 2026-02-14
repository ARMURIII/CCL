package arr.armuriii.ccl.mixin.recipe.Json;

import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(JsonHelper.class)
public class JsonHelperMixin {
    @ModifyArg(method = {"deserializeNullable(Lcom/google/gson/Gson;Ljava/io/Reader;Ljava/lang/Class;Z)Ljava/lang/Object;","deserializeNullable(Lcom/google/gson/Gson;Ljava/io/Reader;Lcom/google/gson/reflect/TypeToken;Z)Ljava/lang/Object;"}
    ,at = @At(value = "INVOKE", target = "Lcom/google/gson/stream/JsonReader;setLenient(Z)V"))
    private static boolean ccl$alwaysLenient(boolean lenient) {
        return true;
    }
}
