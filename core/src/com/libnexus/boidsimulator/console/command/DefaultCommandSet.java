package com.libnexus.boidsimulator.console.command;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.libnexus.boidsimulator.console.Console;
import com.libnexus.boidsimulator.console.ConsoleMessage;
import com.libnexus.boidsimulator.console.ConsoleString;
import com.libnexus.boidsimulator.console.command.annotations.Argument;
import com.libnexus.boidsimulator.console.command.annotations.Command;
import com.libnexus.boidsimulator.console.command.parse.CommandArgumentParser;
import com.libnexus.boidsimulator.console.command.parse.ConsoleCommand;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.boid.DefaultBoidAgency;
import com.libnexus.boidsimulator.util.ColorUtils;
import com.libnexus.boidsimulator.util.Vector2f;
import com.libnexus.boidsimulator.world.World;

import java.util.*;

import static com.badlogic.gdx.math.MathUtils.random;

public class DefaultCommandSet {
    private final Console console;

    public DefaultCommandSet(Console console) {
        this.console = console;
    }

    public HashMap<String, List<ConsoleCommand>> allCommands() {
        HashMap<String, List<ConsoleCommand>> commands = new HashMap<>();
        for (ConsoleCommand command : console.commands) {
            String name = command.commandAttribute.name();
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
                console.log("- ", Color.CYAN, command.commandAttribute.name(), "    ", Color.WHITE, command.commandAttribute.description());
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
            console.log("- ", Color.WHITE, command.commandAttribute.description());
            LinkedList<ConsoleString> arguments = new LinkedList<>();
            arguments.add(new ConsoleString("    ~ "));
            arguments.add(new ConsoleString(command.commandAttribute.name(), Color.CYAN));
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

    @Command(name = "killall", description = "kills all boids")
    public void killAll() {
        int count = World.GRID.boids().size();
        for (BoidAgency agency : World.boidAgencies())
            agency.killAll();
        console.log("killed ", Color.GREEN, String.valueOf(count - World.GRID.boids().size()), Color.WHITE, " boids");
    }

    @Command(name = "killall", description = "kills all boids of a given agency qualifier")
    public void killAll(@Argument(name = "qualifier", description = "the qualifier to know which agencies can kill all their boids") String qualifier) {
        BoidAgency boidAgency = console.simulator.boidAgencyQualifiers.get(qualifier);
        if (boidAgency == null) {
            console.error(Color.RED, qualifier, Color.WHITE, " is not a boid qualifier");
        } else {
            int count = World.GRID.boids().size();
            boidAgency.killAll();
            console.log("killed ", Color.GREEN, String.valueOf(count - World.GRID.boids().size()), Color.WHITE, " boids");
        }
    }

    @Command(name = "quit", description = "quits the simulator")
    public void quit() {
        console.simulator.quit();
    }

    @Command(name = "boids", description = "quits the simulator")
    public void boids() {
        HashMap<BoidAgency, Integer> agencyCounts = new HashMap<>();

        for (Boid boid : World.GRID.boids()) {
            agencyCounts.merge(boid.agency, 1, Integer::sum);
        }

        console.log(Color.CYAN, "Boid agency tallies");

        for (Map.Entry<BoidAgency, Integer> agencyCount : agencyCounts.entrySet()) {
            console.log("- ", Color.CYAN, agencyCount.getKey().name(), Color.WHITE, " : ", String.valueOf(agencyCount.getValue()));
        }

        console.log("Total ", Color.GREEN, String.valueOf(World.GRID.boids().size()), Color.WHITE, " boids");
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

    @Command(name = "speed", description = "shows the current speed that the simulator is running at")
    public void speed() {
        console.log("Current speed is ", Color.GREEN, String.valueOf(console.simulator.speed));
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

    @Command(name = "clear", description = "clears the console log")
    public void clear() {
        console.messages.clear();
    }

    @Command(name = "spawn", description = "displays all the attributes for the spawn commands")
    public void spawn() {
        console.log(Color.CYAN, "Spawn attributes");
        console.log("- ", Color.CYAN, "colour ", Color.WHITE, ": ",
                Color.CYAN, "x", Color.WHITE, ", ",
                Color.CYAN, "y", Color.WHITE, ", ",
                Color.CYAN, "z");
    }

    @Command(name = "spawn", description = "displays all the attributes for the spawn commands", varargs = true)
    public void spawn(List<String> args) {
        List<String> parsingArgs = new LinkedList<>(args);

        BoidAgency agency = DefaultBoidAgency.INSTANCE;
        Vector2f location = new Vector2f(random.nextInt(Gdx.graphics.getWidth()), random.nextInt(Gdx.graphics.getHeight() - 400) + 400);
        Integer visualRange = null;
        Integer speedLimitUpper = null;
        Integer speedLimitLower = null;
        Integer speed = null;
        Integer shynessThreshold = null;
        Float shynessFactor = null;
        Float centringFactor = null;
        Float matchingFactor = null;
        Color colour = null;
        int amount = 1;

        while (!parsingArgs.isEmpty()) {
            String attribute = parsingArgs.remove(0);

            switch (attribute) {
                case "location": {
                    List<Object> locationValues = parseArguments(parsingArgs, CommandArgumentParser.INTEGER_PARSER, CommandArgumentParser.INTEGER_PARSER);
                    if (locationValues == null)
                        return;

                    parsingArgs.remove(0);
                    parsingArgs.remove(0);
                    parsingArgs.remove(0);

                    location = new Vector2f((Float) locationValues.get(0), (Float) locationValues.get(1));

                    break;
                }

                case "colour": {
                    System.out.println(parsingArgs.size());
                    List<Object> colourValues = parseArguments(parsingArgs, CommandArgumentParser.INTEGER_PARSER, CommandArgumentParser.INTEGER_PARSER, CommandArgumentParser.INTEGER_PARSER);
                    if (colourValues == null)
                        return;

                    parsingArgs.remove(0);
                    parsingArgs.remove(0);
                    parsingArgs.remove(0);

                    colour = ColorUtils.fromRGB((int) colourValues.get(0), (int) colourValues.get(1), (int) colourValues.get(2));

                    break;
                }

                default: {
                    console.error("bad spawning argument ", Color.RED, attribute);
                    return;
                }
            }
        }

        for (int i = 0; i < amount; i++) {
            DefaultBoidAgency.INSTANCE.spawn(new Boid(agency, location, visualRange, speedLimitUpper, speedLimitLower, speed, shynessThreshold, shynessFactor, centringFactor, matchingFactor, colour));
        }
    }

    public List<Object> parseArguments(List<String> args, CommandArgumentParser<?>... parsers) {
        if (args.size() != parsers.length) {
            console.error("expected at least ", Color.RED, String.valueOf(parsers.length - args.size()), Color.WHITE, " more argument/s");
            return null;
        }

        LinkedList<Object> parsedArguments = new LinkedList<>();

        for (int i = 0; i < parsers.length; i++) {
            if (!parsers[i].accepts(args.get(i))) {
                console.error("argument ", Color.RED, args.get(i), Color.WHITE, " should be a/n ", Color.RED, parsers[i].name);
                return null;
            }
            parsedArguments.add(parsers[i].parse(args.get(i)));
        }

        return parsedArguments;
    }
}