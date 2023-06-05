package com.ranull.proxychatbridge.velocity.provider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.Flag;
import net.luckperms.api.query.QueryOptions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.UUID;

public class LuckPermsGroupProvider implements GroupProvider {

    private final @NonNull LuckPerms luckPerms;

    public LuckPermsGroupProvider(final @NonNull LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @Override
    public @NonNull List<String> groups(final @NonNull UUID uuid) {
        final User user = this.luckPerms.getUserManager().getUser(uuid);

        if (user == null) {
            return List.of();
        }

        QueryOptions queryOptions = QueryOptions.defaultContextualOptions().toBuilder().flag(Flag.RESOLVE_INHERITANCE, true).build();
        return user.getInheritedGroups(queryOptions).stream().map(Group::getName).toList();
    }

}
