package com.libnexus.boidsimulator.console;

import com.badlogic.gdx.graphics.Color;
import com.libnexus.boidsimulator.BoidSimulator;
import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.api.plugin.Plugin;
import com.libnexus.boidsimulator.entity.effect.Effect;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.effect.ExplosionEffect;
import com.libnexus.boidsimulator.math.Vector2f;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.LogLevel;
import com.strongjoshua.console.annotation.ConsoleDoc;
import com.strongjoshua.console.annotation.HiddenCommand;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.libnexus.boidsimulator.World.RANDOM;


public class SimulatorCommandExecutor extends CommandExecutor {
    private final BoidSimulator boidSimulator;

    public SimulatorCommandExecutor(BoidSimulator simulator) {
        super();
        boidSimulator = simulator;
    }

    @ConsoleDoc(
            description = "Kills all boids of a given type",
            paramDescriptions = {"The qualifier of the type of boid to spawn"})
    public void kill(String qualifier) {
        int beginning = World.boids().size();

        if (qualifier.equals("*")) {
            for (Boid boid : World.boids())
                World.effects().add(new ExplosionEffect(boid.currLocation, boid.currColour, new Random().nextInt(80) + 80, 50, 1));
            World.boids().clear();
            qualifier = "boid";
        } else {
            BoidAgency boidAgency = boidSimulator.boidAgencyQualifiers.get(qualifier);
            if (boidAgency == null)
                console.log(String.format("No agency accepts the qualifier '%s'", qualifier), LogLevel.ERROR);
            else
                boidAgency.killAll();
        }

        int end = World.boids().size();
        this.console.log(String.format("Killed %d %ss", beginning - end, qualifier), LogLevel.SUCCESS);
    }

    @ConsoleDoc(
            description = "Spawn an amount of boids at (150, 150)",
            paramDescriptions = {"The qualifier of the type of boid to spawn", "The amount of boids to spawn"})
    public void spawn(String qualifier, int amount) {
        BoidAgency boidAgency = boidSimulator.boidAgencyQualifiers.get(qualifier);
        if (boidAgency == null)
            console.log(String.format("No agency accepts the qualifier '%s'", qualifier), LogLevel.ERROR);
        else
            for (int i = 0; i < amount; i++)
                boidAgency.spawn(boidAgency.make(new Vector2f(150, 150)));
    }

    @ConsoleDoc(description = "Get a list of the current support agency qualifiers")
    public void agencies() {
        for (BoidAgency boidAgency : World.boidAgencies()) {
            console.log(boidAgency.name() + ": " + String.join(", ", boidAgency.qualifiers()), LogLevel.SUCCESS);
        }
    }

    @ConsoleDoc(description = "Deletes the selected boid")
    public void delete() {
        if (boidSimulator.selected != null) {
            World.boids().remove(boidSimulator.selected);
            boidSimulator.selected = null;
            console.log("Successfully removed", LogLevel.SUCCESS);
        } else {
            console.log("Could not remove, no selection", LogLevel.ERROR);
        }
    }

    @ConsoleDoc(
            description = "Kills all boids which match the given filter",
            paramDescriptions = {"The filter to apply e.g. core:curr_vel_abs:less_than=10"})
    public void killAll(String filter) {

        CommandArguments commandArguments = new CommandArguments(filter);

        int beginning = World.boids().size();

        for (BoidAgency boidAgency : World.boidAgencies()) {
            commandArguments.intAttributes.forEach((name, value) -> {
                boidAgency.filterIntegerAttribute(name, value, World.boids());
            });
        }

        int end = World.boids().size();
        this.console.log(String.format("Killed %d boids", beginning - end), LogLevel.SUCCESS);

        if (commandArguments.isEmpty())
            console.log("Could not fully parse arguments", LogLevel.ERROR);
    }

    @ConsoleDoc(
            description = "Counts all boids which match the given filter",
            paramDescriptions = {"The filter to apply e.g. core:curr_vel_abs:less_than=10"})
    public void count(String filter) {
        CommandArguments commandArguments = new CommandArguments(filter);

        List<Boid> boids = new LinkedList<>(World.boids());

        for (BoidAgency boidAgency : World.boidAgencies()) {
            commandArguments.intAttributes.forEach((name, value) -> {
                boidAgency.filterIntegerAttribute(name, value, boids);
            });
        }

        if (!commandArguments.isEmpty())
            console.log(String.format("Counted %d boids", World.boids().size() - boids.size()), LogLevel.SUCCESS);
    }

    @ConsoleDoc(description = "Displays a list of plugins")
    public void plugins() {
        for (Plugin plugin : boidSimulator.pluginManager.plugins()) {
            console.log(plugin.name(), LogLevel.SUCCESS);
        }
    }

    @HiddenCommand
    public void reload() {
        console.log("reload has begun", LogLevel.ERROR);
        try {
            boidSimulator.pluginManager.reloadPlugins();
        } catch (Exception e) {
            console.log(e);
        }
        console.log("reload was successful", LogLevel.SUCCESS);
    }

    @HiddenCommand
    public void ech0(String arguments) {
        CommandArguments commandArguments = new CommandArguments(arguments);
        commandArguments.floatAttributes.forEach((name, value) -> console.log(String.format("float: %s, %f", name, value), LogLevel.SUCCESS));
        commandArguments.intAttributes.forEach((name, value) -> console.log(String.format("int: %s, %d", name, value), LogLevel.SUCCESS));
        commandArguments.stringAttributes.forEach((name, value) -> console.log(String.format("string: %s, %s", name, value), LogLevel.SUCCESS));
    }

    @HiddenCommand
    public void bomb(float magnitude, float radius) {
        Vector2f centre = World.boids().get(RANDOM.nextInt(World.boids().size())).perceivedCentre();
        for (Boid boid : World.boids())
            if (boid.currLocation.distance(centre) < radius)
                boid.currVelocity.add(centre.subtracted(boid.currLocation).multiplyBy(magnitude));
    }

    @ConsoleDoc(description = "Create a boid with specific values")
    public void create(String arguments) {
        CommandArguments commandArguments = new CommandArguments(arguments);
        BoidAgency boidAgency;
        String boidAgencyName = commandArguments.stringAttributes.getOrDefault("agency", "core:default");
        if ((boidAgency = boidSimulator.boidAgencyQualifiers.get(boidAgencyName)) == null) {
            console.log("No agency called " + boidAgencyName, LogLevel.ERROR);
            return;
        }

        Boid boid = boidAgency.make(new Vector2f(10, 10));

        Integer red = commandArguments.intAttributes.getOrDefault("r", 255);
        Integer green = commandArguments.intAttributes.getOrDefault("g", 255);
        Integer blue = commandArguments.intAttributes.getOrDefault("b", 255);

        boid.currColour = new Color(red, green, blue, 1);

        boid.currVisualRange = commandArguments.intAttributes.getOrDefault("vr", 150);
        boid.currSpeedLimitUpper = commandArguments.intAttributes.getOrDefault("mxs", 7);
        boid.currSpeedLimitLower = commandArguments.intAttributes.getOrDefault("mns", 4);

        boidAgency.spawn(boid);

        boidSimulator.selected = boid;
    }
}
