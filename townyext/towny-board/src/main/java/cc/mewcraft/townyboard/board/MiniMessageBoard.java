package cc.mewcraft.townyboard.board;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * All string data are saved in MiniMessage representation.
 */
public record MiniMessageBoard(
    @NotNull String title,
    @NotNull String author,
    @NotNull List<String> pages
) {
}
