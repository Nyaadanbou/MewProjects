package cc.mewcraft.mewutils.module.string_replacer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.lucko.helper.terminable.Terminable;

public class ProtocolLibHook implements Terminable {

    private final StringReplacerModule module;
    private final ProtocolManager protocolManager;

    // packet listeners
    private final PacketAdapter inventoryTitleReplacer;

    public ProtocolLibHook(final StringReplacerModule module) {
        this.module = module;
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        // --- define packet listeners ---
        this.inventoryTitleReplacer = new PacketAdapter(
            module.getParentPlugin(), ListenerPriority.HIGHEST, PacketType.Play.Server.OPEN_WINDOW
        ) {
            @Override public void onPacketSending(final PacketEvent event) {
                PacketContainer packet = event.getPacket();
                WrappedChatComponent component = packet.getChatComponents().readSafely(0);
                if (component == null)
                    return;
                String oldValue = component.getJson();
                if (module.getParentPlugin().isDevMode())
                    module.info(oldValue);
                String newValue = module.replacement.get(oldValue);
                if (newValue != null) {
                    component.setJson(newValue);
                    packet.getChatComponents().write(0, component);
                }
            }
        };

        // --- register packet listeners ---
        this.protocolManager.addPacketListener(this.inventoryTitleReplacer);
    }

    @Override public void close() {
        this.protocolManager.removePacketListener(this.inventoryTitleReplacer);
    }

}
