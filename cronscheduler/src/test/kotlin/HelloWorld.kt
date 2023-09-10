import com.cronutils.*
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import org.junit.jupiter.api.Test
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
        val job = CronJob.create("hello") {
            println("Hello world! (${ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)})")
            ExecutionStatus.SUCCESS
        }
        val trigger = CronTrigger.create(
            CronParser(definitionWithoutSeconds).parse("* * * SEP SAT")
        )

        scheduler.scheduleCronJob(ExecutableUnit(job, trigger))
        scheduler.startPollingTask()

        Thread.sleep(Duration.ofMinutes(5).toMillis())
    }
}