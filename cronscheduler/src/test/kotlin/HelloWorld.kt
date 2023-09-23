import com.cronutils.CronScheduler
import com.cronutils.ExecutionStatus
import com.cronutils.model.Cron
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import org.junit.Test
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class HelloWorld {
    private val definitionWithoutSeconds = CronDefinitionBuilder.defineCron()
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
    private val definitionWithSeconds = CronDefinitionBuilder.defineCron()
        .withSeconds().withValidRange(0, 59).withStrictRange().and()
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

    @Test
    fun test() {
        val scheduler = CronScheduler()

        val name1 = "cron1"
        val name2 = "cron2"
        val name3 = "cron3"
        val cron1 = CronParser(definitionWithoutSeconds).parse("10 * * SEP THU")
        val cron2 = CronParser(definitionWithoutSeconds).parse("11 * * SEP THU")
        val cron3 = CronParser(definitionWithoutSeconds).parse("12 * * SEP THU")

        scheduler.scheduleCronJob("cron1", cron1) {
            printExecution(name1, cron1)
            ExecutionStatus.SUCCESS
        }
        scheduler.scheduleCronJob("cron2", cron2) {
            printExecution(name2, cron2)
            ExecutionStatus.SUCCESS
        }
        scheduler.scheduleCronJob("cron3", cron3) {
            printExecution(name3, cron3)
            ExecutionStatus.SUCCESS
        }

        scheduler.startPollingTask()

        Thread.sleep(Duration.ofMinutes(3).toMillis())
    }

    private fun printExecution(name: String, cron: Cron) {
        println("$name - ${cron.asString()} - ${ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)}")
    }
}