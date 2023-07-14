package cc.mewcraft.mewfishing.loot.impl.condition;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import cc.mewcraft.nms.MewNMSProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@DefaultQualifier(NonNull.class)
public class BiomeCondition implements Conditioned {

    private final Set<String> biomes; // regex pattern strings
    private final List<Pattern> biomePatternList;

    public BiomeCondition(final Set<String> biomes) {
        this.biomes = biomes;
        this.biomePatternList = biomes.stream()
            .filter(s -> s.startsWith("^")) // starting with "^" to read it as regex
            .map(s -> Pattern.compile(s.substring(1)))
            .toList();
    }

    @Override public boolean evaluate(final FishLootEvent event) {
        String test = MewNMSProvider.get().biomeKey(event.getPlayer().getLocation()).asString();

        if (biomes.isEmpty()) // empty means to match any biome
            return true;

        if (biomes.contains(test)) // check if any string exactly matches
            return true;

        for (final Pattern pattern : biomePatternList) {
            if (pattern.asMatchPredicate().test(test))
                return true;
        }

        return false;
    }

}
