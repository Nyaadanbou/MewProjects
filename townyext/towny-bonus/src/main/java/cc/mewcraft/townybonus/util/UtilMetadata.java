package cc.mewcraft.townybonus.util;

import cc.mewcraft.townybonus.CultureMetadata;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UtilMetadata {

    public static void addCulture(@NotNull Nation nation, @NotNull String culture) {
        final CustomDataField<String> clone = CultureMetadata.FIELD_CULTURE.clone();
        clone.setValueFromString(culture);
        nation.addMetaData(clone, true);
    }

    @Nullable
    public static String getCulture(@NotNull Nation nation) {
        // Fix NPE from running /towny prices
        if (nation != null && nation.hasMeta(CultureMetadata.FIELD_CULTURE.getKey(), StringDataField.class)) {
            final StringDataField metadata = nation.getMetadata(CultureMetadata.KEY_CULTURE, StringDataField.class);
            if (metadata != null) {
                return metadata.getValue();
            }
        }
        return null;
    }

    public static void updateCulture(@NotNull Nation nation, @NotNull String culture) {
        if (!nation.hasMeta(CultureMetadata.FIELD_CULTURE.getKey(), StringDataField.class)) {
            addCulture(nation, culture);
        }
        final StringDataField metadata = nation.getMetadata(CultureMetadata.FIELD_CULTURE.getKey(), StringDataField.class);
        if (metadata != null) {
            metadata.setValueFromString(culture);
        }
    }

    public static void removeCulture(@NotNull Nation nation) {
        nation.removeMetaData(CultureMetadata.FIELD_CULTURE);
    }
}
