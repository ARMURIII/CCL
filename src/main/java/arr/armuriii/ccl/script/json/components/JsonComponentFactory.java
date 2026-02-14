package arr.armuriii.ccl.script.json.components;

import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;

public interface JsonComponentFactory<T extends JsonComponent<?>> {
    boolean isCorrect(JsonElement jsonElement);
    T create(JsonElement jsonElement);
    Identifier getId();
    Class<?> getReturnType();
}
