package cc.mewcraft.townyportal.mask;


import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;

import java.util.UUID;
import java.util.stream.Stream;

public class TownListMask extends IdentifiableListMask {

    public TownListMask(final MaskBuilder.Input input) {
        super(input);
    }

    @Override public Stream<UUID> getIdentifiable() {
        return TownyAPI.getInstance().getTowns()
            .stream()
            .map(Town::getMayor)
            .map(Resident::getUUID);
    }

}