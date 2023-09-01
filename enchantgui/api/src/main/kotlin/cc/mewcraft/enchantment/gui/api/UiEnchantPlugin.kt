package cc.mewcraft.enchantment.gui.api

import cc.mewcraft.mewcore.message.Translations
import cc.mewcraft.mewcore.plugin.MeowJavaPlugin
import com.google.inject.Injector

abstract class UiEnchantPlugin : MeowJavaPlugin() {
    lateinit var languages: Translations
    lateinit var injector: Injector
}
