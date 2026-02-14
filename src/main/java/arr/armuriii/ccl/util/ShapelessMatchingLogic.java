package arr.armuriii.ccl.util;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShapelessMatchingLogic {

    public static boolean shouldApply(List<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.CCL$min() != 1 || ingredient.CCL$max() != 1)
                return true;
        }
        return false;
    }

    public static boolean matches
            (
                    List<Ingredient> ingredients1,
                    List<ItemStack> stacks1,
                    World level
            )
    {
        List<Ingredient> ingredients = new ArrayList<>(ingredients1);
        ingredients.removeIf(Ingredient::isEmpty);
        ingredients.removeIf(Objects::isNull);
        List<ItemStack> stacks = new ArrayList<>(stacks1);
        stacks.removeIf(ItemStack::isEmpty);
        stacks.removeIf(Objects::isNull);
        int minSum = ingredients.stream().mapToInt(ShapelessMatchingLogic::min).sum();
        int maxSum = ingredients.stream().mapToInt(ShapelessMatchingLogic::max).sum();

        if (stacks.size() < minSum || stacks.size() > maxSum)
            return false;


        boolean[] used = new boolean[stacks.size()];
        return backtrack(ingredients, stacks, used, 0, 0);
    }
    public static boolean matches(RecipeInputInventory craftingContainer, CraftingRecipe recipe, World level) {
        return matches(recipe.getIngredients(),craftingContainer.getInputStacks(),level);
    }

    private static boolean backtrack(
            List<Ingredient> ingredients,
            List<ItemStack> objects,
            boolean[] used,
            int index,
            int usedCount
    ) {
        if (index == ingredients.size())
            return usedCount == objects.size();

        Ingredient ingredient = ingredients.get(index);

        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            if (!used[i] && ingredient.test(objects.get(i))) {
                candidates.add(i);
            }
        }

        for (int k = min(ingredient); k <= max(ingredient); k++) {
            if (k > candidates.size()) {
                break;
            }

            if (chooseSubset(
                    ingredients,
                    objects,
                    used,
                    candidates,
                    index,
                    usedCount,
                    0,
                    k,
                    new ArrayList<>()
            )) {
                return true;
            }
        }
        return false;
    }

    private static boolean chooseSubset(
            List<Ingredient> ingredients,
            List<ItemStack> stacks,
            boolean[] used,
            List<Integer> candidates,
            int predicateIndex,
            int usedCount,
            int start,
            int remaining,
            List<Integer> chosen
    ) {
        if (remaining == 0) {
            // Apply choice
            for (int idx : chosen) {
                used[idx] = true;
            }

            boolean success = backtrack(
                    ingredients,
                    stacks,
                    used,
                    predicateIndex + 1,
                    usedCount + chosen.size()
            );

            for (int idx : chosen) {
                used[idx] = false;
            }

            return success;
        }

        for (int i = start; i <= candidates.size() - remaining; i++) {
            chosen.add(candidates.get(i));
            if (chooseSubset(
                    ingredients,
                    stacks,
                    used,
                    candidates,
                    predicateIndex,
                    usedCount,
                    i + 1,
                    remaining - 1,
                    chosen
            )) {
                return true;
            }
            chosen.remove(chosen.size() - 1);
        }

        return false;
    }

    private static int max(Ingredient ingredient) {
        return ingredient.CCL$max();
    }

    private static int min(Ingredient ingredient) {
        return ingredient.CCL$min();
    }
}
