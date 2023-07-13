package cc.mewcraft.townybonus.object.culture;

import cc.mewcraft.townybonus.object.bonus.Bonus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single culture level of a {@link Culture}. A culture level has a
 * number of {@link Bonus} associated with it.
 */
public final class CultureLevel {

    private final int level;
    private final List<Bonus> bonusList;

    public CultureLevel(int level) {
        this.level = level;
        this.bonusList = new ArrayList<>();
    }

    public int getLevel() {
        return level;
    }

    public @NotNull List<Bonus> getBonusList() {
        return bonusList;
    }

    public boolean addBonus(@NotNull Bonus bonus) {
        return this.bonusList.add(bonus);
    }

    public boolean removeBonus(@NotNull Bonus bonus) {
        return this.bonusList.remove(bonus);
    }

    public boolean removeBonus(@NotNull String bonus) {
        return bonusList.removeIf(e -> e.getName().equalsIgnoreCase(bonus));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CultureLevel that = (CultureLevel) o;

        return level == that.level;
    }

    @Override
    public int hashCode() {
        return level;
    }
}
