package com.libnexus.boidsimulator.console.command;

import com.badlogic.gdx.graphics.Color;
import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.console.Console;
import com.libnexus.boidsimulator.console.ConsoleMessage;
import com.libnexus.boidsimulator.console.ConsoleString;
import com.libnexus.boidsimulator.console.command.annotations.Argument;
import com.libnexus.boidsimulator.console.command.annotations.Command;
import com.libnexus.boidsimulator.console.command.parse.ConsoleCommand;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultCommandSet {
    private final Console console;

    public DefaultCommandSet(Console console) {
        this.console = console;
    }

    public HashMap<String, List<ConsoleCommand>> allCommands() {
        HashMap<String, List<ConsoleCommand>> commands = new HashMap<>();
        for (ConsoleCommand command : console.commands) {
            String name = command.command.name();
            List<ConsoleCommand> subcommands;
            if ((subcommands = commands.get(name)) != null)
                subcommands.add(command);
            else {
                LinkedList<ConsoleCommand> subs = new LinkedList<>();
                subs.add(command);
                commands.put(name, subs);
            }
        }
        return commands;
    }

    @Command(name = "help", description = "shows all current console commands, * for a command with subcommands (help [command name])")
    public void help() {
        HashMap<String, List<ConsoleCommand>> commands = allCommands();

        console.log(Color.CYAN, "Commands");
        for (String commandName : commands.keySet()) {
            if (commands.get(commandName).size() > 1)
                console.log("* ", Color.CYAN, commandName);
            else {
                ConsoleCommand command = commands.get(commandName).get(0);
                console.log("- ", Color.CYAN, command.command.name(), "    ", Color.WHITE, command.command.description());
            }
        }
    }

    @Command(name = "help", description = "shows all subcommands and argument syntaxes for a given subcommand")
    public void help(@Argument(name = "command name", description = "the name of the command") String commandName) {
        HashMap<String, List<ConsoleCommand>> commands = allCommands();

        if (!commands.containsKey(commandName)) {
            console.error("there is no command named ", commandName);
            return;
        }

        console.log(Color.CYAN, "Sub-Commands for ", commandName);

        List<ConsoleCommand> subcommands = commands.get(commandName);

        for (ConsoleCommand command : subcommands) {
            console.log("- ", Color.WHITE, command.command.description());
            LinkedList<ConsoleString> arguments = new LinkedList<>();
            arguments.add(new ConsoleString("    ~ "));
            arguments.add(new ConsoleString(command.command.name(), Color.CYAN));
            for (Argument argument : command.arguments) {
                arguments.add(new ConsoleString(" ["));
                arguments.add(new ConsoleString(argument.name(), Color.CYAN));
                arguments.add(new ConsoleString("] "));
            }
            console.addMessage(new ConsoleMessage(null, null, arguments));
            for (Argument argument : command.arguments) {
                console.log("         | ", Color.CYAN, argument.name(), Color.WHITE, " : ", Color.CYAN, argument.parser(), "    ", Color.WHITE, argument.description());
            }
        }
    }

    @Command(name = "echo", description = "repeats what is given as the second argument")
    public void echo(@Argument(name = "what", description = "the thing to be repeated") String what) {
        console.log(what);
    }

    @Command(name = "ping", description = "adds 1 to the given amount then displays the answer")
    public void ping(@Argument(name = "amount", description = "the number to add 1 to", parser = "int") Integer amount) {
        console.log(String.valueOf(amount + 1));
    }

    @Command(name = "killall", description = "kills all boids of a given agency qualifier")
    public void killAll(@Argument(name = "qualifier", description = "the qualifier to know which agencies can kill all their boids") String qualifier) {
        BoidAgency boidAgency = console.simulator.boidAgencyQualifiers.get(qualifier);
        if (boidAgency == null) {
            console.error(Color.RED, qualifier, Color.WHITE, " is not a boid qualifier");
        } else {
            int count = World.boids().size();
            boidAgency.killAll();
            count -= World.boids().size();
            console.log("killed ", Color.GREEN, String.valueOf(count), Color.WHITE, " boids");
        }
    }

    @Command(name = "quit", description = "quits the simulator")
    public void quit() {
        console.simulator.quit();
    }

    @Command(name = "boids", description = "quits the simulator")
    public void boids() {
        HashMap<BoidAgency, Integer> agencyCounts = new HashMap<>();

        for (Boid boid : World.boids()) {
            agencyCounts.merge(boid.agency, 1, Integer::sum);
        }

        console.log(Color.CYAN, "Boid agency tallies");

        for (Map.Entry<BoidAgency, Integer> agencyCount : agencyCounts.entrySet()) {
            console.log("- ", Color.CYAN, agencyCount.getKey().name(), Color.WHITE, " : ", String.valueOf(agencyCount.getValue()));
        }

        console.log("Total ", Color.GREEN, String.valueOf(World.boids().size()), Color.WHITE, " boids");
    }

    @Command(name = "inspect", description = "inspects the selected boid for a given stat name in 3")
    public void inspect(@Argument(name = "stat name", description = "the name of the stat to query on the boid") String statName) {
        if (console.simulator.selected == null) {
            console.error("select a boid first to continue (click on a boid)");
            return;
        }

        String stat = console.simulator.selected.getStat(statName);
        if (stat == null)
            console.error("no stats named ", Color.RED, statName);
        else
            console.log(stat);
    }

    @Command(name = "speed", description = "sets how many times the world is updated each frame")
    public void speed(@Argument(name = "speed", description = "the new amount of times to update the world each frame", parser = "int") Integer speed) {
        if (speed < 0)
            console.error("cannot set the speed below 0");
        else {
            console.simulator.speed = speed;
            console.log("New speed is ", Color.GREEN, String.valueOf(speed));
        }
    }
}
