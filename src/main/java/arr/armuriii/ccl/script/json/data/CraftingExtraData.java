package arr.armuriii.ccl.script.json.data;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftingExtraData extends AbstractExtraData {

    public final List<ItemStack> inventory;

    public CraftingExtraData(List<ItemStack> inventory) {
        List<ItemStack> emptiness = new ArrayList<>(inventory);
        emptiness.removeIf(ItemStack::isEmpty);
        this.inventory = emptiness;
    }
    public CraftingExtraData() {
        this.inventory = List.of();
    }

    @Override
    public Object[] getAllData() {
        return inventory.toArray();
    }

    @Override
    public Object getFirstData() {
        return inventory.get(0);
    }
}
