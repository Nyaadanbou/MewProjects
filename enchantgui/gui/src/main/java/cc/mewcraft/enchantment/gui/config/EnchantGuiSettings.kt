package cc.mewcraft.enchantment.gui.config

import cc.mewcraft.enchantment.gui.EnchantGuiPlugin
import com.google.inject.Inject
import com.google.inject.Singleton
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material

@Singleton
class EnchantGuiSettings
@Inject constructor(
    plugin: EnchantGuiPlugin,
) {
    val guiLayout: Array<String> = plugin.config.getStringList("gui.layout").toTypedArray()

    val itemMaterial: Material = Material.matchMaterial(plugin.config.getString("gui.icon.material")!!)!!
    val displayNameFormat: String = plugin.config.getString("gui.icon.name")!!

    val loreFormat: MutableList<String> = plugin.config.getStringList("gui.icon.lore")
        get() = field.toMutableList()
    val loreFormatCharging: MutableList<String> = plugin.config.getStringList("gui.lore.charging")
        get() = field.toMutableList()
    val loreFormatConflict: MutableList<String> = plugin.config.getStringList("gui.lore.conflict")
        get() = field.toMutableList()
    val loreFormatObtaining: MutableList<String> = plugin.config.getStringList("gui.lore.obtaining")
        get() = field.toMutableList()

    private fun createSound(key: String?): Sound = Sound.sound(Key.key(key!!), Sound.Source.MASTER, 1f, 1f)
    val openSound: Sound = createSound(plugin.config.getString("sound.open"))
    val switchSound: Sound = createSound(plugin.config.getString("sound.switch"))
    val pageTurnSound: Sound = createSound(plugin.config.getString("sound.page_turn"))
    val testSound: Sound = createSound(plugin.config.getString("sound.test"))
}
