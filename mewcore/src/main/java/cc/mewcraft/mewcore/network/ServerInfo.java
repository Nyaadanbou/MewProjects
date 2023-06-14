package cc.mewcraft.mewcore.network;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.util.Optional;
import java.util.function.Supplier;

public final class ServerInfo {

    /**
     * Get the current "server-id" of server in which the player is.
     * <p>
     * The static context "server-id" should be configured in the `[luckperms-data-folder]/contexts.json` file.
     */
    public static final Supplier<Optional<String>> SERVER_ID;
    /**
     * Get the current "server-name" of server in which the player is.
     * <p>
     * The static context "server-name" should be configured in the `[luckperms-data-folder]/contexts.json` file.
     */
    public static final Supplier<Optional<String>> SERVER_NAME;

    static {
        final LuckPerms luckPerms = LuckPermsProvider.get();
        SERVER_ID = () -> luckPerms.getContextManager().getStaticContext().getAnyValue(Constants.SERVER_ID_KEY);
        SERVER_NAME = () -> luckPerms.getContextManager().getStaticContext().getAnyValue(Constants.SERVER_NAME_KEY);
    }

}
