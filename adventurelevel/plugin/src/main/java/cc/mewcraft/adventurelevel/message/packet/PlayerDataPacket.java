package cc.mewcraft.adventurelevel.message.packet;

import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import me.lucko.helper.messaging.codec.Message;

import java.util.UUID;

@Message(codec = PlayerDataCodec.class) // use specific Codec instead of GsonCodec to encode/decode this class
public record PlayerDataPacket(
    UUID uuid,
    String server,
    long timestamp,
    int mainXp,
    int blockBreakXp,
    int breedXp,
    int entityDeathXp,
    int expBottleXp,
    int fishingXp,
    int furnaceXp,
    int grindstoneXp,
    int playerDeathXp,
    int villagerTradeXp
) {

    public int getExpByCategory(LevelCategory category) {
        return switch (category) {
            case MAIN -> mainXp;
            case BLOCK_BREAK -> blockBreakXp;
            case BREED -> breedXp;
            case ENTITY_DEATH -> entityDeathXp;
            case EXP_BOTTLE -> expBottleXp;
            case FISHING -> fishingXp;
            case FURNACE -> furnaceXp;
            case GRINDSTONE -> grindstoneXp;
            case PLAYER_DEATH -> playerDeathXp;
            case VILLAGER_TRADE -> villagerTradeXp;
        };
    }
}
