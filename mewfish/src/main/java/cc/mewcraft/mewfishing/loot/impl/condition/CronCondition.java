package cc.mewcraft.mewfishing.loot.impl.condition;

import cc.mewcraft.mewfishing.event.FishLootEvent;
import cc.mewcraft.mewfishing.loot.api.Conditioned;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.time.ZonedDateTime;
import java.util.List;

@DefaultQualifier(NonNull.class)
public class CronCondition implements Conditioned {
    /**
     * The unix cron syntax shall be used by default. {@code @reboot} nickname is not supported by default.
     */
    public static final CronDefinition DEFAULT_CRON_DEFINITION = CronDefinitionBuilder
        .defineCron()
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
        .instance();

    private final List<Cron> cronList;

    public CronCondition(final List<String> cronList) {
        this.cronList = cronList
            .stream()
            .map(t -> new CronParser(DEFAULT_CRON_DEFINITION).parse(t))
            .toList();
    }

    @Override public boolean evaluate(final FishLootEvent event) {
        if (cronList.isEmpty()) {
            return true;
        }

        for (final Cron cron : cronList) {
            if (ExecutionTime.forCron(cron).isMatch(ZonedDateTime.now())) {
                return true;
            }
        }

        return false;
    }
}
