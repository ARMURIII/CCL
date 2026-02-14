package arr.armuriii.ccl;

import arr.armuriii.ccl.util.TagConversionUtils;
import arr.armuriii.ccl.script.ScriptFinder;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ScriptReaderTest {

    @BeforeAll
    static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    List<ItemStack> setup() {
        List<Item> items = List.of(
                Items.END_STONE,Items.SILVERFISH_SPAWN_EGG,Items.ENCHANTING_TABLE,
                Items.ACACIA_BOAT,Items.COOKED_BEEF,Items.GHAST_TEAR,
                Items.OAK_BOAT,Items.SOUL_CAMPFIRE,Items.END_STONE
        );
        List<ItemStack> stacks = new java.util.ArrayList<>(List.of());
        for (int i = 0; i < 9; i++)
            stacks.add(setNbt(items.get(i).getDefaultStack()));
        return stacks;
    }

    @Test
    void testScript() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString(CustomCraftingLibrary.id("script").toTranslationKey(),"ingr(\"minecraft:end_stone\")" +
                ".forEach({" +
                "self.nbt(\"compound.SoulProfile.Name\");" +
                "});");
        nbt.putString(CustomCraftingLibrary.id("return-type").toTranslationKey(),"L");
        NbtElement tag = ScriptFinder.checkScript(nbt,setup());
        Assertions.assertEquals(List.of("ME","ME"), TagConversionUtils.getValue(tag));
    }
    static ItemStack setNbt(@NotNull ItemStack stack) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("base",6);
        nbt.putString("string","whip'n Nae Nae");
        nbt.putInt("x",2);
        nbt.putInt("y",11);
        NbtCompound soulProfile = new NbtCompound();
        soulProfile.putString("Name","ME");
        if (stack.getItem() == Items.END_STONE)
            nbt.put("SoulProfile",soulProfile);
        stack.getOrCreateNbt().put("compound",nbt);
        return stack;
    }
}
