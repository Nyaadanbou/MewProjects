package cc.mewcraft.betonquest.itemsadder.objective;

import cc.mewcraft.betonquest.itemsadder.util.ItemsAdderUtil;
import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class SmeltObjective extends Objective implements Listener {

    private final String namespacedID;
    private final int amount;

    public SmeltObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = SmeltData.class;
        amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
        namespacedID = instruction.next() + ":" + instruction.next();
        ItemsAdderUtil.validateCustomStackSilently(instruction.getPackage(), namespacedID);
    }

    @EventHandler
    public void onItemGet(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getInventory().getType() == InventoryType.FURNACE || e.getInventory().getType() == InventoryType.BLAST_FURNACE) {
            if (e.getRawSlot() == 2) {

                OnlineProfile profile = PlayerConverter.getID(player);
                if (containsPlayer(profile)) {
                    CustomStack cs = CustomStack.byItemStack(e.getCurrentItem());
                    if (cs != null && cs.getNamespacedID().equalsIgnoreCase(namespacedID)) {
                        if (checkConditions(profile)) {
                            SmeltData playerData = (SmeltData) dataMap.get(profile);
                            playerData.subtract(e.getCurrentItem().getAmount());
                            if (playerData.isZero()) {
                                completeObjective(profile);
                            }
                        }
                    }
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShiftSmelting(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.FURNACE
            && event.getRawSlot() == 2
            && event.getClick() == ClickType.SHIFT_LEFT
            && event.getWhoClicked() instanceof Player player) {
            OnlineProfile profile = PlayerConverter.getID(player);
            if (containsPlayer(profile)) {
                event.setCancelled(true);
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
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(String name, Profile profile) {
        if ("left".equalsIgnoreCase(name))
            return Integer.toString(amount - ((SmeltData) dataMap.get(profile)).getAmount());
        if ("amount".equalsIgnoreCase(name))
            return Integer.toString(((SmeltData) dataMap.get(profile)).getAmount());
        return "";
    }

    public static class SmeltData extends Objective.ObjectiveData {
        private int amount;

        public SmeltData(String instruction, Profile profile, String objID) {
            super(instruction, profile, objID);
            amount = Integer.parseInt(instruction);
        }

        private int getAmount() {
            return amount;
        }

        private void subtract(int amount) {
            this.amount -= amount;
            update();
        }

        private boolean isZero() {
            return (amount < 1);
        }

        public String toString() {
            return Integer.toString(amount);
        }
    }

}
