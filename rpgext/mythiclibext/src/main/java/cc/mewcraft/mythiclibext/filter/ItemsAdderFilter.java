package cc.mewcraft.mythiclibext.filter;

import cc.mewcraft.mythiclibext.object.ItemsAdderStatus;
import com.google.inject.Singleton;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.lib.api.crafting.uifilters.UIFilter;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ItemsAdderFilter implements UIFilter {

    @Override public @NotNull String getIdentifier() {
        return "ia";
    }

    @Override public boolean matches(@NotNull ItemStack item, @NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
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

    @Override public boolean isValid(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
        if (!ItemsAdderStatus.complete()) {
            // We trick MythicLib, allowing this filter to be successfully registered
            return true;
        }

        if (CustomStack.getInstance(argument) != null) {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Valid ItemsAdder item found, $svalidated$b. ");
            return true;
        } else {
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "Invalid ItemsAdder item $u{0}$b. ", argument);
            return false;
        }
    }

    @Override public @NotNull ArrayList<String> tabCompleteArgument(@NotNull String argument) {
        return new ArrayList<>();
    }

    @Override public @NotNull ArrayList<String> tabCompleteData(@NotNull String argument, @NotNull String data) {
        return new ArrayList<>();
    }

    @Override public boolean fullyDefinesItem() {
        return true;
    }

    @Override public @Nullable ItemStack getItemStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
        if (!ItemsAdderStatus.complete()) {
            // An ugly solution to prevent crafting station loading errors
            return ItemFactory.of(Material.BARRIER).name("§cNot Loaded").build();
        }

        if (!isValid(argument, data, ffp)) {
            return null;
        } else {
            ItemStack itemsAdderItem = CustomStack.getInstance(argument).getItemStack();
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Successfully generated $r{0}$b. ", argument);
            return itemsAdderItem;
        }
    }

    @Override public @NotNull ItemStack getDisplayStack(@NotNull String argument, @NotNull String data, @Nullable FriendlyFeedbackProvider ffp) {
        if (!ItemsAdderStatus.complete()) {
            // An ugly solution to prevent crafting station loading errors
            return ItemFactory.of(Material.BARRIER).name("§cNot Loaded").build();
        }

        if (!isValid(argument, data, ffp)) {
            return ItemFactory.of(Material.BARRIER).name("§cInvalid Material §e" + argument).build();
        } else {
            ItemStack itemsAdderItem = CustomStack.getInstance(argument).getItemStack();
            FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.SUCCESS, "Successfully generated $r{0}$b. ", argument);
            return itemsAdderItem;
        }
    }

    @Override public @NotNull ArrayList<String> getDescription(@NotNull String argument, @NotNull String data) {
        if (!ItemsAdderStatus.complete()) {
            // An ugly solution to prevent crafting station loading errors
            return (ArrayList<String>) List.of("NULL");
        }

        if (!isValid(argument, data, null)) {
            return SilentNumbers.toArrayList("This item is $finvalid$b.");
        } else {
            ItemStack itemsAdderItem = CustomStack.getInstance(argument).getItemStack();
            return SilentNumbers.toArrayList(SilentNumbers.getItemName(itemsAdderItem));
        }
    }

    @Override public @NotNull String getSourcePlugin() {
        return "MythicLibExt";
    }

    @Override public @NotNull String getFilterName() {
        return "ItemsAdder";
    }

    @Override public @NotNull String exampleArgument() {
        return "iasurvival:ruby";
    }

    @Override public @NotNull String exampleData() {
        return "0";
    }

    @Override
    public boolean determinateGeneration() {
        return true;
    }

}
