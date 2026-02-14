package arr.armuriii.ccl.script.json.components;

import arr.armuriii.ccl.script.json.data.AbstractExtraData;
import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;

import java.util.Map;

public abstract class JsonComponent<R> {
    public JsonComponent() {}

    public abstract R apply(Map<String,JsonComponent<?>> variables, AbstractExtraData extraData);

    public <T> T apply(Map<String,JsonComponent<?>> variables, AbstractExtraData extraData, Class<T> clazz) {
        R value = apply(variables, extraData);
        if (factory().getReturnType().isAssignableFrom(clazz))
            return (T) value;
        if (value instanceof Boolean bl && clazz == Boolean.class)
            return (T) bl;
        return null;
    }

    public abstract JsonElement write();

    public abstract JsonComponentFactory<?> factory();

    public boolean isCorrectData(AbstractExtraData extraData) {
        return true;
    }
}
