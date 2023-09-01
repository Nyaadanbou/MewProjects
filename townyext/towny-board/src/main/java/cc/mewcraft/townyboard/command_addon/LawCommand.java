package cc.mewcraft.townyboard.command_addon;

import cc.mewcraft.townyboard.TownyBoardPlugin;
import cc.mewcraft.townyboard.board.BoardDefaults;
import cc.mewcraft.townyboard.board.GsonBoard;
import cc.mewcraft.townyboard.board.MiniMessageBoard;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import me.lucko.helper.gson.GsonProvider;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This class contains common code of all law commands.
 */
class LawCommand {
    protected static final String BOARD_CDF_KEY = "law";

    protected final TownyBoardPlugin plugin;
    protected final BoardDefaults defaults;

    protected LawCommand(final TownyBoardPlugin plugin, final BoardDefaults defaults) {
        this.plugin = plugin;
        this.defaults = defaults;
    }

    protected @Nullable Book bookInHand(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        Predicate<ItemStack> testBook = itemStack -> itemStack.getType() == Material.WRITTEN_BOOK;
        ItemStack bookItem = testBook.test(itemInMainHand) ? itemInMainHand : (testBook.test(itemInOffHand) ? itemInOffHand : null);

        if (bookItem == null || !(bookItem.getItemMeta() instanceof Book book)) {
            return null;
        }

        return book;
    }

    protected @NotNull Book readBook(@NotNull TownyObject object) {
        Objects.requireNonNull(object);

        if (object.hasMeta(BOARD_CDF_KEY)) {
            // The towny object has the gsonBoard set - read it and return the book

            // Decode: Metadata -> Byte Array -> Json String -> Object
            StringDataField metadata = Objects.requireNonNull(object.getMetadata(BOARD_CDF_KEY, StringDataField.class));
            byte[] decode = Base64.getDecoder().decode(metadata.getValue().getBytes(StandardCharsets.UTF_8));
            String decodeString = new String(decode, StandardCharsets.UTF_8);
            GsonBoard gsonBoard = GsonProvider.standard().fromJson(decodeString, GsonBoard.class);

            return readBook(gsonBoard);
        }

        // The towny object does NOT have the board set - return the default books
        if (object instanceof Town) {
            return readBook(defaults.defaultTownBoard());
        } else if (object instanceof Nation) {
            return readBook(defaults.defaultNationBoard());
        }

        // The towny object cannot be handled
        throw new IllegalArgumentException("%s is not supported".formatted(object.getClass().getSimpleName()));
    }

    protected @NotNull Book readBook(@NotNull MiniMessageBoard miniMessageBoard) {
        Objects.requireNonNull(miniMessageBoard);

        Component title = MiniMessage.miniMessage().deserialize(miniMessageBoard.title());
        Component author = MiniMessage.miniMessage().deserialize(miniMessageBoard.author());
        List<Component> pages = miniMessageBoard.pages().stream().map(MiniMessage.miniMessage()::deserialize).toList();

        return Book.book(title, author, pages);
    }

    protected @NotNull Book readBook(@NotNull GsonBoard gsonBoard) {
        Objects.requireNonNull(gsonBoard);

        Component title = GsonComponentSerializer.gson().deserialize(gsonBoard.title());
        Component author = GsonComponentSerializer.gson().deserialize(gsonBoard.author());
        List<Component> pages = gsonBoard.pages().stream().map(GsonComponentSerializer.gson()::deserialize).toList();

        return Book.book(title, author, pages);
    }

    protected @NotNull StringDataField writeDataField(@NotNull Book book) {
        Objects.requireNonNull(book);

        String title;
        String author;
        List<String> pages;

        if (book instanceof BookMeta meta) {
            // Implementation Notes: BookMeta#title() and BookMeta#author() in fact might return null
            title = GsonComponentSerializer.gson().serialize(Objects.requireNonNullElse(meta.title(), Component.empty()));
            author = GsonComponentSerializer.gson().serialize(Objects.requireNonNullElse(meta.author(), Component.empty()));
        } else {
            title = GsonComponentSerializer.gson().serialize(book.title());
            author = GsonComponentSerializer.gson().serialize(book.author());
        }
        // Book#pages() always return non-null
        pages = book.pages().stream().map(GsonComponentSerializer.gson()::serialize).toList();

        // Encode: Object -> Json String -> Base64 String -> Metadata
        GsonBoard gsonBoard = new GsonBoard(title, author, pages);
        String json = GsonProvider.standard().toJson(gsonBoard, GsonBoard.class);
        String base64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        return new StringDataField(BOARD_CDF_KEY, base64);
    }
}
