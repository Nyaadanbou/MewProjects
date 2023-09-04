package cc.mewcraft.enchantment.gui.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper
import xyz.xenondevs.inventoryaccess.component.i18n.AdventureComponentLocalizer
import xyz.xenondevs.inventoryaccess.component.i18n.Languages

class AdventureComponentWrapper(
    component: Component,
) : ComponentWrapper {
    private val component = component.compact() // Make it compact to reduce network traffic volume

    override fun serializeToJson(): String =
        GsonComponentSerializer.gson().serialize(component)

    override fun localized(lang: String): ComponentWrapper =
        if (!Languages.getInstance().doesServerSideTranslations()) {
            this
        } else {
            AdventureComponentWrapper(AdventureComponentLocalizer.getInstance().localize(lang, component))
        }

    override fun withoutPreFormatting(): ComponentWrapper =
        AdventureComponentWrapper(component.style {
            // Do not add any unnecessary style
            it.decoration(TextDecoration.ITALIC, false)
        })

    override fun clone(): ComponentWrapper =
        AdventureComponentWrapper(component)
}
