/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.utils;

import java.util.regex.Pattern;

public class RegexpPatterns {
    public static final Pattern REGEXP_DOT = Pattern.compile("\\.");
    public static final Pattern REGEXP_SPACE = Pattern.compile(" ");
    public static final Pattern REGEXP_PIPE = Pattern.compile("\\|");
    public static final Pattern REGEXP_LINE = Pattern.compile("_");
    public static final Pattern REGEXP_SLASH = Pattern.compile("/");
    public static final Pattern REGEXP_BACKSLASH = Pattern.compile("\\\\");
    public static final Pattern REGEXP_COMMA = Pattern.compile(",");
    public static final Pattern REGEXP_INVALID_HTML_CHARS = Pattern.compile("[<>\\&\\\"\\']");
    public static final Pattern REGEXP_WORD = Pattern.compile("\\w*");
    public static final Pattern REGEXP_CRUX_MESSAGE = Pattern.compile("\\$\\{\\w+\\.\\w+\\}");
    public static final Pattern REGEXP_CRUX_RESOURCE = Pattern.compile("\\$\\{\\w[\\.\\w]+\\}");
    public static final Pattern REGEXP_CONTEXT = Pattern.compile("/\\{context\\}");
}
