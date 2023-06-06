package co.mcsky.mmoext.item;

import co.mcsky.mmoext.RPGBridge;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import io.lumine.mythic.lib.api.crafting.uifilters.UIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdderUIFilter implements UIFilter {

    private static List<String> itemNames;
    private static ItemsAdderUIFilter instance;

    public static List<String> getItemNames() {
        if (itemNames == null) {
            itemNames = ItemsAdder.getAllItems().stream().map(CustomStack::getNamespacedID).toList();
        }
        return itemNames;
    }

    public static void register() {
        instance = new ItemsAdderUIFilter();
        UIFilterManager.registerUIFilter(instance);
    }

    public static ItemsAdderUIFilter getInstance() {
        return instance;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "ia";
    }

    @Override
    public boolean matches(@NotNull ItemStack item, @NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
        if (!isValid(argument, data, ffp)) {
            return false; // is argument valid?
        } else if (cancelMatch(item, ffp)) {
            return false; // is counter matched?
        } else {
            CustomStack customStack = CustomStack.byItemStack(item);
            if (customStack != null) {
                return customStack.getNamespacedID().equalsIgnoreCase(argument);
            }
            return false;
        }
    }

    @Override
    public boolean isValid(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
        if (!RPGBridge.ITEMSADDER_LOADED) {
            // TODO trick MythicLib, allowing ItemsAdder UIFilter to successfully register
            return true;
        }

        if (CustomStack.getInstance(argument) != null) {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Valid ItemsAdder item found, $svalidated$b. ");
            return true;
        }
        FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Invalid ItemsAdder item $u{0}$b. ", argument);
        return false;
    }

    @NotNull
    @Override
    public ArrayList<String> tabCompleteArgument(@NotNull String argument) {
        return SilentNumbers.smartFilter((ArrayList<String>) getItemNames(), argument, true);
    }

    @NotNull
    @Override
    public ArrayList<String> tabCompleteData(@NotNull String argument, @NotNull String data) {
        return SilentNumbers.toArrayList("0", "(this_is_not_checked,_write_anything)");
    }

    @Override
    public boolean fullyDefinesItem() {
        return true;
    }

    @Nullable
    @Override
    public ItemStack getItemStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
        if (!RPGBridge.ITEMSADDER_LOADED) {
            // TODO ugly solution to prevent crafting station loading errors
            return ItemStackBuilder.of(Material.BARRIER).name("ITEMSADDER NOT LOADED").color(Color.RED).build();
        }

        if (!isValid(argument, data, ffp)) {
            return null;
        } else {
            ItemStack itemsAdderItem = CustomStack.getInstance(argument).getItemStack();
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Successfully generated $r{0}$b. ", argument);
            return itemsAdderItem;
        }
    }

    @NotNull
    @Override
    public ItemStack getDisplayStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
        if (!RPGBridge.ITEMSADDER_LOADED) {
            // TODO ugly solution to prevent crafting station loading errors
            return ItemStackBuilder.of(Material.BARRIER).name("ITEMSADDER NOT LOADED").color(Color.RED).build();
        }

        if (!isValid(argument, data, ffp)) {
            return ItemFactory.of(Material.BARRIER).name("§cInvalid Material §e" + argument).build();
        } else {
            ItemStack itemsAdderItem = CustomStack.getInstance(argument).getItemStack();
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Successfully generated $r{0}$b. ", argument);
            return itemsAdderItem;
        }
    }

    @NotNull
    @Override
    public ArrayList<String> getDescription(@NotNull String argument, @NotNull String data) {
        if (!RPGBridge.ITEMSADDER_LOADED) {
            // TODO ugly solution to prevent crafting station loading errors
            return (ArrayList<String>) List.of("ITEMSADDER NOT LOADED");
        }

        if (!isValid(argument, data, null)) {
            return SilentNumbers.toArrayList("This ItemsAdder item is $finvalid$b.");
        } else {
            ItemStack itemsAdderItem = CustomStack.getInstance(argument).getItemStack();
            return SilentNumbers.toArrayList(SilentNumbers.getItemName(itemsAdderItem));
        }
    }

    @NotNull
    @Override
    public String getSourcePlugin() {
        return "MMOExt";
    }

    @NotNull
    @Override
    public String getFilterName() {
        return "ItemsAdder";
    }

    @NotNull
    @Override
    public String exampleArgument() {
        return "iasurvival:ruby";
    }

    @NotNull
    @Override
    public String exampleData() {
        return "0";
    }

    @Override
    public boolean determinateGeneration() {
        return true;
    }
}
