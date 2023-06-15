package cc.mewcraft.mewcore.network;

import net.luckperms.api.node.types.MetaNode;

import java.util.Optional;
import java.util.function.Supplier;

public class OriginMeta {
    /**
     * Create a meta node where key is {@link NetworkConstants#SERVER_ORIGIN_ID_KEY} and value is
     * {@link ServerInfo#SERVER_ID}.
     */
    public static final Supplier<Optional<MetaNode>> SERVER_ORIGIN_ID;
    /**
     * Create a meta node where key is {@link NetworkConstants#SERVER_ORIGIN_NAME_KEY} and value is
     * {@link ServerInfo#SERVER_NAME}.
     */
    public static final Supplier<Optional<MetaNode>> SERVER_ORIGIN_NAME_KEY;

    static {
        SERVER_ORIGIN_ID = () -> ServerInfo.SERVER_ID.get().map(id -> MetaNode.builder(NetworkConstants.SERVER_ORIGIN_ID_KEY, id).build());
        SERVER_ORIGIN_NAME_KEY = () -> ServerInfo.SERVER_NAME.get().map(id -> MetaNode.builder(NetworkConstants.SERVER_ORIGIN_NAME_KEY, id).build());
    }
}
