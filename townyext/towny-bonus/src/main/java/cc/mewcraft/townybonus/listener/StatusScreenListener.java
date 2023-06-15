package cc.mewcraft.townybonus.listener;

import cc.mewcraft.townybonus.object.culture.Culture;
import cc.mewcraft.townybonus.TownyBonus;
import cc.mewcraft.townybonus.util.UtilCulture;
import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.event.ClickEvent;
import com.palmergames.adventure.text.event.HoverEvent;
import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.event.statusscreen.NationStatusScreenEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.Translation;
import com.palmergames.bukkit.towny.object.statusscreens.StatusScreen;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import org.jetbrains.annotations.NotNull;

public class StatusScreenListener implements TerminableModule {

    private static String colourBracketElement(String key, String value) {
        return TownyFormatter.bracketFormat.formatted(Translation.of("status_format_bracket_element"), key, value);
    }

    private static String colourHoverKey(String key) {
        return TownyFormatter.hoverFormat.formatted(Translation.of("status_format_hover_bracket_colour"), Translation.of("status_format_hover_key"), key, Translation.of("status_format_hover_bracket_colour"));
    }

    private static Component cultureKey(Culture culture) {
        // make plain text
        String plainText = colourHoverKey(TownyBonus.lang().get("screen.plainText", "culture", culture.getName()));

        // make hover event
        String hoverText = TownyBonus.lang().get("screen.hoverText");
        HoverEvent<Component> hoverEvent = HoverEvent.showText(Component.text(hoverText));

        // make click event
        ClickEvent clickEvent = ClickEvent.openUrl(culture.getUrl());

        return Component.text(plainText).hoverEvent(hoverEvent).clickEvent(clickEvent);
    }

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {
        Events.subscribe(NationStatusScreenEvent.class).handler(e -> {
            Culture culture = UtilCulture.cultureOfNullable(e.getNation());
            if (culture != null) {
                StatusScreen screen = e.getStatusScreen();
                screen.addComponentOf("culture", cultureKey(culture));
            }
        }).bindWith(consumer);

        Events.subscribe(TownStatusScreenEvent.class).handler(e -> {
            Culture culture = UtilCulture.cultureOfNullable(e.getTown());
            if (culture != null) {
                StatusScreen screen = e.getStatusScreen();
                screen.addComponentOf("culture", cultureKey(culture));
            }
        }).bindWith(consumer);
    }

}
