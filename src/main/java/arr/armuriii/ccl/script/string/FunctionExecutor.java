package arr.armuriii.ccl.script.string;

import java.util.List;

@FunctionalInterface
public interface FunctionExecutor {
    Object execute(List<Object> args);
}
