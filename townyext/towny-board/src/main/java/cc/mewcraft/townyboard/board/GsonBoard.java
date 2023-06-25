package cc.mewcraft.townyboard.board;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * All string data are saved in Gson representation.
 */
public record GsonBoard(
    @NotNull String title,
    @NotNull String author,
    @NotNull List<String> pages
) {

}