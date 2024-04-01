package com.libnexus.boidsimulator.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.libnexus.boidsimulator.entity.effect.Effect;

public class Console extends Effect {
    public static final String consoleChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-=+/*";
    private final BitmapFont bitmapFont = new BitmapFont();
    private final StringBuilder input = new StringBuilder();
    public boolean visible = false;
    private int cursor = 0;

    public Console() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                System.out.println(Input.Keys.toString(keycode));
                if (visible) {
                    if (keycode == (Input.Keys.LEFT) && cursor > 0)
                        cursor--;
                    else if (keycode == (Input.Keys.RIGHT) && cursor < input.length() - 1)
                        cursor++;
                    else if (keycode == (Input.Keys.DOWN))
                        cursor = 0;
                    else if (keycode == (Input.Keys.UP))
                        cursor = input.length() - 1;
                    else if (keycode == Input.Keys.BACKSPACE && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                        input.delete(0, cursor);
                        cursor = 0;
                    } else if (keycode == Input.Keys.BACKSPACE && cursor > 0) {
                        input.deleteCharAt(cursor - 1);
                        if (cursor > 0)
                            cursor--;
                    } else {
                        String inputKey = Input.Keys.toString(keycode);
                        if (consoleChars.contains(inputKey)) {
                            input.insert(cursor, inputKey);
                            cursor++;
                        } else if (keycode == Input.Keys.SPACE) {
                            input.insert(cursor, " ");
                            cursor++;
                        }
                    }
                }
                return super.keyDown(keycode);
            }
        });
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        if (visible) {
            shapeRenderer.setColor(new Color(10, 10, 10, 0.3f));
            shapeRenderer.rect(125, 125, Gdx.graphics.getWidth() - 250, 25);
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (visible) {
            bitmapFont.setColor(new Color(255, 255, 255, 1));
            bitmapFont.draw(spriteBatch, drawableInput(), 150, 144);
        }
    }

    public String drawableInput() {
        StringBuilder drawableInput = new StringBuilder(input);
        while (notFit(drawableInput.toString())) {
            if (cursor < drawableInput.length() / 2) {
                while (notFit(drawableInput.toString())) {
                    drawableInput.deleteCharAt(drawableInput.length() - 1);
                }
            } else {
                while (notFit(drawableInput.toString())) {
                    drawableInput.deleteCharAt(0);
                }
            }
        }
        return drawableInput.toString();
    }

    private boolean notFit(String text) {
        final GlyphLayout layout = new GlyphLayout(bitmapFont, text);
        return !(150 + layout.width < Gdx.graphics.getWidth() - 150);
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isAlive() {
        return true;
    }
}
