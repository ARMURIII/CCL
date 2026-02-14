package arr.armuriii.ccl.script.string;

import arr.armuriii.ccl.resources.converter.JsonConverter;
import arr.armuriii.ccl.util.NbtPathUtils;
import arr.armuriii.ccl.util.TagConversionUtils;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.*;

public class ScriptMethods {

    public final List<ItemStack> stacks;
    public Map<String, Method> methodMap;

    ScriptMethods(List<ItemStack> stacks) {
        this.stacks = stacks;
        this.methodMap = new HashMap<>();
    }

    public void registerMethods(Map<String, Method> methods) {
        methods.put("size", new Method("size", 0, this::getSize));
        methods.put("get", new Method("get", 1, this::getEntry));

        methods.put("getSt", new Method("getSt", 0, this::getBySlot));
        methods.put("match", new Method("match", 0, this::getByIngredient));

        methods.put("nbt", new Method("nbt", 1, this::getTag));
        methods.put("id", new Method("id", 0, this::getId));
        methods.put("isIn", new Method("isIn", 0, this::isIn));
        methods.put("count", new Method("count", 0, this::getCount));
        methods.put("maxCount", new Method("maxCount", 0, this::getMaxCount));
        methods.put("edible", new Method("edible", 0, this::isEdible));
        methods.put("enchantable", new Method("enchantable", 0, this::isEnchantable));
        methods.put("enchanted", new Method("enchanted", 0, this::isEnchanted));
        methods.put("durability", new Method("durability", 0, this::getDurability));
        methods.put("maxDurability", new Method("maxDurability", 0, this::getMaxDurability));
        methods.put("rarity", new Method("rarity", 0, this::getRarity));
        methods.put("fireproof", new Method("fireproof", 0, this::isFireProof));

        methods.put("equals", new Method("equals", 1, this::equals));
        methods.put("null", new Method("null", 0, this::isNull));
        methods.put("or", new Method("or", 1, this::orDefault));
        methods.put("string", new Method("string", 0, this::toString));
        methods.put("either", new Method("either", 2, this::either));

        methods.put("yield", new Method("yield", 1, this::yield));

        methods.put("forEach", new Method("forEach", 1, this::forEach));
        methods.put("removeIf", new Method("removeIf", 1, this::removeIf));

        methods.put("convert", new Method("convert", 1, this::convert));
        methodMap = methods;
    }

    public Object applyToSelf(Object object, String s) {
        ScriptReader reader = new ScriptReader(s,'U',stacks,Map.of("self",object));
        return reader.replaceExpression();
    }

    public Object applyToEntry(Map.Entry<?,?> object, String s) {
        ScriptReader reader = new ScriptReader(s,'U',stacks,Map.of("key",object.getKey(),"value",object.getValue()));
        return reader.replaceExpression();
    }

    private ItemStack getBySlot(Object receiver, List<Object> ignored) {
        if (receiver instanceof ScriptReader.Slot slot)
            return stacks.get((int) (slot.x+slot.y*3));
        return null;
    }
    private ItemStack getByIngredient(Object receiver, List<Object> ignored) {
        if (receiver instanceof Ingredient ingr)
            for (ItemStack stack : stacks)
                if (ingr.test(stack))
                    return stack;
        return null;
    }

    private Object getTag(Object finder,List<Object> args) {
        ItemStack stack = getStack(finder);
        if (stack != null && args.get(0) instanceof String path)
            return TagConversionUtils.getValue(NbtPathUtils.maybeGet(path, stack.getOrCreateNbt()).orElse(null));
        return null;
    }

    private Object convert(Object receiver,List<Object> args) {
        if (receiver != null && args.get(0) instanceof String path)
            return JsonConverter.convert(Identifier.splitOn(path,':'),receiver).orElse(null);
        return null;
    }

    private List<Object> forEach(Object receiver,List<Object> args) {
        if (args.get(0) instanceof String s) {
            if (receiver instanceof Ingredient ingr) {
                List<ItemStack> matches = new ArrayList<>(stacks);
                matches.removeIf((stack -> !ingr.test(stack)));
                return matches.stream().map(stack -> applyToSelf(stack,s)).toList();
            }
            if (receiver instanceof List<?> list)
                return list.stream().map(stack -> applyToSelf(stack,s)).toList();
            if (receiver instanceof Map<?,?> map)
                return map.entrySet().stream().map(stack -> applyToEntry(stack,s)).toList();
        }
        return null;
    }

    private List<Object> removeIf(Object receiver,List<Object> args) {
        if (args.get(0) instanceof String s) {
            if (receiver instanceof Ingredient ingr) {
                List<Object> matches = new ArrayList<>(stacks);
                matches.removeIf((stack -> !ingr.test((ItemStack) stack)));
                matches.removeIf(stack -> applyToSelf(stack,s) instanceof Boolean bl && bl);
                return matches;
            }
            if (receiver instanceof List<?> collection) {
                List<Object> list = new ArrayList<>(collection);
                list.removeIf(entry -> applyToSelf(entry, s) instanceof Boolean bl && bl);
                return list;
            }
            if (receiver instanceof Map<?,?> collection) {
                List<Object> list = new ArrayList<>(collection.entrySet());
                list.removeIf(entry -> applyToEntry((Map.Entry<?,?>) entry, s) instanceof Boolean bl && bl);
                return list;
            }
        }
        return null;
    }

    private Object yield(Object receiver,List<Object> args) {
        if (args.get(0) instanceof String s) {
            if (receiver instanceof Ingredient ingr) {
                List<Object> matches = new ArrayList<>(stacks);
                matches.removeIf((stack -> !ingr.test((ItemStack) stack)));
                for (Object stack : matches)
                    if (applyToSelf(stack,s) instanceof Boolean bl && bl)
                        return stack;
            }
            if (receiver instanceof List<?> collection) {
                List<Object> list = new ArrayList<>(collection);
                for (Object entry : list)
                    if (applyToSelf(entry, s) instanceof Boolean bl && bl)
                        return entry;
            }
            if (receiver instanceof Map<?,?> collection) {
                List<Object> list = new ArrayList<>(collection.entrySet());
                for (Object entry : list)
                    if (applyToEntry((Map.Entry<?,?>) entry, s) instanceof Boolean bl && bl)
                        return entry;
            }
        }
        return null;
    }

    private String getId(Object finder,List<Object> ignored) {
        if (getStack(finder) != null)
            return Registries.ITEM.getId(getStack(finder).getItem()).toString();
        return null;
    }

    private Boolean isIn(Object finder,List<Object> args) {
        if (getStack(finder) != null && args.get(0) instanceof String s) {
            Identifier resourceLocation = new Identifier(s.replace("#",""));
            TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, resourceLocation);
            return getStack(finder).isIn(tagKey);
        }
        return null;
    }

    private Boolean isEdible(Object finder,List<Object> ignored) {
        if (getStack(finder) != null)
            return getStack(finder).isFood();

        return null;
    }

    private Boolean isEnchanted(Object finder,List<Object> ignored) {
        if (getStack(finder) != null)
            return getStack(finder).hasEnchantments();
        return null;
    }

    private Boolean isEnchantable(Object finder,List<Object> ignored) {
        if (getStack(finder) != null)
            return getStack(finder).isEnchantable();
        return null;
    }

    private int getDurability(Object finder,List<Object> ignored) {
        if (getStack(finder) != null)
            return getStack(finder).getMaxDamage()-getStack(finder).getDamage();
        return 0;
    }

    private int getMaxDurability(Object finder,List<Object> ignored) {
        if (getStack(finder) != null)
            return getStack(finder).getMaxDamage();
        return 0;
    }

    private int getCount(Object finder,List<Object> ignored) {
        if (getStack(finder) != null)
            return getStack(finder).getCount();
        return 0;
    }

    private int getMaxCount(Object finder,List<Object> ignored) {
        if (getStack(finder) != null)
            return getStack(finder).getMaxCount();
        return 0;
    }

    private String getRarity(Object finder,List<Object> ignored) {
        ItemStack stack = getStack(finder);
        if (stack != null) {
            return stack.getRarity().name();
        }
        return null;
    }

    private boolean isFireProof(Object finder,List<Object> ignored) {
        ItemStack stack = getStack(finder);
        if (stack != null)
            return stack.getItem().isFireproof();

        return false;
    }

    private ItemStack getStack(Object receiver) {
        if (receiver instanceof ItemStack stack1)
            return stack1;
        if (receiver instanceof ScriptReader.Slot slot)
            return getBySlot(slot,null);
        if (receiver instanceof Ingredient ingr)
            return getByIngredient(ingr,null);
        return null;
    }

    private Object getSize(Object finder, List<Object> args) {
        if (finder instanceof List<?> list)
            return list.size();
        if (finder instanceof Map<?,?> map)
            return map.size();
        if (finder instanceof Ingredient ingr) {
            List<ItemStack> matching = new ArrayList<>(stacks);
            matching.removeIf((stack -> !ingr.test(stack)));
            return matching.size();
        }
        return 0;
    }

    private Object getEntry(Object finder, List<Object> args) {
        if (finder instanceof List<?> list)
            return list.get((Integer) args.get(0));
        if (finder instanceof Map<?,?> map)
            return new ArrayList<>(map.entrySet()).get((Integer) args.get(0)).getValue();
        if (finder instanceof Ingredient ingr) {
            List<ItemStack> matching = new ArrayList<>(stacks);
            matching.removeIf((stack -> !ingr.test(stack)));
            return matching.get((Integer) args.get(0));
        }
        return null;
    }

    private double equals(Object receiver,List<Object> args) {
        if (receiver instanceof Boolean bool) {
            return args.get(0).equals(bool ? 1 : 0) ? 1 : 0; // so instead of true it's 1 so equality work 1==1
        }
        return receiver.equals(args.get(0)) ? 1 : 0;
    }

    private boolean isNull(Object receiver,List<Object> ignored) {
        return receiver == null;
    }

    private Object orDefault(Object receiver,List<Object> args) {
        return receiver == null ? args.get(0) : receiver;
    }

    private String toString(Object receiver,List<Object> ignored) {
        if (receiver instanceof Ingredient ingredient) {
            return Arrays.toString(ingredient.getMatchingStacks());
        }
        return receiver.toString();
    }

    private Object either(Object receiver,List<Object> args) {
        return receiver instanceof Number n && n.doubleValue() == 1 ? args.get(0) : args.get(1);
    }
}
