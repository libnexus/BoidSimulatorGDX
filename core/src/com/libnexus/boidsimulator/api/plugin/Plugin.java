package com.libnexus.boidsimulator.api.plugin;

import com.libnexus.boidsimulator.BoidSimulator;
import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.effect.Effect;
import com.libnexus.boidsimulator.entity.obstacle.Obstacle;
import com.strongjoshua.console.LogLevel;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Plugin {
    private final BoidSimulator simulator;
    private final List<Boid> boids = new LinkedList<>();
    private final List<Effect> effects = new LinkedList<>();
    private final List<Obstacle> obstacles = new LinkedList<>();
    private final List<BoidAgency> agencies = new LinkedList<>();

    public Plugin(BoidSimulator simulator) {
        this.simulator = simulator;
    }


    public final void consoleLog(String message, LogLevel logLevel) {
        simulator.console.log(String.format("[%s] %s", name(), message), logLevel);
    }


    public final void consoleLog(String message) {
        simulator.console.log(String.format("[%s] %s", name(), message), LogLevel.DEFAULT);
    }


    public final List<Boid> getBoids() {
        return boids;
    }


    public final List<Boid> getBoidsOfClass(Class<? extends Boid> boidClass) {
        return boids.stream().filter(boid -> boid.getClass() == boidClass).collect(Collectors.toList());
    }


    public final List<Effect> getEffects() {
        return effects;
    }


    public final List<Obstacle> getObstacles() {
        return obstacles;
    }


    public final List<Boid> getWorldBoids() {
        return World.boids();
    }


    public final List<Boid> getWorldBoidsOfClass(Class<? extends Boid> boidClass) {
        return World.boids().stream().filter(boid -> boid.getClass() == boidClass).collect(Collectors.toList());
    }


    public final List<Effect> getWorldEffects() {
        return World.effects();
    }


    public final List<Obstacle> getWorldObstacles() {
        return World.obstacles();
    }


    public final void addBoid(Boid boid) {
        World.boids().add(boid);
        boids.add(boid);
    }


    public final void addEffect(Effect effect) {
        World.effects().add(effect);
        effects.add(effect);
    }


    public final void addObstacle(Obstacle obstacle) {
        World.obstacles().add(obstacle);
        obstacles.add(obstacle);
    }


    public final void removeBoid(Boid boid) {
        World.boids().remove(boid);
        boids.remove(boid);
    }


    public final void removeEffect(Effect effect) {
        World.effects().remove(effect);
        effects.remove(effect);
    }


    public final void removeObstacle(Obstacle obstacle) {
        World.obstacles().remove(obstacle);
        obstacles.remove(obstacle);
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


    public final void pruneMyObstacles() {
        for (Obstacle obstacle : new HashSet<>(obstacles))
            removeObstacle(obstacle);
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
