package com.libnexus.boidsimulator.console;

import com.badlogic.gdx.graphics.Color;

import java.util.LinkedList;

public class ConsoleString {
    public final String string;
    public final Color colour;

    public ConsoleString(String string, Color colour) {
        this.string = string;
        this.colour = colour;
    }

    public ConsoleString(String string) {
        this(string, Color.WHITE);
    }

    public static ConsoleMessage ping(Object... string) {
        return new ConsoleMessage(null, null, consoleStringFromMethodArray(string));
    }

    public static ConsoleMessage message(String prefix, Color prefixColour, Object... string) {
        return new ConsoleMessage(prefix, prefixColour, consoleStringFromMethodArray(string));
    }

    public static LinkedList<ConsoleString> consoleStringFromMethodArray(Object... string) {
        LinkedList<ConsoleString> message = new LinkedList<>();
        Color colour = Color.WHITE;
        for (Object object : string) {
            if (object instanceof String)
                message.add(new ConsoleString((String) object, colour));
            else if (object instanceof Color)
                colour = (Color) object;
        }
        return message;
    }
}

