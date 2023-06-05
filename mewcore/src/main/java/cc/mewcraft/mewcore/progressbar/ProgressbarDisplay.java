package cc.mewcraft.mewcore.progressbar;

import me.lucko.helper.Schedulers;
import me.lucko.helper.metadata.Empty;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ProgressbarDisplay {

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
    public ProgressbarDisplay(int stayTime, ProgressbarGenerator generator) {
        this.stayTime = stayTime;
        this.generator = generator;
    }

    public void show(@NotNull Player player, @NotNull Supplier<Float> fillPercent) {
        show(player, fillPercent, null, null);
    }

    /**
     * @param player      the player who sees this progressbar
     * @param fillPercent fill percent of this progressbar to show
     * @param head        the text before the progressbar, can be empty
     * @param tail        the text after the progressbar, can be empty
     */
    public void show(@NotNull Player player, @NotNull Supplier<Float> fillPercent, @Nullable Supplier<String> head, @Nullable Supplier<String> tail) {
        if (Metadata.provideForPlayer(player).get(ACTIONBAR_TASK).isEmpty()) {
            Schedulers.builder().sync().every(20).consume(task -> {
                if (task.getTimesRan() > stayTime - 1) {
                    Metadata.provideForPlayer(player).remove(ACTIONBAR_TASK);
                    player.sendActionBar(Component.empty()); // immediately remove action bar
                    task.stop();
                    return;
                }
                Metadata.provideForPlayer(player).put(ACTIONBAR_TASK, Empty.instance());
                var base = generator.create(fillPercent.get());
                if (head == null && tail != null) {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(base + tail.get()));
                } else if (head != null && tail == null) {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(head.get() + base));
                } else if (head != null) {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(head.get() + base + tail.get()));
                } else {
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(base));
                }
            });
        }
    }

}
