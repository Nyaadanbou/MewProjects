package cc.mewcraft.mewcore.network;

import net.luckperms.api.node.types.MetaNode;

import java.util.function.Supplier;

public class OriginMeta {
    /**
     * Create a meta node where key is {@link NetworkConstants#SERVER_ORIGIN_ID_KEY} and value is
     * {@link ServerInfo#SERVER_ID}.
     */
    public static final Supplier<MetaNode> SERVER_ORIGIN_ID;
    /**
     * Create a meta node where key is {@link NetworkConstants#SERVER_ORIGIN_NAME_KEY} and value is
     * {@link ServerInfo#SERVER_NAME}.
     */
    public static final Supplier<MetaNode> SERVER_ORIGIN_NAME_KEY;

    static {
        SERVER_ORIGIN_ID = () -> MetaNode.builder(NetworkConstants.SERVER_ORIGIN_ID_KEY, ServerInfo.SERVER_ID.get()).build();
        SERVER_ORIGIN_NAME_KEY = () -> MetaNode.builder(NetworkConstants.SERVER_ORIGIN_NAME_KEY, ServerInfo.SERVER_NAME.get()).build();
    }
}
