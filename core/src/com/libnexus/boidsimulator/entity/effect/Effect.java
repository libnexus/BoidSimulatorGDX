package com.libnexus.boidsimulator.entity.effect;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public interface Effect {
    void update();

    void draw(ShapeRenderer shapeRenderer);

    boolean isAlive();
}
