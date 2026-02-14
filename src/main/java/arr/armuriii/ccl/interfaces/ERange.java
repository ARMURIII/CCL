package arr.armuriii.ccl.interfaces;


import net.minecraft.recipe.Ingredient;

public interface ERange {
    default int CCL$min() {
        throw new UnsupportedOperationException();
    }

    default int CCL$max() {
        throw new UnsupportedOperationException();
    }
    @SuppressWarnings("UnusedReturnValue")
    default Ingredient CCL$min(int min) {
        throw new UnsupportedOperationException();
    }
    @SuppressWarnings("UnusedReturnValue")
    default Ingredient CCL$max(int max) {
        throw new UnsupportedOperationException();
    }
}
