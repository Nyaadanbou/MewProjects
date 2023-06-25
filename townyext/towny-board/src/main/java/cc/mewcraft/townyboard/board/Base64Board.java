package cc.mewcraft.townyboard.board;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Base64Board(
    @NotNull String title,
    @NotNull String author,
    @NotNull List<String> pages
) {

}
