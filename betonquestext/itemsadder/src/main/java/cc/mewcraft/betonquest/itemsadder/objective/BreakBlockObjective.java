package cc.mewcraft.betonquest.itemsadder.objective;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class BreakBlockObjective extends Objective implements Listener {
    private final String namespacedID;
    private final int amount;
    private final boolean notify;
    private final int notifyInterval;

    public BreakBlockObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = BlockData.class;
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = (instruction.hasArgument("notify") || notifyInterval > 1);
        amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
        namespacedID = instruction.next() + ":" + instruction.next();
        ItemsAdderUtil.validateCustomBlockSilently(instruction.getPackage(), namespacedID);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(CustomBlockBreakEvent e) {
        OnlineProfile profile = PlayerConverter.getID(e.getPlayer());

        if (containsPlayer(profile)
            && checkConditions(profile)
            && e.getNamespacedID().equalsIgnoreCase(namespacedID)) {

            BlockData playerData = (BlockData) dataMap.get(profile);
            playerData.add();
            if (playerData.getAmount() == amount) {
                completeObjective(profile);
            } else if (notify && playerData.getAmount() % notifyInterval == 0) {
                if (playerData.getAmount() > amount) {
                    try {
                        Config.sendNotify(instruction.getPackage().getQuestPath(), profile, "blocks_to_break", new String[]{String.valueOf(playerData.getAmount() - amount)}, "blocks_to_break,info");
                    } catch (QuestRuntimeException exception) {
                        /*try {
                            LOG.warn("The notify system was unable to play a sound for the 'blocks_to_break' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                        } catch (InstructionParseException ex) {
                            LOG.reportException(ex);
                        }*/
                    }
                }
            } else {
                try {
                    Config.sendNotify(instruction.getPackage().getQuestPath(), profile, "blocks_to_break", new String[]{String.valueOf(amount - playerData.getAmount())}, "blocks_to_break,info");
                } catch (QuestRuntimeException exception) {
                    /*try {
                        LOG.warn("The notify system was unable to play a sound for the 'blocks_to_break' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                    } catch (InstructionParseException ex) {
                        LOG.reportException(ex);
                    }*/
                }
            }

        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "0";
    }

    @Override
    public String getProperty(String name, Profile profile) {
        if ("left".equalsIgnoreCase(name))
            return Integer.toString(amount - ((BlockData) dataMap.get(profile)).getAmount());
        if ("amount".equalsIgnoreCase(name))
            return Integer.toString(((BlockData) dataMap.get(profile)).getAmount());
        return "";
    }

    public static class BlockData extends Objective.ObjectiveData {
        private int amount;

        public BlockData(String instruction, Profile profile, String objID) {
            super(instruction, profile, objID);
            amount = Integer.parseInt(instruction);
        }

        private void add() {
            amount++;
            update();
        }

        private int getAmount() {
            return amount;
        }

        public String toString() {
            return String.valueOf(amount);
        }
    }

}
