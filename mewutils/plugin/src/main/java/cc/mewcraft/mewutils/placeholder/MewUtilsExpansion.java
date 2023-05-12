package cc.mewcraft.mewutils.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class MewUtilsExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "mewutils";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Nailm";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1.0";
    }

}
