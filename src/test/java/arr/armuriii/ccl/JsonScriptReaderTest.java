package arr.armuriii.ccl;

import arr.armuriii.ccl.script.json.JsonComponentInit;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonScriptReaderTest {

    @BeforeAll
    static void beforeAll() {
        JsonComponentInit.registerComponents();
    }

    @Test
    void testEvaluate() {
        for (int i = 0;i<10;i++){
            Object obj = JsonScriptReader.evaluate(getScript(), null);
            Assertions.assertEquals("5", obj.toString());
        }
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
                        function: random
                      },
                      "equals",
                      1
                    ],
                    then: @ponder,
                    else: 5
                  }
                }""",true);
    }
}
