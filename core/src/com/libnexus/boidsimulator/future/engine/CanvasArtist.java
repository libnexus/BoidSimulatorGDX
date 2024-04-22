package com.libnexus.boidsimulator.future.engine;

import com.libnexus.boidsimulator.future.util.Colour;
import com.libnexus.boidsimulator.future.util.Vec2f;

public interface CanvasArtist {
    void drawCircle(Colour colour, float x, float y, float radius, float margin);
    void drawLine(Colour colour, Vec2f a, Vec2f b, float thickness);
}
