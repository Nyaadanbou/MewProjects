package co.mcsky.mmoext;

public enum HookId {
    TOWNY("Towny"),
    ITEMS_ADDER("ItemsAdder"),
    MYTHIC_LIB("MythicLib"),
    MYTHIC_MOBS("MythicMobs");

    public final String plugin;

    HookId(final String plugin) {
        this.plugin = plugin;
    }
}