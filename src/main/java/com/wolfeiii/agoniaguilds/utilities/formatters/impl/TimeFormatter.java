package com.wolfeiii.agoniaguilds.utilities.formatters.impl;

import com.wolfeiii.agoniaguilds.configuration.message.Message;
import com.wolfeiii.agoniaguilds.utilities.formatters.IFormatter;

import java.time.Duration;
import java.util.Locale;

public class TimeFormatter implements IFormatter<Duration> {

    private static final TimeFormatter INSTANCE = new TimeFormatter();

    public static TimeFormatter getInstance() {
        return INSTANCE;
    }

    private TimeFormatter() {

    }

    @Override
    public String format(Duration value) {
        StringBuilder timeBuilder = new StringBuilder();
        {
            long days = value.toDays();
            if (days > 0) {
                formatTimeSection(timeBuilder, days, days == 1 ? Message.FORMAT_DAY_NAME : Message.FORMAT_DAYS_NAME);
                value = value.minusDays(days);
            }
        }

        {
            long hours = value.toHours();
            if (hours > 0) {
                formatTimeSection(timeBuilder, hours, hours == 1 ? Message.FORMAT_HOUR_NAME : Message.FORMAT_HOURS_NAME);
                value = value.minusHours(hours);
            }
        }

        {
            long minutes = value.toMinutes();
            if (minutes > 0) {
                formatTimeSection(timeBuilder, minutes, minutes == 1 ? Message.FORMAT_MINUTE_NAME : Message.FORMAT_MINUTES_NAME);
                value = value.minusMinutes(minutes);
            }
        }

        {
            long seconds = value.getSeconds();
            if (seconds > 0)
                formatTimeSection(timeBuilder, seconds, seconds == 1 ? Message.FORMAT_SECOND_NAME : Message.FORMAT_SECONDS_NAME);
        }

        if (timeBuilder.length() == 0) {
            timeBuilder.append("1 ").append(Message.FORMAT_SECOND_NAME.getMessage()).append(", ");
        }

        return timeBuilder.substring(0, timeBuilder.length() - 2);
    }

    private static void formatTimeSection(StringBuilder stringBuilder, long value,
                                          Message timeFormatMessage) {
        stringBuilder.append(value)
                .append(" ")
                .append(timeFormatMessage.getMessage())
                .append(", ");
    }

}