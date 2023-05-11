package co.mcsky.mmoext;

import co.mcsky.mmoext.config.loader.SummonItemLoader;
import co.mcsky.mmoext.object.SummonItem;

import java.util.*;

public class MMOConfig {

    private final Main p;

    private final Map<String, SummonItem> summonItemMap;
    private final Set<String> summonMobIds;

    public MMOConfig(Main p) {
        this.p = p;
        this.summonItemMap = new HashMap<>();
        this.summonMobIds = new HashSet<>();
    }

    public void loadDefaultConfig() {
        p.saveDefaultConfig();
        p.reloadConfig();
    }

    public void loadSummonItems() {
        // Put map
        summonItemMap.clear();
        Set<SummonItem> itemSet = new SummonItemLoader().readAll();
        itemSet.forEach(i -> summonItemMap.put(i.getItemId(), i));

        // Put set
        summonMobIds.clear();
        summonItemMap.forEach((k, v) -> summonMobIds.add(v.getMobId()));
    }

    public boolean getDebug() {
        return p.getConfig().getBoolean("debug");
    }

    public Set<String> getSummonMobIds() {
        return summonMobIds;
    }

    public Collection<SummonItem> getSummonItems() {
        return summonItemMap.values();
    }

    public Optional<SummonItem> getSummonItem(String itemId) {
        return Optional.ofNullable(summonItemMap.get(itemId));
    }

    public String getDamageFormat() {
        return p.getConfig().getString("damage.format");
    }

}
