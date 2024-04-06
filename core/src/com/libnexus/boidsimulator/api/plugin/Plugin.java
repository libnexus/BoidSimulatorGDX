package com.libnexus.boidsimulator.api.plugin;

import com.badlogic.gdx.graphics.Color;
import com.libnexus.boidsimulator.BoidSimulator;
import com.libnexus.boidsimulator.console.Console;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.effect.Effect;
import com.libnexus.boidsimulator.entity.obstacle.Obstacle;
import com.libnexus.boidsimulator.util.ColorUtils;
import com.libnexus.boidsimulator.world.World;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Plugin {
    private final BoidSimulator simulator;
    private final List<Boid> boids = new LinkedList<>();
    private final List<Effect> effects = new LinkedList<>();
    private final List<Obstacle> obstacles = new LinkedList<>();
    private final List<BoidAgency> agencies = new LinkedList<>();
    private final Color COLOUR_PLUGIN_NAME = ColorUtils.fromRGB(0, 255, 255, 1);

    public Plugin(BoidSimulator simulator) {
        this.simulator = simulator;
    }


    public final Console console() {
        return simulator.console;
    }

    public final List<Boid> getBoids() {
        return boids;
    }
    public final Set<Boid> getAllBoids() { return World.GRID.boids(); }


    public final List<Boid> getBoidsOfClass(Class<? extends Boid> boidClass) {
        return boids.stream().filter(boid -> boid.getClass() == boidClass).collect(Collectors.toList());
    }


    public final List<Effect> getEffects() {
        return effects;
    }


    public final List<Obstacle> getObstacles() {
        return obstacles;
    }

    public final void addBoid(Boid boid) {
        World.GRID.place(boid);
        boids.add(boid);
    }


    public final void removeBoid(Boid boid) {
        World.GRID.remove(boid);
        boids.remove(boid);
    }

    public final void addEffect(Effect effect) {
        World.effects().add(effect);
        effects.add(effect);
    }

    public final void removeEffect(Effect effect) {
        World.effects().remove(effect);
        effects.remove(effect);
    }

    public final void addAgency(BoidAgency agency) {
        World.boidAgencies().add(agency);
        agencies.add(agency);
    }


    public final void removeAgency(BoidAgency agency) {
        World.boidAgencies().remove(agency);
        agencies.remove(agency);
    }


    public final void pruneMyBoids() {
        for (Boid boid : new HashSet<>(boids))
            removeBoid(boid);
    }


    public final void pruneMyEffects() {
        for (Effect effect : new HashSet<>(effects))
            removeEffect(effect);
    }


    public final void pruneMyAgencies() {
        for (BoidAgency agency : new HashSet<>(agencies))
            removeAgency(agency);
    }

    public final void allAgenciesKillAll() {
        for (BoidAgency agency : agencies) {
            agency.killAll();
        }
    }


    public String name() {
        return this.getClass().getSimpleName();
    }

    public abstract void init();

    public abstract void dispose();
}
