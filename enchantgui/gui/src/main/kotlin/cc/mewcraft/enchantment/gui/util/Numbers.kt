package cc.mewcraft.enchantment.gui.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object Numbers {
    private val FORMAT_ROUND_HUMAN: DecimalFormat = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale.ENGLISH))
    private val ROMAN_MAP = TreeMap<Int, String>().apply {
        set(1000, "M")
        set(900, "CM")
        set(500, "D")
        set(400, "CD")
        set(100, "C")
        set(90, "XC")
        set(50, "L")
        set(40, "XL")
        set(10, "X")
        set(9, "IX")
        set(5, "V")
        set(4, "IV")
        set(1, "I")
    }

    fun format(value: Double): String =
        FORMAT_ROUND_HUMAN.format(value)

    fun round(value: Double): Double =
        BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()

    @Suppress("MemberVisibilityCanBePrivate")
    fun roman(number: Int): String {
        if (number <= 0) return number.toString()
        val key = ROMAN_MAP.floorKey(number)
        return if (number == key)
            ROMAN_MAP[number] ?: "?" else
            ROMAN_MAP[key] + roman(number - key)
    }

    fun splitIntoParts(whole: Int, parts: Int): IntArray {
        val arr = IntArray(parts)
        var remain = whole
        var partsLeft = parts
        var i = 0
        while (partsLeft > 0) {
            val size = (remain + partsLeft - 1) / partsLeft // rounded up, aka ceiling
            arr[i] = size
            remain -= size
            partsLeft--
            i++
        }
        return arr
    }
}
