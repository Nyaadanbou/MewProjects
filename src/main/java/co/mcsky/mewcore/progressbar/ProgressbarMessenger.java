package co.mcsky.mewcore.progressbar;

import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.Empty;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProgressbarMessenger {

    private static final MetadataKey<Empty> ACTIONBAR_TASK;

    private final ProgressbarGenerator generator;
    private final int stayTime;

    static {
        ACTIONBAR_TASK = MetadataKey.createEmptyKey("actionbar-task");
    }

    /**
     * @param stayTime  how long the progressbar stays in seconds
     * @param generator a progressbar generator
     */
    public ProgressbarMessenger(int stayTime, ProgressbarGenerator generator) {
        this.stayTime = stayTime;
        this.generator = generator;
    }

    public void show(@NotNull Player player, double fillPercent) {
        show(player, fillPercent, null, null);
    }

    /**
     * @param player      the player who sees this progressbar
     * @param fillPercent fill percent of this progressbar to show
     * @param head        the text before the progressbar, can be empty
     * @param tail        the text after the progressbar, can be empty
     */
    public void show(@NotNull Player player, double fillPercent, @Nullable String head, @Nullable String tail) {
        if (Metadata.provideForPlayer(player).get(ACTIONBAR_TASK).isEmpty()) {
            Schedulers.builder().sync().every(20).consume(task -> {
                if (task.getTimesRan() > stayTime - 1) {
                    Metadata.provideForPlayer(player).remove(ACTIONBAR_TASK);
                    player.sendActionBar(Component.empty()); // immediately remove action bar
                    task.stop();
                    return;
                }
                Metadata.provideForPlayer(player).put(ACTIONBAR_TASK, Empty.instance());
                var base = generator.create(fillPercent);
                if (head == null && tail != null) {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(base + tail));
                } else if (head != null && tail == null) {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(head + base));
                } else if (head != null) {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(head + base + tail));
                } else {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(base));
                }
            });
        }
    }

}
