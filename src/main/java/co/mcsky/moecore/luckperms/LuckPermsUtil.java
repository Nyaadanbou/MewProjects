package co.mcsky.moecore.luckperms;

import co.mcsky.moecore.MoeCore;
import com.google.common.base.Preconditions;
import me.lucko.helper.Services;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsUtil {

    private static final LuckPerms lp = Services.get(LuckPerms.class).orElseThrow();

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
            if (MoeCore.plugin.debugMode()) {
                MoeCore.plugin.getLogger().info("Adding permission %s to group %s".formatted(permission, name));
            }
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
            if (MoeCore.plugin.debugMode()) {
                MoeCore.plugin.getLogger().info("Removing permission %s from group %s".formatted(permission, name));
            }
        });
    }

    public static boolean isPlayerInGroup(@NotNull Player player, @NotNull String group) {
        return player.hasPermission("group." + group);
    }

    public static @Nullable String getPlayerGroup(@NotNull Player player, @NotNull Collection<String> possibleGroups) {
        for (String group : possibleGroups) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }

    public static void userSetPrefixAsync(@NotNull UUID uuid, @NotNull String prefix, int priority) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(prefix, "prefix");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().add(PrefixNode.builder(prefix, priority).build());
            if (MoeCore.plugin.debugMode()) {
                MoeCore.plugin.getLogger().info("Set prefix %s for user %s with priority %s".formatted(prefix, uuid, priority));
            }
        });
    }

    public static void userRemovePrefixAsync(@NotNull UUID uuid, int priority) {
        Preconditions.checkNotNull(uuid, "uuid");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().remove(PrefixNode.builder().priority(priority).build());
            if (MoeCore.plugin.debugMode()) {
                MoeCore.plugin.getLogger().info("Remove all prefixes with priority %s from user %s".formatted(priority, uuid));
            }
        });
    }

    public static void userSetSuffixAsync(@NotNull UUID uuid, @NotNull String suffix, int priority) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(suffix, "suffix");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().add(SuffixNode.builder(suffix, priority).build());
            if (MoeCore.plugin.debugMode()) {
                MoeCore.plugin.getLogger().info("Set suffix %s for user %s with priority %s".formatted(suffix, uuid, priority));
            }
        });
    }

    public static void userRemoveSuffixAsync(@NotNull UUID uuid, @NotNull int priority) {
        Preconditions.checkNotNull(uuid, "uuid");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().remove(SuffixNode.builder().priority(priority).build());
            if (MoeCore.plugin.debugMode()) {
                MoeCore.plugin.getLogger().info("Remove all suffixes with priority %s from user %s".formatted(priority, uuid));
            }
        });
    }

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
