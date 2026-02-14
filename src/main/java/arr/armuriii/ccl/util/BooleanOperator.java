package arr.armuriii.ccl.util;

public enum BooleanOperator {
    AND('&', (a, b) -> a && b),
    OR('|', (a, b) -> a || b),
    XOR('^', (a, b) -> (a || b) && !(a && b)),
    NONE('\0', (a, b) -> false);

    public final char c;
    public final Calculation calculation;

    BooleanOperator(char c, Calculation calculation) {
        this.c = c;
        this.calculation = calculation;
    }

    public static BooleanOperator get(char c) {
        for (BooleanOperator value : values().clone())
            if (value.c == c) return value;
        return NONE;
    }

    public boolean execute(boolean a, boolean b) {
        return this.calculation.execute(a, b);
    }

    @FunctionalInterface
    public interface Calculation {
        boolean execute(boolean a,boolean b);
    }
}
