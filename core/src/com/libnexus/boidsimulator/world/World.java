package com.libnexus.boidsimulator.world;

import com.badlogic.gdx.Gdx;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.effect.Effect;
import com.libnexus.boidsimulator.entity.obstacle.Obstacle;

import java.util.LinkedList;
import java.util.List;

public class World {
    public static final int WIDTH = Gdx.graphics.getWidth();
    public static final int HEIGHT = Gdx.graphics.getHeight();
    public static final WorldGrid WORLD_GRID = new WorldGrid(WIDTH, HEIGHT);
    public static final int MARGIN = 100;
    private static final List<Boid> boids = new LinkedList<>();
    private static final List<Effect> effects = new LinkedList<>();
    private static final List<Obstacle> obstacles = new LinkedList<>();
    private static final List<BoidAgency> agencies = new LinkedList<>();

    public static List<Boid> boids() {
        return boids;
    }

    public static List<Effect> effects() {
        return effects;
    }

    public static List<Obstacle> obstacles() {
        return obstacles;
    }

    public static List<BoidAgency> boidAgencies() {
        return agencies;
    }

    public static List<Boid> getBoidsOfAgency(BoidAgency agency) {
        List<Boid> agencyBoids = new LinkedList<>();
        for (Boid boid : boids) {
            if (boid.agency.equals(agency))
                agencyBoids.add(boid);
        }
        return agencyBoids;
    }
}
