package cc.mewcraft.mewutils.api.module;

import cc.mewcraft.mewutils.api.MewPlugin;
import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("unused")
public interface ModuleLogger extends ModuleIdentifier {

    MewPlugin getParentPlugin();

    default String getPrefix() {
        return "[" + getLongId() + "] ";
    }

    // --- plain string logger ---

    default void info(String msg) {
        getParentPlugin().getLogger().info(getPrefix() + msg);
    }

    default void warn(String msg) {
        getParentPlugin().getLogger().warning(getPrefix() + msg);
    }

    default void error(String msg) {
        getParentPlugin().getLogger().severe(getPrefix() + msg);
    }

    default void debug(String msg) {
        if (getParentPlugin().isDevMode()) getParentPlugin().getLogger().info(getPrefix() + msg);
    }

    // --- component logger ---

    default void info(Component msg) {
        getParentPlugin().getComponentLogger().info(text(getPrefix()).append(msg));
    }

    default void warn(Component msg) {
        getParentPlugin().getComponentLogger().warn(text(getPrefix()).append(msg));
    }

    default void error(Component msg) {
        getParentPlugin().getComponentLogger().error(text(getPrefix()).append(msg));
    }

}
