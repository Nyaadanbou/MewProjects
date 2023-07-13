package cc.mewcraft.townybonus.object.culture;

import cc.mewcraft.townybonus.TownyBonus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a culture, potentially associated with a {@link
 * com.palmergames.bukkit.towny.object.Nation}.
 */
public final class Culture {

    private final String name;
    private final String url;
    private final Map<Integer, CultureLevel> levels;

    public Culture(@NotNull String name, String url) {
        this.name = name.trim();
        this.url = (url == null || url.isEmpty()) ? TownyBonus.config().getDefaultCulturePage() : url;
        this.levels = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public @Nullable CultureLevel getCultureLevelOrNull(int nationLevel) {
        return levels.get(nationLevel);
    }

    public Optional<CultureLevel> getCultureLevel(int nationLevel) {
        return Optional.ofNullable(getCultureLevelOrNull(nationLevel));
    }

    public CultureLevel addCultureLevel(CultureLevel cultureLevel) {
        return levels.put(cultureLevel.getLevel(), cultureLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Culture culture = (Culture) o;

        return name.equals(culture.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
