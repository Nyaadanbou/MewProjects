package cc.mewcraft.enchantment.gui.util

object Lores {
    private const val EMPTY_STRING = ""

    fun removePlaceholder(placeholder: String, dst: MutableList<String>, keep: Boolean = true) {
        if (keep) {
            val iterator = dst.listIterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (placeholder in next) {
                    iterator.set(next.replace(placeholder, EMPTY_STRING))
                }
            }
        } else {
            dst.removeIf { placeholder in it }
        }
    }

    fun replacePlaceholder(placeholder: String, dst: MutableList<String>, src: String, keep: Boolean = true) =
        replacePlaceholder(placeholder, dst, listOf(src), keep)

    fun replacePlaceholder(placeholder: String, dst: MutableList<String>, src: List<String>, keep: Boolean = true) {
        // Find which line (in the dst list) has the placeholder
        var indexedValue: IndexedValue<String>? = null
        for (iv in dst.withIndex()) {
            if (placeholder in iv.value) {
                indexedValue = iv
                break
            }
        }

        // Return if specified placeholder is not found
        if (indexedValue == null) return

        // Insert the src list into the dst list at the line where the placeholder presents
        dst.removeAt(indexedValue.index) // Need to remove the raw placeholder from dst
        dst.addAll(indexedValue.index, if (keep) src.map { indexedValue.value.replace(placeholder, it) } else src)
    }
}
