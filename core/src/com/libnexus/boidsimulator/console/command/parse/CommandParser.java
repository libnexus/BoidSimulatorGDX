package com.libnexus.boidsimulator.console.command.parse;

import com.libnexus.boidsimulator.console.command.annotations.Argument;
import com.libnexus.boidsimulator.console.command.annotations.Command;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;

public class CommandParser {
    public static LinkedList<ConsoleCommand> consoleCommandsFrom(Object commandSet) {
        LinkedList<ConsoleCommand> consoleCommands = new LinkedList<>();

        for (Method method : commandSet.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Command.class))
                continue;

            Command command = method.getAnnotation(Command.class);
            LinkedList<Argument> arguments = new LinkedList<>();

            for (Parameter parameter : method.getParameters()) {
                if (!parameter.isAnnotationPresent(Argument.class))
                    throw new RuntimeException("Error whilst parsing commands, parameter not annotated");
                arguments.add(parameter.getAnnotation(Argument.class));
            }

            consoleCommands.add(new ConsoleCommand(command, commandSet, method, arguments));
        }

        return consoleCommands;
    }
}
