package cc.mewcraft.townyportal.mask;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;

import java.util.UUID;
import java.util.stream.Stream;

public class NationListMask extends IdentifiableListMask {

    public NationListMask(final MaskBuilder.Input input) {
        super(input);
    }

    @Override public Stream<UUID> getIdentifiable() {
        return TownyAPI.getInstance().getNations()
            .stream()
            .map(Nation::getKing)
            .map(Resident::getUUID);
    }

}
