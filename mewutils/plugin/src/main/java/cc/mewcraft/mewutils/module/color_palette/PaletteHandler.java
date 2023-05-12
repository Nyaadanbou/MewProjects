package cc.mewcraft.mewutils.module.color_palette;

import cc.mewcraft.mewcore.util.PDCUtils;
import me.lucko.helper.item.ItemStackBuilder;
import net.kyori.adventure.text.format.TextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class PaletteHandler<E extends Entity> {

    private final ColorPaletteModule module;

    public PaletteHandler(ColorPaletteModule module) {
        this.module = module;
    }

    public static boolean hasAccess(Player player, Entity base) {
        Optional<UUID> uuidAtEntity = PDCUtils.get(base, PDCUtils.UUID, PaletteConstants.OWNER);
        return uuidAtEntity
            // A UUID is stored in the entity,
            // so checks if player is the owner
            .map(value -> player.getUniqueId().equals(value))
            // No UUID is stored in the entity,
            // we always return true for this
            .orElse(true);
    }


    abstract public @NonNull Color getColor(@NonNull E base);

    abstract public void setColor(@NonNull E base, @NonNull Color color);

    abstract public boolean canHandle(@NonNull Entity base);

    /**
     * The entry point of dyeing furniture.
     *
     * @param player the player who is dyeing this furniture
     * @param base   the entity holding the furniture item stack
     */
    @SuppressWarnings("unchecked")
    public void startEdit(final Player player, Entity base) {
        AnvilGUI.Builder builder = new AnvilGUI.Builder();

        // this is required to register listeners
        builder.plugin(this.module.getParentPlugin());

        // set the title
        builder.title(this.module.getLang().of("gui.input_color").plain());

        // set the placeholder item
        Color color = getColor((E) base);
        ItemStack build = ItemStackBuilder
            .of(Material.PAPER)
            .name(this.module.getLang()
                .of("gui.left_item_name")
                .replace("input", TextColor.color(color.asRGB()).asHexString())
                .plain()
            ).build();
        builder.itemLeft(build);

        // actions on close
        builder.onClose(p -> this.module.getLang().of("gui.closed").send(p));

        // actions on complete
        builder.onComplete(completion -> {
            String text = completion.getText();

            // Validate input
            TextColor inputColor = TextColor.fromHexString(text);
            if (text.length() != 7 || inputColor == null) {
                this.module.getLang().of("msg.incorrect_rgb").replace("input", text).send(player);
                return List.of(AnvilGUI.ResponseAction.close());
            }

            // Apply the color
            return List.of(
                AnvilGUI.ResponseAction.run(() -> {
                    setColor((E) base, Color.fromRGB(inputColor.value())); // Apply input color to the item

                    PDCUtils.set(base, PaletteConstants.OWNER, player.getUniqueId()); // Update the owner of the item

                    this.module.getLang().of("msg.prop_dyed").replace("input", inputColor.asHexString()).send(player);
                }),
                AnvilGUI.ResponseAction.close()
            );
        });

        // open the inventory for player
        builder.open(player);
    }

}
