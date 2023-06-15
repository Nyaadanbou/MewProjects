package cc.mewcraft.mewcore.network;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.util.Optional;
import java.util.function.Supplier;

public final class ServerInfo {

    /**
     * Get the "server-id" of this server.
     * <p>
     * The static context "server-id" should be configured in the `[luckperms-data-folder]/contexts.json` file.
     */
    public static final Supplier<Optional<String>> SERVER_ID;
    /**
     * Get the "server-name" of this server.
     * <p>
     * The static context "server-name" should be configured in the `[luckperms-data-folder]/contexts.json` file.
     */
    public static final Supplier<Optional<String>> SERVER_NAME;

    static {
        final LuckPerms luckPerms = LuckPermsProvider.get();
        SERVER_ID = () -> luckPerms.getContextManager().getStaticContext().getAnyValue(NetworkConstants.SERVER_ID_KEY);
        SERVER_NAME = () -> luckPerms.getContextManager().getStaticContext().getAnyValue(NetworkConstants.SERVER_NAME_KEY);
    }

}
