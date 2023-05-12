package cc.mewcraft.mewfishing.loot;

import cc.mewcraft.mewcore.util.UtilFile;
import cc.mewcraft.mewfishing.MewFishing;
import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.mewfishing.loot.api.Loot;
import cc.mewcraft.mewfishing.loot.api.LootTable;
import cc.mewcraft.mewfishing.loot.impl.loot.*;
import cc.mewcraft.mewfishing.loot.impl.serializer.*;
import cc.mewcraft.mewfishing.loot.impl.table.LootTableImpl;
import cc.mewcraft.mewfishing.util.RandomCollection;
import com.google.common.collect.ImmutableMap;
import io.leangen.geantyref.TypeToken;
import me.lucko.helper.random.RandomSelector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

@DefaultQualifier(NonNull.class)
public class LootTableManager {

    private final Map<String, LootTable> tableMap;
    private final RandomCollection<LootTable> tableRandom;

    public LootTableManager() {
        tableMap = new HashMap<>();
        tableRandom = new RandomCollection<>();
        loadFiles();
    }

    /**
     * Draw a loot table, neglecting whether the player meets the conditions of it.
     *
     * @return a loot table
     */
    public LootTable draw() {
        return tableRandom.pick();
    }

    /**
     * Draw a loot table, where the player meets the conditions of it.
     *
     * @param event the event related to this drawing
     *
     * @return a loot table
     */
    public LootTable drawMatched(FishLootEvent event) {
        // TODO kinda expensive
        // we should cache it for each player
        // since you usually don't move when fishing
        Collection<LootTable> all = tableMap.values();
        List<LootTable> matched = all.stream().filter(table -> table.evaluate(event)).toList();
        return RandomSelector.weighted(matched).pick();
    }

    /**
     * Get all the loaded loot tables.
     *
     * @return all the loaded loot tables
     */
    public ImmutableMap<String, LootTable> tables() {
        return ImmutableMap.copyOf(tableMap);
    }

    /**
     * Load all the loot tables from files.
     */
    public void loadFiles() {
        File lootDir = new File(MewFishing.instance().getDataFolder(), "loots");
        boolean mkdirs = lootDir.mkdirs();
        Collection<File> lootFiles = FileUtils.listFiles(lootDir, new String[]{"yml"}, true);
        if (mkdirs || lootFiles.isEmpty())
            UtilFile.copyResourcesRecursively(MewFishing.instance().getClassloader().getResource("loots"), lootDir);
        for (final File file : lootFiles) {
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .defaultOptions(config -> config.serializers(builder -> {
                    // loot
                    builder.register(CommandLoot.class, new CommandLootSerializer());
                    builder.register(SimpleItemLoot.class, new SimpleItemLootSerializer());
                    builder.register(CustomItemLoot.class, new CustomItemLootSerializer());
                    builder.register(PluginItemLoot.class, new PluginItemLootSerializer());
                    builder.register(TableLoot.class, new TableLootSerializer());

                    // loot list
                    builder.register(new TypeToken<>() {}, new LootCollectionSerializer());

                    // condition
                    builder.register(new TypeToken<>() {}, new ConditionSerializer());
                }))
                .indent(4)
                .path(file.toPath())
                .build();
            try {
                final CommentedConfigurationNode root = loader.load();

                String name = FilenameUtils.getBaseName(file.getName());
                double weight = root.node("weight").getDouble(0D);
                int rolls = root.node("rolls").getInt(0);
                boolean replacement = root.node("replacement").getBoolean(false);
                List<Conditioned> conditions = Objects.requireNonNull(root.node("conditions").get(new TypeToken<>() {}), "conditions");
                RandomCollection<Loot> lootEntries = Objects.requireNonNull(root.node("items").get(new TypeToken<>() {}), "items");

                LootTableImpl table = new LootTableImpl(name, weight, rolls, replacement, conditions, lootEntries);
                tableMap.put(name, table);
                tableRandom.add(table);
            } catch (Exception e) {
                e.printStackTrace();
                MewFishing.log(Level.SEVERE, "Failed to load loot config file: '%s'".formatted(file.getName()));
            }
        }
    }

}
