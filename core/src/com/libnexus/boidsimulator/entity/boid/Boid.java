package com.libnexus.boidsimulator.entity.boid;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.entity.obstacle.Obstacle;
import com.libnexus.boidsimulator.math.Vector2f;

import java.util.LinkedList;

import static com.libnexus.boidsimulator.World.RANDOM;

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

    public Boid(BoidAgency agency, Vector2f location, Integer visualRange, Integer speedLimitUpper, Integer speedLimitLower, Integer speed, Integer shynessThreshold, Float shynessFactor, Float centringFactor, Float matchingFactor, Color colour) {
        if (visualRange == null) visualRange = 150;
        if (speedLimitUpper == null) speedLimitUpper = 7;
        if (speedLimitLower == null) speedLimitLower = 4;
        if (speed == null) speed = 4;
        if (shynessThreshold == null) shynessThreshold = 15;
        if (shynessFactor == null) shynessFactor = 0.05f;
        if (centringFactor == null) centringFactor = 0.002f;
        if (matchingFactor == null) matchingFactor = 0.03f;
        if (colour == null) colour = new Color(0, 255, 0, 1);

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
        currVelocity = new Vector2f(RANDOM.nextInt(8) - 8, RANDOM.nextInt(8) - 8);
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
        int neighbours = 0;
        for (Boid boid : World.boids()) {
            if (neighbours > 30) break;

            if (currLocation.distance(boid.currLocation) < currVisualRange) {
                neighbours++;
                centre.add(boid.currLocation);
            }
        }

        if (neighbours > 0) centre.divideBy(neighbours);

        return centre;
    }

    public void matchVelocity() {
        Vector2f average = new Vector2f(0, 0);
        int neighbours = 0;
        for (Boid boid : World.boids()) {
            if (currLocation.distance(boid.currLocation) < currVisualRange) {
                neighbours++;
                average.add(boid.currVelocity);
            }
        }

        if (neighbours > 0) average.divideBy(neighbours);

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
        for (Boid boid : World.boids()) {
            if (boid == this) continue;

            if (currLocation.distance(boid.currLocation) < currShynessThreshold) {
                move.add(currLocation.subtracted(boid.currLocation));
            }
        }

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
                    currVelocity.set(currVelocity.opposite()).multiplyBy(1 + (float) RANDOM.nextInt(2) / 7);
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
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(currLocation.x, currLocation.y, 5);
        if (tail != null) shapeRenderer.line(new Vector2(tail.x, tail.y), new Vector2(currLocation.x, currLocation.y));
        shapeRenderer.end();
    }

    public void drawFov(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(255 - currColour.r, 255 - currColour.g, 255 - currColour.b, 1);
        Vector2f centre = perceivedCentre();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Boid boid : World.boids()) {
            if (boid == this || !(currLocation.distance(boid.currLocation) < currVisualRange)) continue;

            shapeRenderer.line(new Vector2(currLocation.x, currLocation.y), new Vector2(boid.currLocation.x, boid.currLocation.y));
        }
        shapeRenderer.line(new Vector2(centre.x, centre.y), new Vector2(currLocation.x, currLocation.y));
        shapeRenderer.end();

        Gdx.gl.glLineWidth(1);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(currLocation.x, currLocation.y, currVisualRange);
        shapeRenderer.circle(centre.x, centre.y, 10);
        shapeRenderer.end();
    }
}
