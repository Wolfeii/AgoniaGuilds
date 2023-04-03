package com.wolfeiii.agoniaguilds.utilities.formatters;

import com.wolfeiii.agoniaguilds.utilities.formatters.impl.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Formatters {

    public static final IFormatter<String> COLOR_FORMATTER = ColorFormatter.getInstance();
    public static final IFormatter<String> STRIP_COLOR_FORMATTER = StripColorFormatter.getInstance();
    public static final IFormatter<Number> NUMBER_FORMATTER = NumberFormatter.getInstance();
    public static final IFormatter<Duration> TIME_FORMATTER = TimeFormatter.getInstance();
    public static final IFormatter<Date> DATE_FORMATTER = DateFormatter.getInstance();

    private Formatters() {

    }

    public static <T> List<String> formatList(List<T> list, IFormatter<T> formatter) {
        return list.stream().map(formatter::format).collect(Collectors.toList());
    }

}
