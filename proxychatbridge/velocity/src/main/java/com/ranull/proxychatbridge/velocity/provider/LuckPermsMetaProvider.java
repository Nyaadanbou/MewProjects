package com.ranull.proxychatbridge.velocity.provider;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public class LuckPermsMetaProvider implements MetaProvider {

    protected static final String EMPTY = "";

    private final LuckPerms api;

    public LuckPermsMetaProvider(LuckPerms luckPerms) {
        this.api = luckPerms;
    }

    @Override public @NonNull String meta(final @NonNull UUID uuid, final String metaKey) {
        final User user = api.getUserManager().getUser(uuid);

        if (user == null)
            return EMPTY;

        String metaValue = user.getCachedData().getMetaData().getMetaValue(metaKey);
        if (metaValue == null)
            return EMPTY;

        return metaValue;
    }

}
