package arr.armuriii.ccl.script.string;

import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractScriptReader {
    protected final Returnable returnValue;
    protected Object returnableObject = null;
    int position;
    protected final Map<String, Function> functions = new HashMap<>();
    protected final Map<String, Method> methods = new HashMap<>();
    protected Map<String, Object> converter = new HashMap<>();
    public List<ItemStack> stacks = List.of();

    public AbstractScriptReader(char returnValue) {
        this.returnValue = Returnable.get(returnValue);
        this.position = 0;

        registerFunctions();
    }
    public AbstractScriptReader(char returnValue, RecipeInputInventory craftingContainer) {
        this.returnValue = Returnable.get(returnValue);
        this.position = 0;
        this.stacks = craftingContainer.getInputStacks();

        registerFunctions();
    }
    public AbstractScriptReader(char returnValue, List<ItemStack> stacks) {
        this.returnValue = Returnable.get(returnValue);
        this.position = 0;
        this.stacks = stacks;

        registerFunctions();
    }
    public AbstractScriptReader(char returnValue, List<ItemStack> stacks,Map<String,Object> converter) {
        this.returnValue = Returnable.get(returnValue);
        this.position = 0;
        this.stacks = stacks;
        this.converter = converter;

        registerFunctions();
    }

    protected void registerFunctions() {
        functions.put("if",new Function("if",3,this::ifStatement));

        functions.put("slot",new Function("slot",2,this::getSlot));
        functions.put("ingr",new Function("ingr",-1,this::getIngredient));

        new ScriptMethods(stacks).registerMethods(this.methods);
    }

    protected Object ifStatement(List<Object> args) {
        return args.get(0) instanceof Number n && n.doubleValue() == 1 ? args.get(1) : args.get(2);
    }

    protected Slot getSlot(List<Object> args) {
        return new Slot((Double) args.get(0), (Double) args.get(1));
    }

    protected Ingredient getIngredient(List<Object> args) {
        List<Ingredient.Entry> values = new ArrayList<>(List.of());
        for (Object object : args) {
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
        return Ingredient.ofEntries(values.stream());
    }

    public enum Returnable {
        STRING('S', false),
        BOOLEAN('B', false),
        INT('I', true),
        FLOAT('F', true),
        LIST('L', false),
        MAP('M', false),
        DOUBLE('D', true),
        CONSUMER('C', false),
        UNKNOWN('U', false);


        public final char c;
        private final boolean number;
        Returnable(char c, boolean number) {
            this.c = c;
            this.number = number;
        }

        public static Returnable get(char c) {
            for (Returnable value : values().clone())
                if (value.c == c) return value;
            return null;
        }

        public boolean isNumber() {
            return number;
        }
    }

    // ! A slot, via x y reference in gui
    public static class Slot {
        double x, y;

        public Slot(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Slot("+x+", "+y+")";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Slot slot && slot.x == this.x && slot.y == this.y;
        }
    }
}
