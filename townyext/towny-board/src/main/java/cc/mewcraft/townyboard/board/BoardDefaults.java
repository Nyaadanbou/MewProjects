package cc.mewcraft.townyboard.board;

import cc.mewcraft.townyboard.TownyBoardPlugin;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Singleton
public class BoardDefaults {
    private final TownyBoardPlugin plugin;
    private final MiniMessageBoard defaultTownBoard;
    private final MiniMessageBoard defaultNationBoard;

    @Inject
    public BoardDefaults(final TownyBoardPlugin plugin) {
        this.plugin = plugin;
        this.defaultTownBoard = readConfig("default_town_board");
        this.defaultNationBoard = readConfig("default_nation_board");
    }

    private MiniMessageBoard readConfig(String sectionPath) {
        ConfigurationSection config = Objects.requireNonNull(plugin.getConfig().getConfigurationSection(sectionPath));

        String title = Objects.requireNonNull(config.getString("title"));
        String author = Objects.requireNonNull(config.getString("author"));
        List<String> pages = Optional.ofNullable(config.getConfigurationSection("pages"))
            .map(section -> {
                List<String> pages1 = new ArrayList<>();
                Set<String> numbers = section.getKeys(false);
                for (final String num : numbers) {

                    // Merge lines
                    StringBuilder sb = new StringBuilder();
                    section.getStringList(num).forEach(line -> sb.append("\n").append(line == null ? "" : line));
                    pages1.add(sb.toString().replaceFirst("\n", ""));

                }
                return pages1;
            }).orElse(Collections.emptyList());

        return new MiniMessageBoard(title, author, pages);
    }

    public @NotNull MiniMessageBoard defaultTownBoard() {
        return defaultTownBoard;
    }

    public @NotNull MiniMessageBoard defaultNationBoard() {
        return defaultNationBoard;
    }
}
