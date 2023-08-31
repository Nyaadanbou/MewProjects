package cc.mewcraft.enchantment.gui.adapter;

import cc.mewcraft.enchantment.gui.api.ChargeableUiEnchant;
import cc.mewcraft.enchantment.gui.api.UiEnchant;
import cc.mewcraft.enchantment.gui.api.UiEnchantAdapter;
import cc.mewcraft.enchantment.gui.api.UiEnchantPlugin;
import cc.mewcraft.enchantment.gui.api.UiEnchantProvider;
import cc.mewcraft.enchantment.gui.api.UiEnchantRarity;
import cc.mewcraft.enchantment.gui.api.UiEnchantTarget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentenchants.enchantment.EnchantRegistry;
import su.nightexpress.excellentenchants.enchantment.impl.ExcellentEnchant;
import su.nightexpress.excellentenchants.enchantment.type.FitItemType;
import su.nightexpress.excellentenchants.enchantment.type.ObtainType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class ExcellentEnchantAdapter implements UiEnchantAdapter<ExcellentEnchant, FitItemType> {
    private final UiEnchantPlugin plugin;

    @Inject
    public ExcellentEnchantAdapter(UiEnchantPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        if (!canInitialize()) {
            return;
        }

        for (final ExcellentEnchant enchant : EnchantRegistry.getRegistered()) {
            UiEnchantProvider.register(adaptEnchantment(enchant));
        }
    }

    @Override public boolean canInitialize() {
        return plugin.isPluginPresent("ExcellentEnchants");
    }

    @Override
    public @NotNull UiEnchant adaptEnchantment(@NotNull ExcellentEnchant enchant) {
        UiEnchant enchantment = new UiEnchant() {
            @Override public @NotNull String name() {
                return enchant.getDisplayName();
            }

            @Override public @NotNull Map<Integer, String> displayName() {
                return scaleMapper(enchant::getNameFormatted);
            }

            @Override public @NotNull Map<Integer, List<String>> description() {
                return scaleMapper(level -> enchant.getDescription(level).stream().toList());
            }

            @Override public boolean canEnchantment(final @NotNull ItemStack item) {
                return enchant.canEnchantItem(item);
            }

            @Override public @NotNull List<UiEnchantTarget> enchantmentTargets() {
                return Arrays.stream(enchant.getFitItemTypes()).map(it -> adaptEnchantmentTarget(it)).toList();
            }

            @Override public @NotNull UiEnchantRarity rarity() {
                return new UiEnchantRarity(enchant.getTier().getName(), enchant.getTier().getColor());
            }

            @Override public double enchantingChance() {
                return enchant.getObtainChance(ObtainType.ENCHANTING);
            }

            @Override public double villagerTradeChance() {
                return enchant.getObtainChance(ObtainType.VILLAGER);
            }

            @Override public double lootGenerationChance() {
                return enchant.getObtainChance(ObtainType.LOOT_GENERATION);
            }

            @Override public double fishingChance() {
                return enchant.getObtainChance(ObtainType.FISHING);
            }

            @Override public double mobSpawningChance() {
                return enchant.getObtainChance(ObtainType.MOB_SPAWNING);
            }

            @Override public @NotNull List<@NotNull UiEnchant> conflict() {
                return enchant.getConflicts().stream().map(UiEnchantProvider::get).filter(Objects::nonNull).toList();
            }

            @Override public boolean conflictsWith(final @NotNull Enchantment other) {
                return enchant.conflictsWith(other);
            }

            @Override public int minimumLevel() {
                return enchant.getStartLevel();
            }

            @Override public int maximumLevel() {
                return enchant.getMaxLevel();
            }

            @Override public @NotNull Key key() {
                return enchant.key();
            }
        };

        if (enchant.isChargesEnabled()) {
            MiniMessage miniMessage = MiniMessage.builder().strict(true).build(); // to generate closed tags

            return new ChargeableUiEnchant(
                enchantment,
                Optional.of(enchant.getChargesFuel().getItem())
                    .map(item -> {
                        ItemMeta itemMeta = item.getItemMeta();
                        // display name may be null
                        return itemMeta.hasDisplayName() ? itemMeta.displayName() : Component.translatable(item);
                    })
                    .map(miniMessage::serialize)
                    .orElseThrow(),
                enchant::getChargesConsumeAmount,
                enchant::getChargesRechargeAmount,
                enchant::getChargesMax
            );
        }

        return enchantment;
    }

    @Override
    public @NotNull UiEnchantTarget adaptEnchantmentTarget(final @NotNull FitItemType enchantmentTarget) {
        return switch (enchantmentTarget) {
            case UNIVERSAL -> UiEnchantTarget.ALL;
            case HELMET -> UiEnchantTarget.HELMET;
            case CHESTPLATE -> UiEnchantTarget.CHESTPLATE;
            case LEGGINGS -> UiEnchantTarget.LEGGINGS;
            case BOOTS -> UiEnchantTarget.BOOTS;
            case ELYTRA -> UiEnchantTarget.ELYTRA;
            case WEAPON -> UiEnchantTarget.WEAPON;
            case TOOL -> UiEnchantTarget.TOOL;
            case ARMOR -> UiEnchantTarget.ARMOR;
            case SWORD -> UiEnchantTarget.SWORD;
            case TRIDENT -> UiEnchantTarget.TRIDENT;
            case AXE -> UiEnchantTarget.AXE;
            case BOW -> UiEnchantTarget.BOW;
            case CROSSBOW -> UiEnchantTarget.CROSSBOW;
            case HOE -> UiEnchantTarget.HOE;
            case PICKAXE -> UiEnchantTarget.PICKAXE;
            case SHOVEL -> UiEnchantTarget.SHOVEL;
            case FISHING_ROD -> UiEnchantTarget.FISHING_ROD;
        };
    }
}
