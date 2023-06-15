package cc.mewcraft.townyportal;

import cc.mewcraft.townyportal.mask.NationListMask;
import cc.mewcraft.townyportal.mask.TownListMask;
import me.hsgamer.bettergui.lib.core.bukkit.addon.PluginAddon;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;

public class TownyPortal extends PluginAddon {

    @Override public void onEnable() {
        MaskBuilder.INSTANCE.register(TownListMask::new, "town-list", "townlist", "towns");
        MaskBuilder.INSTANCE.register(NationListMask::new, "nation-list", "nationlist", "nations");
    }

}
