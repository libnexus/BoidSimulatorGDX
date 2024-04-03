package com.libnexus.boidsimulator.console.command.parse;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public abstract class CommandArgumentParser<T> {
    public static final HashMap<String, CommandArgumentParser<?>> commandArgumentParsers = new HashMap<>();

    static {
        new CommandArgumentParser<String>("str") {

            @Override
            public boolean accepts(String argument) {
                return true;
            }

            @Override
            public String parse(String argument) {
                return argument;
            }
        };

        new CommandArgumentParser<Integer>("int") {
            @Override
            public boolean accepts(String argument) {
                return StringUtils.isNumeric(argument);
            }

            @Override
            public Integer parse(String argument) {
                return Integer.parseInt(argument);
            }
        };
    }

    public CommandArgumentParser(String name) {
        commandArgumentParsers.put(name, this);
    }

    public abstract boolean accepts(String argument);

    public abstract T parse(String argument);
}
