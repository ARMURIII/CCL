package arr.armuriii.ccl.script.json.constructor;

import arr.armuriii.ccl.script.json.components.JsonComponent;
import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;

public interface JsonConstructorFactory<T> {
    T create(JsonElement jsonElement);
    Identifier getId();
}
