package arr.armuriii.ccl.script.lua;

import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContainerAPI extends TwoArgFunction {

    private final List<ItemStack> stacks;

    public ContainerAPI(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set( "stack_of_index", new getStackOfIndex());
        library.set( "stack_by_ingr", new getStackByIngredient());
        env.set( "ccl-container", library );
        return library;
    }
    class getStackOfIndex extends OneArgFunction {
        public LuaTable call(LuaValue index) {
            return LuaConversionUtils.fromItemStack(stacks.get(index.checkint()));
        }
    }
    class getStackByIngredient extends OneArgFunction {
        public LuaTable call(LuaValue value) {
            List<Object> list = (List<Object>) LuaConversionUtils.getValue(value.checktable());
            List<Ingredient.Entry> values = new ArrayList<>(List.of());
            Objects.requireNonNull(list);
            for (Object object : list) {
                if (object instanceof String string)
                    if (string.startsWith("#")) {
                        Identifier resourceLocation = new Identifier(string.replace("#",""));
                        TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, resourceLocation);
                        values.add(new Ingredient.TagEntry(tagKey));
                    }else {
                        Item item = Registries.ITEM.getOrEmpty(Identifier.tryParse(string))
                                .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
                        values.add(new Ingredient.StackEntry(item.getDefaultStack()));
                    }
            }
            Ingredient ingredient = Ingredient.ofEntries(values.stream());
            List<LuaTable> passedStacks = new ArrayList<>(stacks).stream().filter(ingredient).map(LuaConversionUtils::fromItemStack).toList();
            return new LuaTable(new LuaValue[0],passedStacks.toArray(new LuaValue[0]),null);
        }
    }
}