package cc.mewcraft.adventurelevel.message;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.data.RealPlayerData;
import cc.mewcraft.adventurelevel.level.LevelBeanFactory;
import cc.mewcraft.adventurelevel.level.category.LevelBean.Category;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.lucko.helper.messaging.codec.Codec;
import me.lucko.helper.messaging.codec.EncodingException;

import java.util.UUID;

public final class RealPlayerDataCodec implements Codec<RealPlayerData> {

    @Override public byte[] encode(final RealPlayerData message) throws EncodingException {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // First write out uuid
        out.writeLong(message.getUuid().getMostSignificantBits());
        out.writeLong(message.getUuid().getLeastSignificantBits());

        // Start write out experience values
        out.writeInt(message.getMainLevel().getExperience());
        out.writeInt(message.getCateLevel(Category.BLOCK_BREAK).getExperience());
        out.writeInt(message.getCateLevel(Category.BREED).getExperience());
        out.writeInt(message.getCateLevel(Category.ENTITY_DEATH).getExperience());
        out.writeInt(message.getCateLevel(Category.EXP_BOTTLE).getExperience());
        out.writeInt(message.getCateLevel(Category.FISHING).getExperience());
        out.writeInt(message.getCateLevel(Category.FURNACE).getExperience());
        out.writeInt(message.getCateLevel(Category.GRINDSTONE).getExperience());
        out.writeInt(message.getCateLevel(Category.PLAYER_DEATH).getExperience());
        out.writeInt(message.getCateLevel(Category.VILLAGER_TRADE).getExperience());

        return out.toByteArray();
    }

    @Override public RealPlayerData decode(final byte[] buf) throws EncodingException {
        ByteArrayDataInput in = ByteStreams.newDataInput(buf);

        return new RealPlayerData(
            AdventureLevelPlugin.getInstance(),
            new UUID(in.readLong(), in.readLong()),
            LevelBeanFactory.createMainLevelBean().withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.BLOCK_BREAK).withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.BREED).withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.ENTITY_DEATH).withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.EXP_BOTTLE).withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.FISHING).withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.FURNACE).withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.GRINDSTONE).withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.PLAYER_DEATH).withExperience(in.readInt()),
            LevelBeanFactory.createCatLevelBean(Category.VILLAGER_TRADE).withExperience(in.readInt())
        );
    }

}
