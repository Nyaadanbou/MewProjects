package com.ranull.proxychatbridge.velocity.provider;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

/**
 * {@code MetaProvider} defines methods to retrieve a user's metadata.
 */
public interface MetaProvider {

    /**
     * Returns a "meta value" to which the "meta key" maps.
     *
     * @param uuid the uuid of a player
     *
     * @return a "meta value" to which the "meta key" maps
     */
    @NonNull String meta(final @NonNull UUID uuid, String metaKey);

}
