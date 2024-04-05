package com.libnexus.boidsimulator.console.command.parse;

import com.libnexus.boidsimulator.console.command.annotations.Argument;
import com.libnexus.boidsimulator.console.command.annotations.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

public class ConsoleCommand {
    public final Command commandAttribute;
    public final Object source;
    public final Method method;
    public final List<Argument> arguments;

    public ConsoleCommand(Command commandAttribute, Object source, Method method, List<Argument> arguments) {
        this.commandAttribute = commandAttribute;
        this.source = source;
        this.method = method;
        this.arguments = arguments;
    }

    public static LinkedList<ConsoleCommand> from(Object commandSet) {
        LinkedList<ConsoleCommand> consoleCommands = new LinkedList<>();

        for (Method method : commandSet.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Command.class))
                continue;

            Command command = method.getAnnotation(Command.class);
            LinkedList<Argument> arguments = new LinkedList<>();

            Parameter[] parameters = method.getParameters();
            int last;

            if (command.varargs()) {
                if (parameters.length < 1)
                    throw new RuntimeException("Command with varargs and less than 1 parameter");
                last = parameters.length - 1;
            } else {
                last = parameters.length;
            }

            for (int i = 0; i < last; i++) {
                Parameter parameter = parameters[i];
                if (!parameter.isAnnotationPresent(Argument.class))
                    throw new RuntimeException("Error whilst parsing commands, parameter not annotated");
                arguments.add(parameter.getAnnotation(Argument.class));
            }

            consoleCommands.add(new ConsoleCommand(command, commandSet, method, arguments));
        }

        return consoleCommands;
    }

    public boolean invokeIfAccepts(List<String> command) {
        if (accepts(command)) {
            invoke(command);
            return true;
        }
        return false;
    }

    public boolean accepts(List<String> rawCommand) {
        if ((commandAttribute.varargs() && rawCommand.size() < arguments.size()) || (!commandAttribute.varargs() && rawCommand.size() != arguments.size()))
            return false;

        boolean argumentsFine = true;

        for (int i = 0; i < arguments.size(); i++) {
            if (!CommandArgumentParser.commandArgumentParsers.get(arguments.get(i).parser()).accepts(rawCommand.get(i)))
                argumentsFine = false;
        }

        return argumentsFine;
    }

    public void invoke(List<String> command) {
        LinkedList<Object> parameters = new LinkedList<>();

        int i = 0;

        for (; i < arguments.size(); i++) {
            Argument argument = arguments.get(i);
            Object object = CommandArgumentParser.commandArgumentParsers.get(argument.parser()).parse(command.get(i));
            parameters.add(object);
        }

        if (commandAttribute.varargs()) {
            LinkedList<String> remainingArguments = new LinkedList<>();

            for (; i < command.size(); i++) {
                remainingArguments.add(command.get(i));
            }

            parameters.add(remainingArguments);
        }

        try {
            method.invoke(source, parameters.toArray());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
