package com.libnexus.boidsimulator.entity.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.libnexus.boidsimulator.BoidSimulator;
import com.libnexus.boidsimulator.util.Colour;
import com.libnexus.boidsimulator.util.Vector2f;

public class FadingTextEffect extends Effect {
    private final int fadeIn, fadeOut;
    private int currFadeIn = 0;
    private int currFadeOut = 0;
    private Color colour;
    private final String text;
    private final int x, y;
    protected static final BitmapFont bitmapFont = new BitmapFont();

    public FadingTextEffect(String text, int r, int g, int b, int x, int y, int fadeIn, int fadeOut, boolean centre) {
        this.text = text;
        colour = Colour.fromRGB(r, g, b, 0);
        if (centre) {
            final GlyphLayout layout = new GlyphLayout(bitmapFont, text);
            this.x = (int) (x - layout.width / 2);
            this.y = (int) (y - layout.height / 2);
        } else {
            this.x = x;
            this.y = y;
        }
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    public FadingTextEffect(String text, int x, int y, int fadeIn, int fadeOut, boolean centre) {
        this(text, 255, 255, 255, x, y, fadeIn, fadeOut, centre);
    }


    @Override
    public void update() {
        if (currFadeIn < fadeIn) {
            currFadeIn++;
            colour.a = (float) currFadeIn / fadeIn;
        } else {
            currFadeOut++;
            colour.a = 1 - (float) currFadeOut / fadeOut;
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        bitmapFont.setColor(colour);
        bitmapFont.draw(spriteBatch, text, x, y);
    }

    @Override
    public boolean isAlive() {
        return currFadeIn < fadeIn || currFadeOut < fadeOut;
    }

    @Override
    public boolean shouldUpdateWhilePaused() {
        return true;
    }
}
