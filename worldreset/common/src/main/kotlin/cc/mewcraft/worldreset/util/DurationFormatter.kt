package cc.mewcraft.worldreset.util

import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import java.time.Duration as JDuration

// Copied from https://github.com/lucko/LuckPerms/blob/master/common/src/main/java/me/lucko/luckperms/common/util/DurationFormatter.java
// Uses String instead of Chat Components

/**
 * Formats durations to a readable form
 */
class DurationFormatter(
    private val concise: Boolean,
    private val accuracy: ChronoUnit = ChronoUnit.SECONDS,
) {
    fun format(duration: Duration): String =
        format(duration.toJavaDuration())

    /**
     * Formats `duration` as a string.
     *
     * @param duration the duration
     * @return the formatted string
     */
    fun format(duration: JDuration): String {
        var seconds = duration.seconds
        val builder = StringBuilder()
        var outputSize = 0
        for (unit in UNITS) {
            val n = seconds / unit.duration.seconds
            if (n > 0) {
                seconds -= unit.duration.seconds * n
                if (outputSize != 0) {
                    builder.append(' ')
                }
                builder.append(formatPart(n, unit))
                outputSize++
            }
            if (seconds <= 0 || unit == accuracy) {
                break
            }
        }
        return if (outputSize == 0) {
            formatPart(seconds, ChronoUnit.SECONDS)
        } else {
            builder.toString()
        }
    }

    private fun formatPart(amount: Long, unit: ChronoUnit): String {
        val format = if (concise) "short" else if (amount == 1L) "singular" else "plural"
        val translationKey = "${unit.name.lowercase()}.$format"
        return TRANSLATIONS[translationKey]?.format(amount).toString()
    }

    @Suppress("unused")
    companion object {
        val LONG = DurationFormatter(false)
        val YEARS = DurationFormatter(true, ChronoUnit.YEARS)
        val MONTHS = DurationFormatter(true, ChronoUnit.MONTHS)
        val WEEKS = DurationFormatter(true, ChronoUnit.WEEKS)
        val DAYS = DurationFormatter(true, ChronoUnit.DAYS)
        val HOURS = DurationFormatter(true, ChronoUnit.HOURS)
        val MINUTES = DurationFormatter(true, ChronoUnit.MINUTES)
        val SECONDS = DurationFormatter(true, ChronoUnit.SECONDS)

        private val UNITS = arrayOf(
            ChronoUnit.YEARS,
            ChronoUnit.MONTHS,
            ChronoUnit.WEEKS,
            ChronoUnit.DAYS,
            ChronoUnit.HOURS,
            ChronoUnit.MINUTES,
            ChronoUnit.SECONDS
        )

        private val TRANSLATIONS: Map<String, String> = buildMap {
            put("years.plural", "%s 年")
            put("years.singular", "%s 年")
            put("years.short", "%s年")
            put("months.plural", "%s 月")
            put("months.singular", "%s 月")
            put("months.short", "%s月")
            put("weeks.plural", "%s 周")
            put("weeks.singular", "%s 周")
            put("weeks.short", "%s周")
            put("days.plural", "%s 天")
            put("days.singular", "%s 天")
            put("days.short", "%s天")
            put("hours.plural", "%s 小时")
            put("hours.singular", "%s 小时")
            put("hours.short", "%s时")
            put("minutes.plural", "%s 分钟")
            put("minutes.singular", "%s 分钟")
            put("minutes.short", "%s分")
            put("seconds.plural", "%s 秒")
            put("seconds.singular", "%s 秒")
            put("seconds.short", "%s秒")
        }
    }
}