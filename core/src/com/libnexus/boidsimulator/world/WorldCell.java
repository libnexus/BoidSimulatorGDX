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

            if (i > 20)
                boid.currVelocity.add(boid.currVelocity.opposite().multiplyBy(2));

            if (boid.currLocation.x > x + worldGrid.cellSize()) {
                boidIterator.remove();

                if (boid.currLocation.y > y + worldGrid.cellSize() && southEast != null) {
                    southEast.pickup.add(boid);
                    boid.worldCell = southEast;
                } else if (boid.currLocation.y < y && northEast != null) {
                    northEast.pickup.add(boid);
                    boid.worldCell = northEast;
                } else if (east != null) {
                    east.pickup.add(boid);
                    boid.worldCell = east;
                }
            } else if (boid.currLocation.x < x) {
                boidIterator.remove();

                if (boid.currLocation.y > y + worldGrid.cellSize() && southWest != null) {
                    southWest.pickup.add(boid);
                    boid.worldCell = southWest;
                } else if (boid.currLocation.y < y && northWest != null) {
                    northWest.pickup.add(boid);
                    boid.worldCell = northWest;
                } else if (west != null) {
                    west.pickup.add(boid);
                    boid.worldCell = west;
                }
            } else if (boid.currLocation.y > y + worldGrid.cellSize() && south != null) {
                boidIterator.remove();
                south.pickup.add(boid);
                boid.worldCell = south;
            } else if (boid.currLocation.y < y && north != null) {
                boidIterator.remove();
                north.pickup.add(boid);
                boid.worldCell = north;
            }
        }
        
        boids.addAll(pickup);
        pickup.clear();
    }

    public void forEachBoidNeighbour(Consumer<Boid> lambda) {
        Consumer<Boid> aliveBoid = boid -> {
            if (boid.worldCell != null) lambda.accept(boid);
        };
        boids.forEach(aliveBoid);
        if (north != null) north.boids.forEach(aliveBoid);
        if (northEast != null) northEast.boids.forEach(aliveBoid);
        if (east != null) east.boids.forEach(aliveBoid);
        if (southEast != null) southEast.boids.forEach(aliveBoid);
        if (south != null) south.boids.forEach(aliveBoid);
        if (southWest != null) southWest.boids.forEach(aliveBoid);
        if (west != null) west.boids.forEach(aliveBoid);
        if (northWest != null) northWest.boids.forEach(aliveBoid);
    }

    @Override
    public String toString() {
        return String.format("[%d]", boids.size());
    }
}
