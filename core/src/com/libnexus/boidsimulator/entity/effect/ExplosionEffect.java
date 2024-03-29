package com.libnexus.boidsimulator.entity.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.libnexus.boidsimulator.math.Vector2f;

public class ExplosionEffect implements Effect {
    public final Vector2f LOCATION;
    public final Color COLOUR;
    public final int FRAME_INTERVAL;
    public final int MAX_SIZE;
    public final int GROWTH_STEP;

    private int life = 0;
    private int size = 0;

    public ExplosionEffect(Vector2f location, Color colour, int frameInterval, int maxSize, int growthStep) {
        LOCATION = location;
        COLOUR = colour;
        FRAME_INTERVAL = frameInterval;
        MAX_SIZE = maxSize;
        GROWTH_STEP = growthStep;
    }

    @Override
    public void update() {
        life++;

        if (life % FRAME_INTERVAL != 0)
            return;

        if (life / FRAME_INTERVAL > MAX_SIZE)
            size -= GROWTH_STEP;
        else
            size += GROWTH_STEP;
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(COLOUR);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(LOCATION.x, LOCATION.y, size);
        shapeRenderer.end();
    }

    @Override
    public boolean isAlive() {
        return size == MAX_SIZE * 2;
    }
}
