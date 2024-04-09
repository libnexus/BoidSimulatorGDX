package com.libnexus.boidsimulator.world;

import com.libnexus.boidsimulator.entity.boid.Boid;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class WorldCell {
    public final WorldGrid worldGrid;
    public final int x, y;
    public List<Boid> boids = new LinkedList<>();
    public List<Boid> pickup = new LinkedList<>();
    public WorldCell north, northEast, east, southEast, south, southWest, west, northWest;

    public WorldCell(WorldGrid worldGrid, int x, int y) {
        this.worldGrid = worldGrid;
        this.x = x;
        this.y = y;
        north = northEast = east = southEast = south = southWest = west = northWest = null;
    }

    /**
     * This relies on the fact that no boids should be able to move faster than their visual range.
     * Moves boids from cell to cell depending on where they are
     */
    public void update() {
        int i = 0;
        for (Iterator<Boid> boidIterator = boids.iterator(); boidIterator.hasNext(); ) {
            i++;
            Boid boid = boidIterator.next();

            if (worldGrid.migrated(boid)) {
                boidIterator.remove();
                worldGrid.place(boid);
            }
        }

        boids.addAll(pickup);
        pickup.clear();
    }

    public void forEachBoidNeighbour(Consumer<Boid> lambda) {
        boids.forEach(lambda);
        if (north != null)
            north.boids.forEach(lambda);
        if (northEast != null)
            northEast.boids.forEach(lambda);
        if (east != null)
            east.boids.forEach(lambda);
        if (southEast != null)
            southEast.boids.forEach(lambda);
        if (south != null)
            south.boids.forEach(lambda);
        if (southWest != null)
            southWest.boids.forEach(lambda);
        if (west != null)
            west.boids.forEach(lambda);
        if (northWest != null)
            northWest.boids.forEach(lambda);
    }

    @Override
    public String toString() {
        return String.format("[%d]", boids.size());
    }
}
