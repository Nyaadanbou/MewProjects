package cc.mewcraft.mewcore.util;

import com.google.common.base.Preconditions;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
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

public class UtilLuckPerms {

    private static final LuckPerms lp;

    static {
        lp = LuckPermsProvider.get();
    }

    /**
     * Adds permission to specified group without any contexts.
     *
     * @param name       the group name
     * @param permission the permission
     */
    public static void groupAddPermissionAsync(@NotNull String name, @NotNull String permission) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(permission, "permission");

        lp.getGroupManager().modifyGroup(name, group -> {
            group.data().add(permissionNodeWithoutContext(permission));
        });
    }

    /**
     * Removes permission from specified group without any contexts.
     *
     * @param name       the group name
     * @param permission the permission
     */
    public static void groupRemovePermissionAsync(@NotNull String name, @NotNull String permission) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(permission, "permission");

        lp.getGroupManager().modifyGroup(name, group -> {
            group.data().remove(permissionNodeWithoutContext(permission));
        });
    }

    /**
     * Checks whether a player belongs to the specified group.
     *
     * @param player the player
     * @param group  the group name
     *
     * @return true if the player belongs to the group, otherwise false
     */
    public static boolean isPlayerInGroup(@NotNull Player player, @NotNull String group) {
        return player.hasPermission("group." + group);
    }

    /**
     * Adds a prefix with specified priority to the user.
     * <p>
     * This will first remove all prefixes before adding the prefix.
     *
     * @param uuid     the uuid of the user
     * @param prefix   the prefix
     * @param priority the priority of the prefix
     */
    public static void userSetPrefixAsync(@NotNull UUID uuid, @NotNull String prefix, int priority) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(prefix, "prefix");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().clear(NodeType.PREFIX::matches);
            user.data().add(PrefixNode.builder(prefix, priority).build());
        });
    }

    /**
     * Removes all the prefixes with specified priority from the user.
     *
     * @param uuid the uuid of the user
     */
    public static void userRemovePrefixAsync(@NotNull UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().clear(NodeType.PREFIX::matches);
        });
    }

    /**
     * Adds a suffix with specified suffix to the user.
     * <p>
     * This will first remove all suffixes before adding the suffix.
     *
     * @param uuid     the uuid of the user
     * @param suffix   the suffix
     * @param priority the priority of the suffix
     */
    public static void userSetSuffixAsync(@NotNull UUID uuid, @NotNull String suffix, int priority) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(suffix, "suffix");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().clear(NodeType.SUFFIX::matches);
            user.data().add(SuffixNode.builder(suffix, priority).build());
        });

    }

    /**
     * Removes all the suffixes with specified priority from the user.
     *
     * @param uuid the uuid of the user
     */
    public static void userRemoveSuffixAsync(@NotNull UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().clear(NodeType.SUFFIX::matches);
        });
    }

    /**
     * Adds a permission node without context to the specified user.
     *
     * @param uuid       the uuid of the user
     * @param permission the permission node
     */
    public static void userAddPermissionAsync(@NotNull UUID uuid, @NotNull String permission) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(permission, "permission");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().add(permissionNodeWithoutContext(permission));
        });
    }

    /**
     * Removes a permission node without context to the specified user.
     *
     * @param uuid       the uuid of the user
     * @param permission the permission node
     */
    public static void userRemovePermissionAsync(@NotNull UUID uuid, @NotNull String permission) {
        Preconditions.checkNotNull(uuid, "uuid");
        Preconditions.checkNotNull(permission, "permission");

        lp.getUserManager().modifyUser(uuid, user -> {
            user.data().remove(permissionNodeWithoutContext(permission));
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
        return PermissionNode.builder(permission).context(ImmutableContextSet.empty()).build();
    }

}
