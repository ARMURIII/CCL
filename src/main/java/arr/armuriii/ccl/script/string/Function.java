package arr.armuriii.ccl.script.string;

import java.util.List;

// ! A function is any pre-defined method like 'Slot()'
public class Function {
    String name;
    int argCount;
    FunctionExecutor executor;

    public Function(String name, int argCount, FunctionExecutor executor) {
        this.name = name;
        this.argCount = argCount;
        this.executor = executor;
    }

    public Object call(List<Object> args) {
        if (args.size() != argCount && argCount != -1) {
            throw new IllegalArgumentException("Function " + name + " expects " + argCount + " args, got " + args.size());
        }
        return executor.execute(args);
    }
}
