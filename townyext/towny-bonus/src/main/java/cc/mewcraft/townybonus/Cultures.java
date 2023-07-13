package cc.mewcraft.townybonus;

import cc.mewcraft.townybonus.object.culture.Culture;
import com.palmergames.util.Trie;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Cultures {

    private final TownyBonus p;
    private final Map<String, Culture> cultureMap;
    private final Trie cultureTrie;

    public Cultures(TownyBonus p) {
        this.p = p;
        this.cultureMap = new HashMap<>();
        this.cultureTrie = new Trie();
    }

    public Optional<Culture> get(String name) {
        return Optional.ofNullable(cultureMap.get(name));
    }

    public @Nullable Culture getOrNull(String name) {
        return cultureMap.get(name);
    }

    public @Nullable Culture put(@NotNull Culture culture) {
        final String cultureName = culture.getName();
        cultureTrie.addKey(cultureName);
        return cultureMap.put(cultureName, culture);
    }

    public Culture remove(String cultureName) {
        cultureTrie.removeKey(cultureName);
        return cultureMap.remove(cultureName);
    }

    public List<String> getCultureStartingWith(String arg) {
        return cultureTrie.getStringsFromKey(arg);
    }
}
