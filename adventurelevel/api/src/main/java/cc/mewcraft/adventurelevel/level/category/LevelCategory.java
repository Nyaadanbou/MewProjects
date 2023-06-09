package cc.mewcraft.adventurelevel.level.category;

import org.bukkit.entity.ExperienceOrb;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LevelCategory {
    /**
     * Exp source is implementation-defined
     */
    MAIN,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#PLAYER_DEATH}
     */
    PLAYER_DEATH,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#ENTITY_DEATH}
     */
    ENTITY_DEATH,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#FURNACE}
     */
    FURNACE,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#BREED}
     */
    BREED,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#VILLAGER_TRADE}
     */
    VILLAGER_TRADE,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#FISHING}
     */
    FISHING,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#BLOCK_BREAK}
     */
    BLOCK_BREAK,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#EXP_BOTTLE}
     */
    EXP_BOTTLE,
    /**
     * Exp source is {@link org.bukkit.entity.ExperienceOrb.SpawnReason#GRINDSTONE}
     */
    GRINDSTONE;

    /**
     * @param reason the spawn reason
     *
     * @return returns an empty if the SpawnReason should not be counted
     */
    public static @Nullable LevelCategory toLevelCategory(@NotNull ExperienceOrb.SpawnReason reason) {
        return switch (reason) {
            case PLAYER_DEATH -> LevelCategory.PLAYER_DEATH;
            case ENTITY_DEATH -> LevelCategory.ENTITY_DEATH;
            case FURNACE -> LevelCategory.FURNACE;
            case BREED -> LevelCategory.BREED;
            case VILLAGER_TRADE -> LevelCategory.VILLAGER_TRADE;
            case FISHING -> LevelCategory.FISHING;
            case BLOCK_BREAK -> LevelCategory.BLOCK_BREAK;
            case EXP_BOTTLE -> LevelCategory.EXP_BOTTLE;
            case GRINDSTONE -> LevelCategory.GRINDSTONE;
            case CUSTOM, UNKNOWN -> null;
        };
    }
}
