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

        for (Iterator<Boid> boidIterator = boids.iterator(); boidIterator.hasNext(); ) {
            Boid boid = boidIterator.next();

            if (boid.currLocation.x > x + worldGrid.cellSize()) {
                boidIterator.remove();

                if (boid.currLocation.y > y + worldGrid.cellSize() && southEast != null) {
                    southEast.boids.add(boid);
                    boid.worldCell = southEast;
                } else if (boid.currLocation.y < y && northEast != null) {
                    northEast.boids.add(boid);
                    boid.worldCell = northEast;
                } else if (east != null) {
                    east.boids.add(boid);
                    boid.worldCell = east;
                }
            } else if (boid.currLocation.x < x) {
                boidIterator.remove();

                if (boid.currLocation.y > y + worldGrid.cellSize() && southWest != null) {
                    southWest.boids.add(boid);
                    boid.worldCell = southWest;
                } else if (boid.currLocation.y < y && northWest != null) {
                    northWest.boids.add(boid);
                    boid.worldCell = northWest;
                } else if (west != null) {
                    west.boids.add(boid);
                    boid.worldCell = west;
                }
            } else if (boid.currLocation.y > y + worldGrid.cellSize() && south != null) {
                boidIterator.remove();
                south.boids.add(boid);
                boid.worldCell = south;
            } else if (boid.currLocation.y < y && north != null) {
                boidIterator.remove();
                north.boids.add(boid);
                boid.worldCell = north;
            }
        }
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
