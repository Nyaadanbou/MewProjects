package cc.mewcraft.mewcore.network;

import net.luckperms.api.LuckPermsProvider;

import java.util.function.Supplier;

public final class ServerInfo {

    /**
     * Get the "server-id" of this server.
     * <p>
     * The static context "server-id" should be configured in the `[luckperms-data-folder]/contexts.json` file.
     */
    public static final Supplier<String> SERVER_ID;
    /**
     * Get the "server-name" of this server.
     * <p>
     * The static context "server-name" should be configured in the `[luckperms-data-folder]/contexts.json` file.
     */
    public static final Supplier<String> SERVER_NAME;

    static {
        SERVER_ID = () -> LuckPermsProvider.get().getContextManager().getStaticContext().getAnyValue(NetworkConstants.SERVER_ID_KEY).orElseThrow();
        SERVER_NAME = () -> LuckPermsProvider.get().getContextManager().getStaticContext().getAnyValue(NetworkConstants.SERVER_NAME_KEY).orElseThrow();
    }

}
