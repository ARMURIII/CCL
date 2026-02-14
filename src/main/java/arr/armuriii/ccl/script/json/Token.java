package arr.armuriii.ccl.script.json;

public class Token {
    protected final String string;
    public Token(String string) {
        this.string = string;
    }

    public int length() {
        return string.length();
    }

    public char charAt(int index) {
        return string.charAt(index);
    }

    public String getString() {
        return string;
    }
}
