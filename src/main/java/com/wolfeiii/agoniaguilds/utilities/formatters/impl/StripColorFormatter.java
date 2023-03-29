package com.wolfeiii.agoniaguilds.utilities.formatters.impl;

import com.wolfeiii.agoniaguilds.utilities.formatters.IFormatter;
import com.wolfeiii.agoniaguilds.utilities.text.Text;

import java.util.regex.Pattern;

public class StripColorFormatter implements IFormatter<String> {

    private static final StripColorFormatter INSTANCE = new StripColorFormatter();

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)([&§])[0-9A-FK-OR]");

    public static StripColorFormatter getInstance() {
        return INSTANCE;
    }

    private StripColorFormatter() {

    }

    @Override
    public String format(String value) {
        return Text.isBlank(value) ? "" : STRIP_COLOR_PATTERN.matcher(value).replaceAll("");
    }

}