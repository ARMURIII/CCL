package arr.armuriii.ccl.util;

public enum Operator {
    ADDITION('+', 3, Double::sum),
    SUBSTRACTION('-', 3, (a, b) -> a - b),
    MULTIPLICATION('*', 2, (a, b) -> a * b),
    DIVISION('/', 2, (a, b) -> {
        if (b == 0)
            throw new ArithmeticException(
                    "Cannot divide by zero");
        return a / b;
    }),
    MODULO('%', 2, (a, b) -> a % b),
    POWER('^', 1, Math::pow),
    AND('&', 4, (a, b) -> (a == 1 && b == 1) ? 1 : 0),
    OR('|', 4, (a, b) -> (a == 1 || b == 1) ? 1 : 0),
    EQUAL('=', 4, (a, b) -> a == b ? 1 : 0),
    NOT_EQUAL('≠', 4, (a, b) -> a != b ? 1 : 0),
    GREATER('>', 4, (a, b) -> a > b ? 1 : 0),
    SMALLER('<', 4, (a, b) -> a < b ? 1 : 0),
    GREATER_EQUAL('≥', 4, (a, b) -> a >= b ? 1 : 0),
    SMALLER_EQUAL('≤', 4, (a, b) -> a <= b ? 1 : 0),
    NONE('\0', 0, (a, b) -> 0);

    public final char c;
    public final Calculation calculation;
    public final int precedence;

    Operator(char c, int precedence, Calculation calculation) {
        this.c = c;
        this.calculation = calculation;
        this.precedence = precedence;
    }

    public static Operator get(char c) {
        for (Operator value : values().clone())
            if (value.c == c) return value;
        return NONE;
    }

    public double execute(double a, double b) {
        return this.calculation.execute(a, b);
    }

    @FunctionalInterface
    public interface Calculation {
        double execute(double a,double b);
    }
}
