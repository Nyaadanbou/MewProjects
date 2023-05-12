package cc.mewcraft.mewutils.module.case_insensitive_commands;

import cc.mewcraft.mewcore.listener.AutoCloseableListener;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Locale;


public class CaseInsensitiveCommandsModule extends ModuleBase implements AutoCloseableListener {
    @Inject
    public CaseInsensitiveCommandsModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void enable() throws Exception {
        registerListener(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/")) {
            String[] commandParts = event.getMessage().split(" ", 2);
            if (commandParts.length > 1) {
                event.setMessage(commandParts[0].toLowerCase(Locale.ROOT) + " " + commandParts[1]);
            } else {
                event.setMessage(event.getMessage().toLowerCase(Locale.ROOT));
            }
        }
    }

}
