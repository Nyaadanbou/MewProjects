package cc.mewcraft.betonquest.itemsadder.util;

import cc.mewcraft.betonquest.BetonQuestExt;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import lombok.CustomLog;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.jetbrains.annotations.NotNull;

@CustomLog
public final class ItemsAdderUtil {

    public static void validateCustomStack(@NotNull QuestPackage questPackage, @NotNull String namespacedID) throws InstructionParseException {
        CustomStack cs = CustomStack.getInstance(namespacedID);
        if (cs == null) {
            throw new InstructionParseException("Unknown item ID: " + namespacedID);
        }
    }

    public static void validateCustomStackSilently(@NotNull QuestPackage questPackage, @NotNull String namespacedID) {
        if (!BetonQuestExt.INSTANCE.isDevMode())
            return;
        CustomStack cs = CustomStack.getInstance(namespacedID);
        if (cs == null) {
            LOG.info(questPackage, "Unknown item ID: " + namespacedID);
        }
    }

    public static void validateCustomBlockSilently(@NotNull QuestPackage questPackage, @NotNull String namespacedID) {
        if (!BetonQuestExt.INSTANCE.isDevMode())
            return;
        CustomBlock cb = CustomBlock.getInstance(namespacedID);
        if (cb == null) {
            LOG.info(questPackage, "Unknown item ID: " + namespacedID);
        } else if (!cb.isBlock()) {
            LOG.info(questPackage, "Item is not block: " + namespacedID);
        }
    }

    private ItemsAdderUtil() {
        throw new UnsupportedOperationException("This class cannot instantiate");
    }

}
