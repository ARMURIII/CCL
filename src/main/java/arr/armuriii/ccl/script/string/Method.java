package arr.armuriii.ccl.script.string;

import java.util.List;

// ! A method is any pre-defined method with a receiver like 'X.equals()'
public class Method {
    String name;
    int argCount;
    MethodExecutor executor;

    public Method(String name, int argCount, MethodExecutor executor) {
        this.name = name;
        this.argCount = argCount;
        this.executor = executor;
    }

    public Object call(Object receiver, List<Object> args) {
        if (args.size() != argCount && argCount != -1) {
            throw new IllegalArgumentException("Method " + name + " expects " + argCount + " args, got " + args.size());
        }
        return executor.execute(receiver, args);
    }
}
