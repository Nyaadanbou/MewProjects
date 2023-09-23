package cc.mewcraft.worldreset.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

internal fun String.mini(): Component =
    MiniMessage.miniMessage().deserialize(this)

internal fun List<String>.mini(): List<Component> =
    this.map(String::mini)

internal fun String.translatable(): Component =
    Component.translatable(this)

internal fun List<String>.translatable(): List<Component> =
    this.map(String::translatable)
