package arr.armuriii.ccl.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class Pair<L,R> {

    private L left;
    private R right;
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L,R> Pair<L,R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    public JsonElement toJson() {
        return new JsonPrimitive(left+"-"+right);
    }
}
