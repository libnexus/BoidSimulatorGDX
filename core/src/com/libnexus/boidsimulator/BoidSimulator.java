package com.libnexus.boidsimulator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.libnexus.boidsimulator.api.plugin.PluginManager;
import com.libnexus.boidsimulator.console.Console;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.boid.DefaultBoidAgency;
import com.libnexus.boidsimulator.entity.effect.Effect;
import com.libnexus.boidsimulator.entity.effect.FadingTextEffect;
import com.libnexus.boidsimulator.entity.obstacle.LineObstacle;
import com.libnexus.boidsimulator.entity.obstacle.Obstacle;
import com.libnexus.boidsimulator.util.ColorUtils;
import com.libnexus.boidsimulator.util.Vector2f;
import com.libnexus.boidsimulator.world.World;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.Deflater;

public class BoidSimulator extends ApplicationAdapter {
    public final HashMap<BoidAgency, Integer> boidAgencyBindings = new HashMap<>();
    public final HashMap<String, BoidAgency> boidAgencyQualifiers = new HashMap<>();
    public Boid selected = null;
    public Console console;
    public PluginManager pluginManager;
    public int speed = 1;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;
    private boolean paused = false;
    private Vector2f obstacleSelector;

    @Override
    public void create() {
        final FileHandle pluginDirectory = Gdx.files.external("BoidSimulatorGDX/plugins");
        if (!pluginDirectory.isDirectory())
            pluginDirectory.mkdirs();

        camera = new OrthographicCamera();
        shapeRenderer = new ShapeRenderer();
        camera.setToOrtho(false);
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);
        bitmapFont = new BitmapFont();
        console = new Console(this);
        pluginManager = new PluginManager(this, pluginDirectory);

        World.WORLD_GRID.size = 70;
        World.WORLD_GRID.initCells();

        World.boidAgencies().add(DefaultBoidAgency.INSTANCE);
        initPlugins();
    }

    public void initPlugins() {
        pluginManager.loadPlugins();
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_RIGHT))
            screenshot();

        ScreenUtils.clear(0, 0, 0, 1);

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);
        Vector2f mousePosition = new Vector2f(mouse.x, mouse.y);

        defaultKeyEvents(mousePosition);
        for (int i = 0; i < speed; i++) {
            updateBoidsAndEffects(mousePosition);
        }
        drawApplicationDetails();
    }

    public void screenshot() {
        // https://libgdx.com/wiki/graphics/taking-a-screenshot
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        ByteBuffer pixels = pixmap.getPixels();

        int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
        for (int i = 3; i < size; i += 4) {
            pixels.put(i, (byte) 255);
        }

        PixmapIO.writePNG(Gdx.files.external(String.format("BoidSimulatorGDX/screenshots/%s.png", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime()))), pixmap, Deflater.DEFAULT_COMPRESSION, true);
        pixmap.dispose();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }

    public void initAgencies() {
        boidAgencyBindings.clear();
        boidAgencyQualifiers.clear();

        for (BoidAgency boidAgency : World.boidAgencies()) {
            for (String qualifier : boidAgency.qualifiers()) {
                boidAgencyQualifiers.put(qualifier, boidAgency);
            }

            if (boidAgency.keyBindings().length == 0)
                continue;

            boolean success = false;
            for (int key : boidAgency.keyBindings()) {
                if (!boidAgencyBindings.containsValue(key)) {
                    boidAgencyBindings.put(boidAgency, key);
                    success = true;
                    break;
                }
            }
            if (!success)
                boidAgencyBindings.put(boidAgency, null);
        }
    }

    public void defaultKeyEvents(Vector2f mousePosition) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            World.effects().add(new FadingTextEffect(paused ? "un-paused" : "paused", Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 50, 50, true));
            paused = !paused;
            console.visible = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) && paused) {
            console.visible = !console.visible;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
            quit();
        }

        if (!paused) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.DEL))
                selected = null;

            if (Gdx.input.isKeyJustPressed(Input.Keys.O))
                if (obstacleSelector == null)
                    obstacleSelector = mousePosition.copy();
                else {
                    World.obstacles().add(new LineObstacle(ColorUtils.fromRGB(255, 255, 255, 1), obstacleSelector, mousePosition));
                    obstacleSelector = null;
                }

            for (BoidAgency boidAgency : World.boidAgencies()) {
                Integer key = boidAgencyBindings.get(boidAgency);
                if (key == null)
                    continue;

                if ((Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(key)) && !paused || Gdx.input.isKeyJustPressed(key)) {
                    boidAgency.spawn(boidAgency.make(mousePosition));
                    break;
                }
            }
        }
    }

    public void updateBoidsAndEffects(Vector2f mousePosition) {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();

        for (Boid boid : World.WORLD_GRID.boids()) {
            if (!paused)
                boid.update();
            else if (Gdx.input.isTouched())
                if (mousePosition.distance(boid.currLocation) < 5)
                    selected = boid;
            boid.draw(shapeRenderer);
        }

        World.WORLD_GRID.update();

        /*
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (int i = 0; i < World.WORLD_GRID.cells.length; i++) {
            for (int j = 0; j < World.WORLD_GRID.cells[i].length; j++) {
                float y = (i - 1) * World.WORLD_GRID.size;
                float x = (j - 1) * World.WORLD_GRID.size;
                shapeRenderer.line(new Vector2(x, y), new Vector2(x, y + World.WORLD_GRID.size));
                shapeRenderer.line(new Vector2(x, y), new Vector2(x + World.WORLD_GRID.size, y));
            }
        }
        */

        for (Obstacle obstacle : new HashSet<>(World.obstacles())) {
            if (!paused)
                obstacle.update();
            obstacle.draw(shapeRenderer);
        }

        spriteBatch.begin();
        for (Effect effect : new HashSet<>(World.effects())) {
            if (!paused || effect.shouldUpdateWhilePaused())
                effect.update();
            effect.draw(shapeRenderer);
            effect.draw(spriteBatch);
        }
        spriteBatch.end();

        World.effects().removeIf(effect -> !effect.isAlive());

        if (obstacleSelector != null)
            new LineObstacle(ColorUtils.fromRGB(100, 100, 100, 1), obstacleSelector, mousePosition).draw(shapeRenderer);

        if (!World.boids().contains(selected))
            selected = null;

        if (selected != null)
            selected.drawFov(shapeRenderer);

        shapeRenderer.end();
    }

    public void drawApplicationDetails() {
        String details = String.format("Boids: %d, FPS: %s, x: %d, y: %d", World.WORLD_GRID.boids().size(), Gdx.graphics.getFramesPerSecond(), Gdx.input.getX(), Gdx.input.getY());
        bitmapFont.setColor(0, 0, 255, 1);

        shapeRenderer.begin();
        console.draw(shapeRenderer);
        shapeRenderer.end();

        spriteBatch.begin();

        /*
        for (int i = 0; i < World.WORLD_GRID.cells.length; i++) {
            for (int j = 0; j < World.WORLD_GRID.cells[i].length; j++) {
                float y = (i - 1) * World.WORLD_GRID.size;
                float x = (j - 1) * World.WORLD_GRID.size;
                bitmapFont.draw(spriteBatch, String.valueOf(World.WORLD_GRID.cells[i][j].boids.size()), x + 35, y + 35);

            }
        }
        */

        bitmapFont.draw(spriteBatch, details, 20, Gdx.graphics.getHeight() - 20);
        console.draw(spriteBatch);
        spriteBatch.end();
    }

    public void drawText(String text, Color colour, int x, int y) {
        bitmapFont.setColor(colour);
        spriteBatch.begin();
        bitmapFont.draw(spriteBatch, text, x, y);
        spriteBatch.end();
    }

    public boolean isPaused() {
        return paused;
    }

    public void quit() {
        pluginManager.unloadPlugins();
        Gdx.app.exit();
    }
}
