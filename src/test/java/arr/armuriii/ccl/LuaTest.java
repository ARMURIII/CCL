package arr.armuriii.ccl;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaTest {

    static String[] paths = {
            "C:/",
            "Users/",
            "cleme/",
            "Desktop/",
            "CCL/",
            "src/",
            "test/",
            "java/",
            "arr/",
            "armuriii/",
            "ccl/",
            "Test.lua"
    };

    static @NotNull String getPath(int i) {
        StringBuilder bld = new StringBuilder();
        for (int j = paths.length-1-i; j < paths.length; j++) {
            bld.append(paths[j]);
        }
        return bld.toString();
    }

    @Test
    void TestLua() {
        Globals glb =  JsePlatform.standardGlobals();
        //glb.load();
        LuaValue file = null;
        for (int i = 0; i < paths.length; i++) {
            try {
                file = glb.loadfile(getPath(i));
            }catch (Exception ignored) {
                continue;
            }
            System.out.println(getPath(i));
            break;
        }
        LuaValue craft = file.invoke().arg1();
        Assertions.assertEquals(5, craft.checkint());
    }
}
