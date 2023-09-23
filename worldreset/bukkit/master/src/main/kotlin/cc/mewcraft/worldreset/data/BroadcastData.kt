package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.util.mini
import org.bukkit.plugin.Plugin

class BroadcastData(
    private val plugin: Plugin,
    messages: List<String>,
) {
    private val messages = messages.map { it.mini() }

    fun broadcast() {
        messages.forEach { plugin.server.sendMessage(it) }
    }
}