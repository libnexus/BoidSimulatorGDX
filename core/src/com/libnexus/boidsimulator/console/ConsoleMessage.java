package com.libnexus.boidsimulator.console;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class ConsoleMessage {
    public final String prefix;
    public final Color prefixColour;
    public final List<ConsoleString> message;

    public ConsoleMessage(String prefix, Color prefixColour, List<ConsoleString> message) {
        this.prefix = prefix;
        this.prefixColour = prefixColour;
        this.message = message;
    }
}
