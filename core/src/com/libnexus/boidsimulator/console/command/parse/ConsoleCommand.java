package com.libnexus.boidsimulator.console.command.parse;

import com.libnexus.boidsimulator.console.command.annotations.Argument;
import com.libnexus.boidsimulator.console.command.annotations.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ConsoleCommand {
    public final Command command;
    public final Object source;
    public final Method method;
    public final List<Argument> arguments;

    public ConsoleCommand(Command command, Object source, Method method, List<Argument> arguments) {
        this.command = command;
        this.source = source;
        this.method = method;
        this.arguments = arguments;
    }

    public boolean invokeIfAccepts(List<String> command) {
        if (accepts(command)) {
            invoke(command);
            return true;
        }
        return false;
    }

    public boolean accepts(List<String> command) {
        if (command.size() != arguments.size())
            return false;

        for (int i = 0; i < arguments.size(); i++) {
            if (!CommandArgumentParser.commandArgumentParsers.get(arguments.get(i).parser()).accepts(command.get(i)))
                return false;
        }

        return true;
    }

    public void invoke(List<String> command) {
        LinkedList<Object> parameters = new LinkedList<>();

        for (int i = 0; i < arguments.size(); i++) {
            Argument argument = arguments.get(i);
            Object object = CommandArgumentParser.commandArgumentParsers.get(argument.parser()).parse(command.get(i));
            parameters.add(object);
        }

        try {
            method.invoke(source, parameters.toArray());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
