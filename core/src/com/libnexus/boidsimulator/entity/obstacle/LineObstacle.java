package com.libnexus.boidsimulator.entity.obstacle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.libnexus.boidsimulator.util.Vector2f;

public class LineObstacle implements Obstacle {
    private final Color colour;
    private final Vector2f a, b;

    public LineObstacle(Color colour, Vector2f a, Vector2f b) {
        this.colour = colour;
        this.a = a;
        this.b = b;
    }

    @Override
    public Vector2f[] vertices() {
        return new Vector2f[]{a, b};
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        Gdx.gl.glLineWidth(1);
        shapeRenderer.setColor(colour);
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(new Vector2(a.x, a.y), new Vector2(b.x, b.y));
    }
}
