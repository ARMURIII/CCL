package arr.armuriii.ccl.script.json.constructor;

import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class JsonConstructor<R> implements JsonConstructorFactory<R> {

    protected final Function<JsonElement,R> function;
    protected final Identifier identifier;

    public JsonConstructor(Function<JsonElement, R> function, Identifier identifier) {
        this.function = function;
        this.identifier = identifier;
    }

    @Override
    public R create(JsonElement jsonElement) {
        return function.apply(jsonElement);
    }

    @Override
    public Identifier getId() {
        return identifier;
    }
}
