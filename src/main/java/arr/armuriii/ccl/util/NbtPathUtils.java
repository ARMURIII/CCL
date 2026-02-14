package arr.armuriii.ccl.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;

import java.util.List;
import java.util.Optional;

import static net.minecraft.command.argument.NbtPathArgumentType.nbtPath;

public class NbtPathUtils {

    public static NbtPathArgumentType.NbtPath createPath(String s) {
        try {
            return nbtPath().parse(new StringReader(s));
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    public static Optional<NbtElement> maybeGet(NbtPathArgumentType.NbtPath path, NbtElement nbt) {
        if (path == null) return Optional.empty();
        List<NbtElement> tag;
        try {
            tag = path.get(nbt);
        } catch (CommandSyntaxException e) {
            return Optional.empty();
        }
        if (tag.size() == 1) {
            return Optional.ofNullable(tag.get(0));
        }else {
            NbtList listTag = new NbtList();
            listTag.addAll(tag);
            return Optional.of(listTag);
        }
    }

    public static Optional<NbtElement> maybeGet(String s, NbtCompound nbt) {
        NbtPathArgumentType.NbtPath path = createPath(s);
        return maybeGet(path,nbt);
    }

    public static String getString(String s, NbtCompound nbt) {
        NbtElement tag = maybeGet(s,nbt).orElse(NbtString.of(""));
        if (tag instanceof NbtString stringTag)
            return stringTag.asString();
        return "";
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean trySet(NbtPathArgumentType.NbtPath path, NbtElement nbt, NbtElement set) {
        if (path == null)
            return false;
        try {
            path.put(nbt,set);
        } catch (CommandSyntaxException e) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean trySet(String s, NbtElement nbt, NbtElement set) {
        NbtPathArgumentType.NbtPath path = createPath(s);
        if (path == null)
            return false;
        try {
            path.put(nbt,set);
        } catch (CommandSyntaxException e) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean tryRemove(NbtPathArgumentType.NbtPath path, NbtElement nbt) {
        if (path == null)
            return false;
        path.remove(nbt);
        return true;
    }
}
