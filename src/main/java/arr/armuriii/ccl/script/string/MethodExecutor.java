package arr.armuriii.ccl.script.string;

import java.util.List;

@FunctionalInterface
public interface MethodExecutor {
    Object execute(Object receiver, List<Object> args);
}
