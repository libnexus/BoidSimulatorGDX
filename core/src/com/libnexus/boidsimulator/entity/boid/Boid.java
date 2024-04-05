package com.libnexus.boidsimulator.entity.boid;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.libnexus.boidsimulator.entity.obstacle.Obstacle;
import com.libnexus.boidsimulator.util.ColorUtils;
import com.libnexus.boidsimulator.util.Vector2f;
import com.libnexus.boidsimulator.world.World;
import com.libnexus.boidsimulator.world.WorldCell;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.badlogic.gdx.math.MathUtils.random;

public class Boid {
    public final BoidAgency agency;
    public final Vector2f initLocation;
    public final int initVisualRange;
    public final int initSpeedLimitUpper;
    public final int initSpeedLimitLower;
    public final int initSpeed;
    public final int initShynessThreshold;
    public final float initShynessFactor;
    public final float initCentringFactor;
    public final float initMatchingFactor;
    public final Color initColour;

    public final Vector2f currVelocity;
    private final LinkedList<Vector2f> history = new LinkedList<>();
    public Vector2f currLocation;
    public int currVisualRange;
    public int currSpeedLimitUpper;
    public int currSpeedLimitLower;
    public int currSpeed;
    public int currShynessThreshold;
    public float currShynessFactor;
    public float currCentringFactor;
    public float currMatchingFactor;
    public Color currColour;
    public WorldCell worldCell;

    public Boid(BoidAgency agency, Vector2f location, Integer visualRange, Integer speedLimitUpper, Integer speedLimitLower, Integer speed, Integer shynessThreshold, Float shynessFactor, Float centringFactor, Float matchingFactor, Color colour) {
        if (agency == null) agency = DefaultBoidAgency.INSTANCE;
        if (location == null) location = new Vector2f(0, 0);
        if (visualRange == null) visualRange = 150;
        if (speedLimitUpper == null) speedLimitUpper = 7;
        if (speedLimitLower == null) speedLimitLower = 4;
        if (speed == null) speed = 4;
        if (shynessThreshold == null) shynessThreshold = 15;
        if (shynessFactor == null) shynessFactor = 0.05f;
        if (centringFactor == null) centringFactor = 0.002f;
        if (matchingFactor == null) matchingFactor = 0.03f;
        if (colour == null) colour = ColorUtils.fromRGB(0, 255, 0, 1);

        this.agency = agency;
        initLocation = currLocation = location;
        initVisualRange = currVisualRange = visualRange;
        initSpeedLimitUpper = currSpeedLimitUpper = speedLimitUpper;
        initSpeedLimitLower = currSpeedLimitLower = speedLimitLower;
        initSpeed = currSpeed = speed;
        initShynessThreshold = currShynessThreshold = shynessThreshold;
        initShynessFactor = currShynessFactor = shynessFactor;
        initCentringFactor = currCentringFactor = centringFactor;
        initMatchingFactor = currMatchingFactor = matchingFactor;
        initColour = currColour = colour;
        currVelocity = new Vector2f(random.nextInt(8) - 8, random.nextInt(8) - 8);
    }

    public Boid(BoidAgency agency, Vector2f location, Color colour) {
        this(agency, location, null, null, null, null, null, null, null, null, colour);
    }

    public Boid(BoidAgency agency, Vector2f location) {
        this(agency, location, null, null, null, null, null, null, null, null, null);
    }

    public void flyTowardsCentre() {
        currVelocity.add(perceivedCentre().subtracted(currLocation).multipliedBy(currCentringFactor));

        matchVelocity();
    }

    public Vector2f perceivedCentre() {
        Vector2f centre = new Vector2f(0, 0);
        AtomicInteger neighbours = new AtomicInteger();
        worldCell.forEachBoidNeighbour(boid -> {
            if (currLocation.distance(boid.currLocation) < currVisualRange) {
                neighbours.getAndIncrement();
                centre.add(boid.currLocation);
            }
        });

        if (neighbours.get() > 0) centre.divideBy(neighbours.get());

        return centre;
    }

    public void matchVelocity() {
        Vector2f average = new Vector2f(0, 0);
        AtomicInteger neighbours = new AtomicInteger();

        worldCell.forEachBoidNeighbour(boid -> {
            if (currLocation.distance(boid.currLocation) < currVisualRange) {
                neighbours.getAndIncrement();
                average.add(boid.currVelocity);
            }
        });

        if (neighbours.get() > 0) average.divideBy(neighbours.get());

        currVelocity.add(average.multipliedBy(currMatchingFactor));

        limitSpeed();
    }

    public void limitSpeed() {
        float speed = currVelocity.abs();

        if (speed == 0) return;

        if (speed > currSpeedLimitUpper) currVelocity.divideBy(speed).multiplyBy(initSpeedLimitUpper);
        else if (speed < currSpeedLimitLower) currVelocity.divideBy(speed).multiplyBy(currSpeedLimitLower);


        avoidOthers();
    }

    public void avoidOthers() {
        Vector2f move = new Vector2f(0, 0);

        worldCell.forEachBoidNeighbour(boid -> {
            if (boid == this) return;

            if (currLocation.distance(boid.currLocation) < currShynessThreshold) {
                move.add(currLocation.subtracted(boid.currLocation));
            }
        });

        currVelocity.add(move.multipliedBy(currShynessFactor));

        keepWithinBounds();
    }

    public void keepWithinBounds() {
        if (currLocation.x < World.MARGIN) currVelocity.add(1f, 0);
        if (currLocation.x > World.WIDTH - World.MARGIN) currVelocity.add(-1f, 0);
        if (currLocation.y < World.MARGIN) currVelocity.add(0, 1f);
        if (currLocation.y > World.HEIGHT - World.MARGIN) currVelocity.add(0, -1f);

        for (Obstacle obstacle : World.obstacles()) {

            Vector2f[] vertices = obstacle.vertices();

            for (int i = 0; i < vertices.length - 1; i++) {
                Vector2f a = vertices[i];
                Vector2f b = vertices[i + 1];
                if (currLocation.between(a, b, 1f)) {
                    currVelocity.set(currVelocity.opposite()).multiplyBy(1 + (float) random.nextInt(2) / 7);
                    currLocation.add(currVelocity);
                }
            }
        }
    }

    public void update() {
        flyTowardsCentre();
        history.add(currLocation.copy());
        if (history.size() > 5)
            history.removeFirst();
        currLocation.add(currVelocity);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(currColour);
        Vector2f tail = history.peekFirst();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(currLocation.x, currLocation.y, 5);
        if (tail != null) shapeRenderer.line(new Vector2(tail.x, tail.y), new Vector2(currLocation.x, currLocation.y));
    }

    public void drawFov(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(currColour);
        Vector2f centre = perceivedCentre();

        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        worldCell.forEachBoidNeighbour(boid -> {
            if (boid == this || !(currLocation.distance(boid.currLocation) < currVisualRange)) return;

            shapeRenderer.line(new Vector2(currLocation.x, currLocation.y), new Vector2(boid.currLocation.x, boid.currLocation.y));
        });
        shapeRenderer.line(new Vector2(centre.x, centre.y), new Vector2(currLocation.x, currLocation.y));

        Gdx.gl.glLineWidth(1);

        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(currLocation.x, currLocation.y, currVisualRange);
        shapeRenderer.circle(centre.x, centre.y, 10);
    }

    public String getStat(String name) {
        return switch (name) {
            case "vel", "velocity" -> String.format("(%f, %f)", currVelocity.x, currVelocity.y);
            case "loc", "location" -> String.format("(%f, %f)", currLocation.x, currLocation.y);
            case "centre" -> String.format("(%f, %f)", perceivedCentre().x, perceivedCentre().y);
            default -> null;
        };
    }
}
