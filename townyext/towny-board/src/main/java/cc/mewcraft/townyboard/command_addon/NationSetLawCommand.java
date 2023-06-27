package cc.mewcraft.townyboard.command_addon;

import cc.mewcraft.townyboard.TownyBoardPlugin;
import cc.mewcraft.townyboard.board.BoardDefaults;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.palmergames.adventure.key.Key;
import com.palmergames.adventure.sound.Sound;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Translation;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import net.kyori.adventure.inventory.Book;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Singleton
public class NationSetLawCommand extends LawCommand implements CommandExecutor {
    @Inject
    public NationSetLawCommand(final TownyBoardPlugin plugin, final BoardDefaults defaults) {
        super(plugin, defaults);
    }

    @Override public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        Nation nation = TownyAPI.getInstance().getNation(player);
        if (nation == null) {
            plugin.getLang().of("msg_you_have_no_nation").send(player);
            return true;
        }

        if (!TownyUniverse.getInstance().getPermissionSource().testPermission(player, "townyboard.command.nation.set.law")) {
            TownyMessaging.sendErrorMsg(player, Translation.of("msg_err_command_disable"));
            ;
            return true;
        }

        Book book = bookInHand(player);
        if (book == null) {
            plugin.getLang().of("msg_you_have_no_book_in_hand").send(player);
            return true;
        }

        StringDataField boardDataField = writeDataField(book);
        nation.addMetaData(boardDataField);
        TownyMessaging.sendPrefixedNationMessage(nation, plugin.getLang().of("msg_nation_board_updated").plain());

        @Subst("minecraft:block.anvil.use")
        String sound = Objects.requireNonNull(plugin.getConfig().getString("sounds.nation.sound"));
        float volume = (float) plugin.getConfig().getDouble("sounds.nation.volume", 1f);
        float pitch = (float) plugin.getConfig().getDouble("sounds.nation.pitch", 1f);
        nation.playSound(Sound.sound(Key.key(sound), Sound.Source.MASTER, volume, pitch));

        return true;
    }
}
