package com.ranull.proxychatbridge.velocity.provider;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;
import java.util.UUID;

/**
 * {@code GroupProvider} defines methods to retrieve a user's inherited and primary groups.
 */
@DefaultQualifier(NonNull.class)
public interface GroupProvider {

    /**
     * Returns a {@link List} containing the IDs of all groups a player is a member of.
     *
     * @param uuid the uuid of a player
     *
     * @return a {@link List}, will be empty if there is no group data for the provided {@link UUID}
     */
    List<String> groups(final UUID uuid);

}
