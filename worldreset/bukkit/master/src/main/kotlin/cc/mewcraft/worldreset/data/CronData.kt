package cc.mewcraft.worldreset.data

import com.cronutils.model.Cron
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import org.bukkit.plugin.Plugin

/**
 * The unix cron syntax shall be used by default.
 * `@reboot` nickname is not supported by default.
 */
private val DEFAULT_CRON_DEFINITION = CronDefinitionBuilder.defineCron()
    .withMinutes().withValidRange(0, 59).withStrictRange().and()
    .withHours().withValidRange(0, 23).withStrictRange().and()
    .withDayOfMonth().withValidRange(1, 31).withStrictRange().and()
    .withMonth().withValidRange(1, 12).withStrictRange().and()
    .withDayOfWeek().withValidRange(0, 7).withMondayDoWValue(1).withIntMapping(7, 0).withStrictRange().and()
    .withSupportedNicknameYearly()
    .withSupportedNicknameAnnually()
    .withSupportedNicknameMonthly()
    .withSupportedNicknameWeekly()
    .withSupportedNicknameMidnight()
    .withSupportedNicknameDaily()
    .withSupportedNicknameHourly()
    .instance()

class CronData(
    private val plugin: Plugin,
    expression: String,
) {
    val cron: Cron = CronParser(DEFAULT_CRON_DEFINITION).parse(expression)
}
