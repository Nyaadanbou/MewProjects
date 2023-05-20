package cc.mewcraft.mewhelp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class MewConfig {

    private final MewHelp p;

    public MewConfig(MewHelp p) {
        this.p = p;
    }

    public void loadDefaultConfig() {
        p.saveDefaultConfig();
        p.reloadConfig();
    }

    public boolean getDebug() {
        return p.getConfig().getBoolean("debug");
    }

    public List<String> getCommandLabels() {
        ConfigurationSection helpSec = Objects.requireNonNull(p.getConfig().getConfigurationSection("help"));
        Set<String> keys = helpSec.getKeys(false);
        return keys.stream().toList(); // return a modifiable list
    }

    public List<String> getRawHelpMessages(String commandLabel) {
        ConfigurationSection helpSec = Objects.requireNonNull(p.getConfig().getConfigurationSection("help"));
        return helpSec.getStringList(commandLabel);
    }

    public List<Component> getHelpMessages(String commandLabel) {
        List<String> rawHelpMessages = getRawHelpMessages(commandLabel);
        return rawHelpMessages.stream().map(MiniMessage.miniMessage()::deserialize).toList();
    }

    public Map<String, List<Component>> getHelpMessageMap() {
        Map<String, List<Component>> map = new HashMap<>();
        for (String label : getCommandLabels()) {
            List<Component> helpMessages = getHelpMessages(label);
            map.put(label.toLowerCase(Locale.ROOT), helpMessages); // note that keys are lowercase
        }
        return map;
    }

}
