package com.libnexus.boidsimulator.world;

import com.libnexus.boidsimulator.entity.boid.Boid;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class WorldGrid {
    private final int width, height;
    private final Set<Boid> boids = new HashSet<>();
    public int size = 0;
    public WorldCell[][] cells;

    public WorldGrid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void initCells() {
        int cellsX = Math.floorDiv(width, size) + 2;
        int cellsY = Math.floorDiv(height, size) + 2;

        cells = new WorldCell[cellsY][cellsX];

        for (int i = 0; i < cellsY; i++) {
            for (int j = 0; j < cellsX; j++) {
                cells[i][j] = new WorldCell(this, (j * size) - size, (i * size) - size);
            }
        }

        for (int i = 0; i < cellsY; i++) {
            for (int j = 0; j < cellsX; j++) {
                WorldCell cell = cells[i][j];
                if (i > 0)
                    cell.north = cells[i - 1][j];
                if (i > 0 && j < cellsX - 1)
                    cell.northEast = cells[i - 1][j + 1];
                if (j < cellsX - 1)
                    cell.east = cells[i][j + 1];
                if (i < cellsY - 1 && j < cellsX - 1)
                    cell.southEast = cells[i + 1][j + 1];
                if (i < cellsY - 1)
                    cell.south = cells[i + 1][j];
                if (i < cellsY - 1 && j > 0)
                    cell.southWest = cells[i + 1][j - 1];
                if (j > 0)
                    cell.west = cells[i][j - 1];
                if (i > 0 && j > 0)
                    cell.northWest = cells[i - 1][j - 1];
            }
        }
    }

    /**
     * Sets size to maximum value of currVisualRange of all boids. Used to calculate cell size
     */
    public void recalibrate() {
        for (Boid boid : boids) {
            if (boid.currVisualRange > size)
                size = boid.currVisualRange;
        }
    }


    public int cellSize() {
        return size;
    }

    /**
     * Assumes the boid is within the width and height specified
     *
     * @param boid the boid to place in the world
     *
     * @return if the boid moved or not
     */
    public void place(Boid boid) {
        WorldCell boidLocation = normalizedBoidLocation(boid);
        if (boid.worldCell != null)
            boid.worldCell.boids.remove(boid);
        boid.worldCell = boidLocation;
        boidLocation.boids.add(boid);
        boids.add(boid);
    }

    public boolean migrated(Boid boid) {
        WorldCell boidLocation = normalizedBoidLocation(boid);
        return boid.worldCell != boidLocation;
    }

    public WorldCell normalizedBoidLocation(Boid boid) {
        int i = Math.floorDiv((int) boid.currLocation.y + size, size);
        int j = Math.floorDiv((int) boid.currLocation.x + size, size);
        if (i < 0)
            i = 0;
        if (i > cells.length - 1)
            i = cells.length - 1;
        if (j < 0)
            j = 0;
        if (j > cells[i].length - 1)
            j = cells[i].length - 1;
        return cells[i][j];
    }

    public void remove(Boid boid) {
        boid.worldCell.boids.remove(boid);
        boid.worldCell.pickup.remove(boid);
        boid.worldCell = null;
        boids.remove(boid);
    }

    public Set<Boid> boids() {
        return Collections.unmodifiableSet(boids);
    }

    public void update() {
        for (WorldCell[] y : cells) {
            for (WorldCell x : y) {
                x.update();
            }
        }
    }
}
