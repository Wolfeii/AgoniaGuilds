package com.wolfeiii.agoniaguilds.utilities.formatters.impl;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.guild.Guild;
import com.wolfeiii.agoniaguilds.utilities.formatters.IFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter implements IFormatter<Date> {

    private static final DateFormatter INSTANCE = new DateFormatter();

    private static SimpleDateFormat dateFormatter;

    public static void setDateFormatter(AgoniaGuilds plugin, String dateFormat) {
        dateFormatter = new SimpleDateFormat(dateFormat);
        try {
            for (Guild guild : plugin.getGuildManager().getGuilds()) {
                guild.updateDatesFormatter();
            }
        } catch (Exception ignored) {
        }
    }

    public static DateFormatter getInstance() {
        return INSTANCE;
    }

    private DateFormatter() {

    }

    @Override
    public String format(Date value) {
        return dateFormatter.format(value);
    }
}
