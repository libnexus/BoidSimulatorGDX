package com.libnexus.boidsimulator.api.plugin;

import com.badlogic.gdx.graphics.Color;
import com.libnexus.boidsimulator.BoidSimulator;
import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.effect.Effect;
import com.libnexus.boidsimulator.entity.obstacle.Obstacle;
import com.strongjoshua.console.Log;
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


    public void consoleLog(String message, LogLevel logLevel) {
        simulator.console.log(String.format("[%s] %s", name(), message), logLevel);
    }


    public void consoleLog(String message) {
        simulator.console.log(String.format("[%s] %s", name(), message), LogLevel.DEFAULT);
    }


    public List<Boid> getBoids() {
        return boids;
    }


    public List<Boid> getBoidsOfClass(Class<? extends Boid> boidClass) {
        return boids.stream().filter(boid -> boid.getClass() == boidClass).collect(Collectors.toList());
    }


    public List<Effect> getEffects() {
        return effects;
    }


    public List<Obstacle> getObstacles() {
        return obstacles;
    }


    public List<Boid> getWorldBoids() {
        return World.boids();
    }


    public List<Boid> getWorldBoidsOfClass(Class<? extends Boid> boidClass) {
        return World.boids().stream().filter(boid -> boid.getClass() == boidClass).collect(Collectors.toList());
    }


    public List<Effect> getWorldEffects() {
        return World.effects();
    }


    public List<Obstacle> getWorldObstacles() {
        return World.obstacles();
    }


    public void addBoid(Boid boid) {
        World.boids().add(boid);
        boids.add(boid);
    }


    public void addEffect(Effect effect) {
        World.effects().add(effect);
        effects.add(effect);
    }


    public void addObstacle(Obstacle obstacle) {
        World.obstacles().add(obstacle);
        obstacles.add(obstacle);
    }


    public void removeBoid(Boid boid) {
        World.boids().remove(boid);
        boids.remove(boid);
    }


    public void removeEffect(Effect effect) {
        World.effects().remove(effect);
        effects.remove(effect);
    }


    public void removeObstacle(Obstacle obstacle) {
        World.obstacles().remove(obstacle);
        obstacles.remove(obstacle);
    }


    public void addAgency(BoidAgency agency) {
        World.boidAgencies().add(agency);
        agencies.add(agency);
    }


    public void removeAgency(BoidAgency agency) {
        World.boidAgencies().remove(agency);
        agencies.remove(agency);
    }


    public void pruneMyBoids() {
        for (Boid boid : new HashSet<>(boids))
            removeBoid(boid);
    }


    public void pruneMyEffects() {
        for (Effect effect : new HashSet<>(effects))
            removeEffect(effect);
    }


    public void pruneMyObstacles() {
        for (Obstacle obstacle : new HashSet<>(obstacles))
            removeObstacle(obstacle);
    }


    public void pruneMyAgencies() {
        for (BoidAgency agency : new HashSet<>(agencies))
            removeAgency(agency);
    }

    public void allAgenciesKillAll() {
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
