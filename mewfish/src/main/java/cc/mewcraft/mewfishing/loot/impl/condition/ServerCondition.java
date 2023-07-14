package cc.mewcraft.mewfishing.loot.impl.condition;

import cc.mewcraft.mewcore.network.ServerInfo;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public class ServerCondition implements Conditioned {
    private final List<String> serverList;

    public ServerCondition(final List<String> serverList) {
        this.serverList = serverList;
    }

    @Override public boolean evaluate(final FishLootEvent event) {
        if (serverList.isEmpty()) {
            return true;
        }

        return serverList.stream().anyMatch(server ->
            server.equals(ServerInfo.SERVER_ID.get())
        );
    }
}
