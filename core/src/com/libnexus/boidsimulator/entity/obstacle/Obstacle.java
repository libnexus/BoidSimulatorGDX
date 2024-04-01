package com.libnexus.boidsimulator.entity.obstacle;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.libnexus.boidsimulator.util.Vector2f;

public interface Obstacle {
    Vector2f[] vertices();

    void update();

    void draw(ShapeRenderer shapeRenderer);
}
