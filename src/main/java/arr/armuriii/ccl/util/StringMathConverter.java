package arr.armuriii.ccl.util;

import java.util.Stack;

public class StringMathConverter {

    /* algorithm from https://www.geeksforgeeks.org/java/how-to-evaluate-math-expression-given-in-string-form-in-java/
    *  all credits to them
    *  modified to accept booleans and negative numbers
    */

    public static double
    evaluateExpressionWithBoolean(String expression) {
        while (expression.contains("!1") || expression.contains("!0"))
            expression = expression.replace("!1","0").replace("!0","1");

        return evaluateExpression(expression
                .replace("!=","≠")
                .replace(">=","≥")
                .replace("<=","≤")
                .replace("==","=")
                .replace("&&","&")
                .replace("||","|") // yeah, this is dumb, but I'm too lazy to create 2 chars operations
        );
    }

    public static double
    evaluateExpression(String expression)
    {
        char[] tokens = expression.toCharArray();
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ')
                continue;
            if (Character.isDigit(tokens[i])
                    || tokens[i] == '.' || isNegativeSign(i,tokens)) {
                StringBuilder sb = new StringBuilder();
                while (i < tokens.length
                        && (Character.isDigit(tokens[i])
                        || tokens[i] == '.') || isNegativeSign(i,tokens)) {
                    sb.append(tokens[i]);
                    i++;
                }
                values.push(
                        Double.parseDouble(sb.toString()));
                i--;
            }
            else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            }
            else if (tokens[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperator(
                            operators.pop(), values.pop(),
                            values.pop()));
                }
                operators.pop();
            }
            else if (Operator.get(tokens[i]) != Operator.NONE) {
                while (!operators.isEmpty()
                        && hasPrecedence(tokens[i],
                        operators.peek())) {
                    values.push(applyOperator(
                            operators.pop(), values.pop(),
                            values.pop()));
                }
                operators.push(tokens[i]);
            }
        }
        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(),
                    values.pop(),
                    values.pop()));
        }
        return values.pop();
    }

    // '(-X' -> X is negative, '-X' -> X is negative, '*-X' -> X is negative, 'y-X' -> X is positive
    private static boolean isNegativeSign(int i, char[] tokens) {
        if (i >= tokens.length)
            return false;
        if (tokens[i] != '-')
            return false;
        if (i == 0)
            return true;
        return tokens[i-1] == '(' || Operator.get(tokens[i - 1]) != Operator.NONE;
    }
    
    private static boolean hasPrecedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')')
            return false;
        if (operator1 == '(' || operator1 == ')')
            return true;
        if (Operator.get(operator1) == null || Operator.get(operator2) == null )
            return false;
        if (Operator.get(operator1).precedence == Operator.get(operator2).precedence)
            return true;
        return Operator.get(operator1).precedence > Operator.get(operator2).precedence;
    }
    private static double applyOperator(char operator, double b, double a) {
        return Operator.get(operator) != Operator.NONE ? Operator.get(operator).execute(a,b) : 0;
    }
}
