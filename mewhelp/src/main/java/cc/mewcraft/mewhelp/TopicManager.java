package cc.mewcraft.mewhelp;

import cc.mewcraft.mewhelp.object.HelpTopic;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public class TopicManager {

    private final MewHelp plugin;
    private final Map<String, HelpTopic> topicMap;

    public TopicManager(MewHelp plugin) {
        this.plugin = plugin;
        this.topicMap = new HashMap<>();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        Optional.ofNullable(plugin.getConfig().getConfigurationSection("help")).ifPresent(section -> {
            for (String key : section.getKeys(false)) {
                List<String> messages = section.getStringList(key);
                HelpTopic topic = new HelpTopic(key, messages);
                topicMap.put(key, topic);
            }
            plugin.getComponentLogger().info(
                plugin.getLanguages().of("msg_loaded_topics").replace("amount", topicMap.size()).component()
            );
        });
    }

    public @NonNull Collection<String> allTopics() {
        return topicMap.keySet();
    }

    public @Nullable HelpTopic topic(String key) {
        return topicMap.get(key);
    }

}
