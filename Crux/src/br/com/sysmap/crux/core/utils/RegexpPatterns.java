package br.com.sysmap.crux.core.utils;

import java.util.regex.Pattern;

public class RegexpPatterns {
    public static final Pattern REGEXP_DOT = Pattern.compile("\\.");
    public static final Pattern REGEXP_SPACE = Pattern.compile(" ");
    public static final Pattern REGEXP_PIPE = Pattern.compile("\\|");
    public static final Pattern REGEXP_LINE = Pattern.compile("_");
    public static final Pattern REGEXP_SLASH = Pattern.compile("/");
    public static final Pattern REGEXP_COMMA = Pattern.compile(",");
    public static final Pattern REGEXP_INVALID_HTML_CHARS = Pattern.compile("[<>\\&\\\"\\']");

}
