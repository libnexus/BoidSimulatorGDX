package com.libnexus.future.boidsimulator.engine;

import com.libnexus.future.boidsimulator.util.Colour;
import com.libnexus.future.boidsimulator.util.Vec2f;

public interface CanvasArtist {
    void drawCircle(Colour colour, float x, float y, float radius, float margin);
    void drawLine(Colour colour, Vec2f a, Vec2f b, float thickness);
}
