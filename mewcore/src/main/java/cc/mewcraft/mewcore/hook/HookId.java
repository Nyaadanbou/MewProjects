package cc.mewcraft.mewcore.hook;

public enum HookId {

    VAULT("Vault"),
    PLACEHOLDER_API("PlaceholderAPI"),
    BREWERY("Brewery"),
    INTERACTIVE_BOOKS("InteractiveBooks"),
    ITEMS_ADDER("ItemsAdder"),
    MMOITEMS("MMOItems"),
    TOWNY("Towny");

    public final String pluginName;

    HookId(final String pluginName) {this.pluginName = pluginName;}

}
