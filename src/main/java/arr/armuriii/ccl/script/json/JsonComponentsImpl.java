package arr.armuriii.ccl.script.json;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.script.json.components.JsonComponent;
import arr.armuriii.ccl.script.json.components.JsonComponentFactory;
import arr.armuriii.ccl.script.json.constructor.JsonConstructorFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JsonComponentsImpl {

    public static final String SCRIPT_KEY = "script";
    public static final String VARIABLES_KEY = "variables";

    public static final String VARIABLE_KEY = "variable";

    public static final String STATEMENT_KEY = "statement";

    public static final String FUNCTION_KEY = "function";
    public static final String RECEIVER_KEY = "receiver";
    public static final String ARGUMENT_KEY = "args";

    public static final Map<Identifier, JsonComponentFactory<? extends JsonComponent<?>>> REGISTERED_COMPONENTS = new ConcurrentHashMap<>();

    public static final Map<Identifier, JsonConstructorFactory<?>> REGISTERED_CONSTRUCTORS = new ConcurrentHashMap<>();

    public static void registerComponent(JsonComponentFactory<? extends JsonComponent<?>> component) {
        Objects.requireNonNull(component.getId(), "JsonComponentFactory identifier may not be null.");

        if (REGISTERED_COMPONENTS.putIfAbsent(component.getId(), component) != null)
            throw new IllegalArgumentException("JsonComponentFactory with identifier " + component.getId() + " already registered.");
    }
    public static void registerConstructor(JsonConstructorFactory<?> constructor) {
        Objects.requireNonNull(constructor.getId(), "JsonConstructorFactory identifier may not be null.");

        if (REGISTERED_CONSTRUCTORS.putIfAbsent(constructor.getId(), constructor) != null)
            throw new IllegalArgumentException("JsonConstructorFactory with identifier " + constructor.getId() + " already registered.");
    }

    @Nullable
    public static JsonComponentFactory<?> getComponent(Identifier identifier) {
        Objects.requireNonNull(identifier, "Identifier may not be null.");
        return REGISTERED_COMPONENTS.get(identifier);
    }

    @Nullable
    public static JsonComponentFactory<?> getComponent(String s) {
        Objects.requireNonNull(s, "String may not be null.");
        return REGISTERED_COMPONENTS.get(CustomCraftingLibrary.id(s));
    }
}
