package com.wolfeiii.agoniaguilds.utilities.formatters;

import com.wolfeiii.agoniaguilds.utilities.formatters.impl.ColorFormatter;
import com.wolfeiii.agoniaguilds.utilities.formatters.impl.StripColorFormatter;

import java.util.List;
import java.util.stream.Collectors;

public class Formatters {

    public static final IFormatter<String> COLOR_FORMATTER = ColorFormatter.getInstance();
    public static final IFormatter<String> STRIP_COLOR_FORMATTER = StripColorFormatter.getInstance();

    private Formatters() {

    }

    public static <T> List<String> formatList(List<T> list, IFormatter<T> formatter) {
        return list.stream().map(formatter::format).collect(Collectors.toList());
    }

}
