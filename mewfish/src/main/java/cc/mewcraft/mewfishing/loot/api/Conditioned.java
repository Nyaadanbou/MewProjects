package cc.mewcraft.mewfishing.loot.api;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * Something that has condition attached.
 */
@DefaultQualifier(NonNull.class)
public interface Conditioned {
    boolean evaluate(FishLootEvent event);
}
