package cc.mewcraft.townyboard.board;

import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import me.lucko.helper.gson.GsonProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoardDataField extends CustomDataField<GsonBoard> {
    public BoardDataField(final String key) {
        super(key);
    }

    public BoardDataField(final String key, final GsonBoard value) {
        super(key, value);
    }

    public BoardDataField(final String key, final String label) {
        super(key, label);
    }

    public BoardDataField(final String key, final GsonBoard value, final String label) {
        super(key, value, label);
    }

    @Override public @NotNull String getTypeID() {
        return typeID();
    }

    public static String typeID() {
        return "townyboard_board";
    }

    @Override public void setValueFromString(final String strValue) {
        GsonBoard gsonBoard = GsonProvider.standard().fromJson(strValue, GsonBoard.class);
        setValue(gsonBoard);
    }

    @Override protected @Nullable String serializeValueToString() {
        return GsonProvider.standard().toJson(getValue(), GsonBoard.class);
    }

    @Override protected String displayFormattedValue() {
        return getValue().title();
    }

    @Override public @NotNull CustomDataField<GsonBoard> clone() {
        return new BoardDataField(getKey(), getValue(), label);
    }
}
