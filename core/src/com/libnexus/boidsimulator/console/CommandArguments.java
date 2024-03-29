package com.libnexus.boidsimulator.console;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandArguments {
    public static final Pattern FLOAT_ATTRIBUTE = Pattern.compile("^([a-zA-Z:_]+)=(\\d+\\.\\d+)");
    public static final Pattern INT_ATTRIBUTE = Pattern.compile("^([a-zA-Z:_]+)=(\\d+)");
    public static final Pattern STRING_ATTRIBUTE = Pattern.compile("(^[a-zA-Z:_]+)=([a-zA-Z_.()]+)");
    public final String STRING_ARGUMENTS;
    public final HashMap<String, Float> floatAttributes = new HashMap<>();
    public final HashMap<String, Integer> intAttributes = new HashMap<>();
    public final HashMap<String, String> stringAttributes = new HashMap<>();
    public final int PARSE_HALT;


    public CommandArguments(String arguments) {
        STRING_ARGUMENTS = arguments;

        Matcher matcher;

        for (; ; ) {
            if ((matcher = FLOAT_ATTRIBUTE.matcher(arguments)).find()) {
                floatAttributes.put(matcher.group(1), Float.parseFloat(matcher.group(2)));
                arguments = arguments.substring(matcher.group().length());
            } else if ((matcher = INT_ATTRIBUTE.matcher(arguments)).find()) {
                intAttributes.put(matcher.group(1), Integer.parseInt(matcher.group(2)));
                arguments = arguments.substring(matcher.group().length());
            } else if ((matcher = STRING_ATTRIBUTE.matcher(arguments)).find()) {
                stringAttributes.put(matcher.group(1), matcher.group(2));
                arguments = arguments.substring(matcher.group().length());
            }

            if (!arguments.isEmpty() && arguments.charAt(0) == '&') {
                arguments = arguments.substring(1);
            } else {
                break;
            }
        }

        PARSE_HALT = STRING_ARGUMENTS.length() - arguments.length();
    }

    public boolean isEmpty() {
        return floatAttributes.isEmpty() && intAttributes.isEmpty() && stringAttributes.isEmpty();
    }
}
