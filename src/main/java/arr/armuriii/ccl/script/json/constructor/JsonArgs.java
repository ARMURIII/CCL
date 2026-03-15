package arr.armuriii.ccl.script.json.constructor;

import arr.armuriii.ccl.script.json.components.JsonComponent;

import java.util.HashMap;

public class JsonArgs extends HashMap<String,JsonComponent<?>> {

    public static JsonArgs empty() {
        return new JsonArgs();
    }
}
