package com.libnexus.boidsimulator.entity.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.util.Vector2f;

public class ExplosionEffect extends Effect {
    public final Vector2f location;
    public final Color colour;
    public final int frameInterval;
    public final int maxSize;
    public final int growthStep;

    private int life = 0;
    private int size = 0;

    public ExplosionEffect(Vector2f location, Color colour, int frameInterval, int maxSize, int growthStep) {
        this.location = location;
        this.colour = colour;
        this.frameInterval = frameInterval;
        this.maxSize = maxSize;
        this.growthStep = growthStep;
    }

    public static void forBoid(Boid boid) {
        World.effects().add(new ExplosionEffect(boid.currLocation, boid.currColour, 1, 50, 5));
    }

    @Override
    public void update() {
        life++;

        if (life % frameInterval != 0)
            return;

        if (life / frameInterval > maxSize)
            size -= growthStep;
        else
            size += growthStep;
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(colour);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(location.x, location.y, size);
    }

    @Override
    public boolean isAlive() {
        return size < maxSize * 2;
    }
}
