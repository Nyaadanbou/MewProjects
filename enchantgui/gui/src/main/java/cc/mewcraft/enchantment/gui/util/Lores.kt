package cc.mewcraft.enchantment.gui.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public final class LoreUtils {
    @Contract
    public static void removePlaceholder(@NotNull String placeholder, @Nullable List<String> dst) {
        removePlaceholder(placeholder, dst, true);
    }

    @Contract
    public static void removePlaceholder(@NotNull String placeholder, @Nullable List<String> dst, boolean keep) {
        if (dst == null) return;

        if (keep) {
            ListIterator<String> it = dst.listIterator();
            while (it.hasNext()) {
                String next = it.next();
                if (next.contains(placeholder)) {
                    it.set(next.replace(placeholder, ""));
                }
            }
        } else {
            dst.removeIf(line -> line.contains(placeholder));
        }
    }

    @Contract()
    public static void replacePlaceholder(@NotNull String placeholder, @Nullable List<String> dst, @NotNull String src) {
        replacePlaceholder(placeholder, dst, Collections.singletonList(src), true);
    }

    @Contract()
    public static void replacePlaceholder(@NotNull String placeholder, @Nullable List<String> dst, @NotNull String src, boolean keep) {
        replacePlaceholder(placeholder, dst, Collections.singletonList(src), keep);
    }

    @Contract()
    public static void replacePlaceholder(@NotNull String placeholder, @Nullable List<String> dst, @NotNull List<String> src) {
        replacePlaceholder(placeholder, dst, src, true);
    }

    @Contract()
    public static void replacePlaceholder(@NotNull String placeholder, @Nullable List<String> dst, @NotNull List<String> src, boolean keep) {
        if (dst == null) return;

        // Let's find which line (in the dst) has the placeholder
        int placeholderIdx = -1;
        String placeholderLine = null;
        for (int i = 0; i < dst.size(); i++) {
            placeholderLine = dst.get(i);
            if (placeholderLine.contains(placeholder)) {
                placeholderIdx = i;
                break;
            }
        }
        if (placeholderIdx == -1) return;

        // Let's make the list to be inserted into the dst
        if (keep) {
            src = new ArrayList<>(src);
            ListIterator<String> it = src.listIterator();
            while (it.hasNext()) {
                String line = it.next();
                String replaced = placeholderLine.replace(placeholder, line);
                it.set(replaced);
            }
        }

        // Insert the src into the dst
        dst.remove(placeholderIdx); // Need to remove the raw placeholder from dst
        dst.addAll(placeholderIdx, src);
    }
}
