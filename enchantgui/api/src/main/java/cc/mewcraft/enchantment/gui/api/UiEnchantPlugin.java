package cc.mewcraft.enchantment.gui.api;

import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewcore.plugin.MeowJavaPlugin;
import com.google.inject.Injector;

public abstract class UiEnchantPlugin extends MeowJavaPlugin {
    public abstract Translations getLang();

    public abstract Injector getInjector();
}
