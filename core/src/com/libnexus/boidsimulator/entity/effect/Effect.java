package com.libnexus.boidsimulator.entity.effect;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Effect {
    public abstract void update();

    public void draw(ShapeRenderer shapeRenderer) {
    }

    public void draw(SpriteBatch spriteBatch) {
    }

    public abstract boolean isAlive();

    public boolean shouldUpdateWhilePaused() {
        return false;
    }
}
