package cc.mewcraft.enchantment.gui.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper

internal fun String.miniMessage(): Component =
    MiniMessage.miniMessage().deserialize(this)

internal fun List<String>.miniMessage(): List<Component> =
    this.map(String::miniMessage)

internal fun String.translatable(): Component =
    Component.translatable(this)

internal fun List<String>.translatable(): List<Component> =
    this.map(String::translatable)

internal fun Component.wrapper(): ComponentWrapper =
    AdventureComponentWrapper(this)

internal fun List<Component>.wrapper(): List<ComponentWrapper> =
    this.map(Component::wrapper)
