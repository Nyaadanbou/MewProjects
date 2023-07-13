package cc.mewcraft.enchantment.gui.config;

import java.util.ArrayList;
import java.util.List;

public record LoreFormat(
    List<String> all,
    List<String> conflicts,
    List<String> charging,
    List<String> obtaining
) {
    @Override public List<String> all() {
        return new ArrayList<>(all);
    }

    @Override public List<String> conflicts() {
        return new ArrayList<>(conflicts);
    }

    @Override public List<String> charging() {
        return new ArrayList<>(charging);
    }

    @Override public List<String> obtaining() {
        return new ArrayList<>(obtaining);
    }
}
