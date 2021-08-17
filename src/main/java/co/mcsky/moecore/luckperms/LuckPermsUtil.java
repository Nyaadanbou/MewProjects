package co.mcsky.moecore.luckperms;

import co.mcsky.moecore.MoeCore;
import com.google.common.base.Preconditions;
import me.lucko.helper.Services;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.actionlog.Action;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsUtil {

    private static final LuckPerms lp = Services.get(LuckPerms.class).orElseThrow();
    private static final String pluginName = MoeCore.plugin.getName();

    /**
     * Adds permission to specified group without any contexts.
     *
     * @param name       the group name
     * @param permission the permission to add
     */
    public static void groupAddPermissionAsync(@NotNull String name, @NotNull String permission) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(permission, "permission");

        lp.getGroupManager().modifyGroup(name, group -> {
            group.data().add(permissionNodeWithoutContext(permission));
            lp.getActionLogger().submit(Action.builder()
                    .sourceName(pluginName)
                    .targetName(name)
                    .description("add group permission")
                    .build());
        });
    }

    /**
     * Removes permission from specified group without any contexts.
     *
     * @param name       the group name
     * @param permission the permission to remove
     */
    public static void groupRemovePermissionAsync(@NotNull String name, @NotNull String permission) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(permission, "permission");

        lp.getGroupManager().modifyGroup(name, group -> {
            group.data().remove(permissionNodeWithoutContext(permission));
            lp.getActionLogger().submit(Action.builder()
                    .sourceName(pluginName)
                    .targetName(name)
                    .description("remove group permission")
                    .build());
        });
    }

    /**
     * Checks whether a player belongs to the specified group.
     *
     * @param player the player to check
     * @param group  the group to check
     * @return true if the player belongs to the group, otherwise false
     */
    public static boolean isPlayerInGroup(@NotNull Player player, @NotNull String group) {
        return player.hasPermission("group." + group);
    }

    /**
     * Adds a prefix with specified priority to the user.
     *
     * @param uuid     the uuid of the user
     * @param prefix   the prefix to set
     * @param priority the priority of the prefix
     */
    public static void userSetPrefixAsync(@NotNull UUID uuid, @NotNull String prefix, int priority) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(prefix, "prefix");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().add(PrefixNode.builder(prefix, priority).build());
            lp.getActionLogger().submit(Action.builder()
                    .sourceName(pluginName)
                    .target(uuid)
                    .description("set prefix with priority %s".formatted(priority))
                    .build());
        });
    }

    /**
     * Removes all the prefixes with specified priority from the user.
     *
     * @param uuid     the uuid of the user
     * @param priority the priority of prefix to be removed
     */
    public static void userRemovePrefixAsync(@NotNull UUID uuid, int priority) {
        Preconditions.checkNotNull(uuid, "uuid");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().clear(test -> {
                final Optional<PrefixNode> prefixNode = NodeType.PREFIX.tryCast(test);
                return prefixNode.filter(node -> node.getPriority() == priority).isPresent();
            });
            lp.getActionLogger().submit(Action.builder()
                    .sourceName(pluginName)
                    .target(uuid)
                    .description("remove prefix with priority %s".formatted(priority))
                    .build());
        });
    }

    /**
     * Adds a suffix with specified suffix to the user.
     *
     * @param uuid     the uuid of the user
     * @param suffix   the suffix to add
     * @param priority the priority of the suffix
     */
    public static void userSetSuffixAsync(@NotNull UUID uuid, @NotNull String suffix, int priority) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(suffix, "suffix");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().add(SuffixNode.builder(suffix, priority).build());
            lp.getActionLogger().submit(Action.builder()
                    .sourceName(pluginName)
                    .target(uuid)
                    .description("set suffix with priority %s".formatted(priority))
                    .build());
        });
    }

    /**
     * Removes all the suffixes with specified priority from the user.
     *
     * @param uuid     the uuid of the user
     * @param priority the priority of suffix to be removed
     */
    public static void userRemoveSuffixAsync(@NotNull UUID uuid, @NotNull int priority) {
        Preconditions.checkNotNull(uuid, "uuid");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().clear(test -> {
                final Optional<SuffixNode> suffixNode = NodeType.SUFFIX.tryCast(test);
                return suffixNode.filter(node -> node.getPriority() == priority).isPresent();
            });
            lp.getActionLogger().submit(Action.builder()
                    .sourceName(pluginName)
                    .target(uuid)
                    .description("remove suffix with priority %s".formatted(priority))
                    .build());
        });
    }

    /**
     * Adds a permission node without context to the specified user.
     *
     * @param uuid       the uuid of the user
     * @param permission the permission node to add
     */
    public static void userAddPermissionAsync(@NotNull UUID uuid, @NotNull String permission) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(permission, "permission");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().add(permissionNodeWithoutContext(permission));
            if (MoeCore.plugin.debugMode()) {
                MoeCore.plugin.getLogger().info("Adding permission %s to user %s".formatted(permission, uuid));
            }
        });
    }

    /**
     * Removes a permission node without context to the specified user.
     *
     * @param uuid       the uuid of the user
     * @param permission the permission node to remove
     */
    public static void userRemovePermissionAsync(@NotNull UUID uuid, @NotNull String permission) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(permission, "permission");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().remove(permissionNodeWithoutContext(permission));
            if (MoeCore.plugin.debugMode()) {
                MoeCore.plugin.getLogger().info("Removing permission %s from user %s".formatted(permission, uuid));
            }
        });
    }

    public static @NonNull CompletableFuture<Optional<Group>> getGroup(@NotNull String name) {
        return lp.getGroupManager().loadGroup(name);
    }

    public static @NonNull CompletableFuture<User> loadUser(@NotNull UUID uuid) {
        return lp.getUserManager().loadUser(uuid);
    }

    public static @NonNull CompletableFuture<User> loadUser(@NotNull Player player) {
        return loadUser(player.getUniqueId());
    }

    public static @NotNull PermissionNode permissionNodeWithoutContext(@NotNull String permission) {
        return PermissionNode.builder()
                .permission(permission)
                .context(ImmutableContextSet.empty())
                .build();
    }

}
