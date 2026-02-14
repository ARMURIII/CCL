package arr.armuriii.ccl.script.json.data;

import net.minecraft.server.command.ServerCommandSource;

public class CommandExtraData extends AbstractExtraData {
    private final ServerCommandSource source;

    public CommandExtraData(ServerCommandSource source) {
        this.source = source;
    }

    @Override
    public Object[] getAllData() {
        return new ServerCommandSource[]{source};
    }

    @Override
    public Object getFirstData() {
        return source;
    }
}
