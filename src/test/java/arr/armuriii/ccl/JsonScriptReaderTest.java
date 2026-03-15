package arr.armuriii.ccl;

import arr.armuriii.ccl.script.json.JsonComponentInit;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;

public class JsonScriptReaderTest {

    @BeforeAll
    static void beforeAll() {
        JsonComponentInit.registerComponents();
    }

    @Test
    void testEvaluate() {
        Assertions.assertArrayEquals(
                new String[]{
                "minecraft:ponder",
                "minecraft:ponder",
                "minecraft:ponder",
                "minecraft:ponder",
                "minecraft:ponder",
                "minecraft:ponder",
                "minecraft:ponder"
        },
                new String[]{
                JsonScriptReader.evaluate(getScript(), null).toString(),
                JsonScriptReader.evaluate(getScript(), null).toString(),
                JsonScriptReader.evaluate(getScript(), null).toString(),
                JsonScriptReader.evaluate(getScript(), null).toString(),
                JsonScriptReader.evaluate(getScript(), null).toString(),
                JsonScriptReader.evaluate(getScript(), null).toString(),
                JsonScriptReader.evaluate(getScript(), null).toString()
        });
    }

    static JsonObject getScript() {
        return JsonHelper.deserialize("""
                {
                  variables: {
                    ponder: "\\"minecraft:ponder\\""
                  },
                  script: {
                    statement: if,
                    condition: [
                      {
                        function: random,
                        min: 0,
                        max: 0
                      },
                      "EQUALS",
                      0
                    ],
                    then: @ponder,
                    else: 5
                  }
                }""",true);
    }
}
