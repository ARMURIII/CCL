package arr.armuriii.ccl.util;

public enum ObjectOperator {
    ADDITION('+', 3, Double::sum, 0.0),
    SUBSTRACTION('-', 3, (a, b) -> a - b, 0.0f),
    MULTIPLICATION('*', 2, (a, b) -> a * b, 0.0f),
    DIVISION('/', 2, (a, b) -> {
        if (b == 0)
            throw new ArithmeticException(
                    "Cannot divide by zero");
        return a / b;
    }, 0.0f),
    MODULO('%', 2, (a, b) -> a % b, 0.0f),
    POWER('^', 1, Math::pow, 0.0),
    EQUALS('=', 4, (a, b) -> a == b, null),
    NOT_EQUALS('≠', 4, (a, b) -> a != b, null),
    GREATER('>', 4, (a, b) -> a > b, 0.0f),
    SMALLER('<', 4, (a, b) -> a < b, 0.0f),
    GREATER_OR_EQUAL('≥', 4, (a, b) -> a >= b, 0.0f),
    SMALLER_OR_EQUAL('≤', 4, (a, b) -> a <= b, 0.0f),
    NONE('\0', 0, (a, b) -> 0, 0);

    public final char c;
    public final Calculation calculation;
    public final Object defaultObject;
    public final int precedence;

    <T> ObjectOperator(char c, int precedence, Calculation<T> calculation, T defaultObject) {
        this.c = c;
        this.calculation = calculation;
        this.precedence = precedence;
        this.defaultObject = defaultObject;
    }

    public static ObjectOperator get(char c) {
        for (ObjectOperator value : values().clone())
            if (value.c == c) return value;
        return NONE;
    }

    public Object execute(Object a, Object b) {
        try{
            return this.calculation.execute(a, b);
        }catch (Exception ignored) {
            return this.defaultObject;
        }
    }

    @FunctionalInterface
    public interface Calculation<T> {
        Object execute(T a,T b);
    }
}
