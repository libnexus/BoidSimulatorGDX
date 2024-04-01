package com.libnexus.boidsimulator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.libnexus.boidsimulator.api.plugin.Plugin;
import com.libnexus.boidsimulator.api.plugin.PluginManager;
import com.libnexus.boidsimulator.console.SimulatorCommandExecutor;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.boid.DefaultBoidAgency;
import com.libnexus.boidsimulator.entity.effect.Effect;
import com.libnexus.boidsimulator.entity.effect.FadingTextEffect;
import com.libnexus.boidsimulator.entity.obstacle.LineObstacle;
import com.libnexus.boidsimulator.entity.obstacle.Obstacle;
import com.libnexus.boidsimulator.util.Colour;
import com.libnexus.boidsimulator.util.Vector2f;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;

import java.util.HashMap;
import java.util.HashSet;

public class BoidSimulator extends ApplicationAdapter {
    public final HashMap<BoidAgency, Integer> boidAgencyBindings = new HashMap<>();
    public final HashMap<String, BoidAgency> boidAgencyQualifiers = new HashMap<>();
    public Boid selected = null;
    public Console console;
    public com.libnexus.boidsimulator.console.Console nConsole;
    public PluginManager pluginManager;
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
        console = new GUIConsole();
        nConsole = new com.libnexus.boidsimulator.console.Console();
        pluginManager = new PluginManager(this, pluginDirectory);
        console.setCommandExecutor(new SimulatorCommandExecutor(this));
        console.setDisplayKeyID(Input.Keys.TAB);

        World.boidAgencies().add(new DefaultBoidAgency());
        initPlugins();
    }

    public void initPlugins() {
        pluginManager.loadPlugins();

        for (Plugin plugin : pluginManager.plugins()) {
            console.log(String.format("Successfully loaded plugin '%s' on startup", plugin.name()), LogLevel.SUCCESS);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);
        Vector2f mousePosition = new Vector2f(mouse.x, mouse.y);

        defaultKeyEvents(mousePosition);
        updateBoidsAndEffects(mousePosition);
        drawApplicationDetails();

        console.draw();
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
            if (!success) {
                console.log(String.format("Could not bind agency '%s' to a key", boidAgency.name()), LogLevel.ERROR);
                boidAgencyBindings.put(boidAgency, null);
            } else
                console.log(String.format("Bound agency '%s' to key %d", boidAgency.name(), boidAgencyBindings.get(boidAgency)), LogLevel.SUCCESS);
        }
    }

    public void defaultKeyEvents(Vector2f mousePosition) {
        if (!console.isVisible()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                String state;
                if (paused)
                    state = "un-paused";
                else
                    state = "paused";
                World.effects().add(new FadingTextEffect(state, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 50, 50, true));
                paused = !paused;
                nConsole.visible = !nConsole.visible;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
                pluginManager.unloadPlugins();
                Gdx.app.exit();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.S))
                selected = null;

            if (Gdx.input.isKeyJustPressed(Input.Keys.O))
                if (obstacleSelector == null)
                    obstacleSelector = mousePosition.copy();
                else {
                    World.obstacles().add(new LineObstacle(Colour.fromRGB(255, 255, 255, 1), obstacleSelector, mousePosition));
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

        for (Boid boid : new HashSet<>(World.boids())) {
            if (!paused)
                boid.update();
            else if (Gdx.input.isTouched() && mousePosition.distance(boid.currLocation) < 5)
                selected = boid;
            boid.draw(shapeRenderer);
        }

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
            new LineObstacle(Colour.fromRGB(100, 100, 100, 1), obstacleSelector, mousePosition).draw(shapeRenderer);

        if (!World.boids().contains(selected))
            selected = null;

        if (selected != null)
            selected.drawFov(shapeRenderer);

        shapeRenderer.end();
    }

    public void drawApplicationDetails() {
        String details = String.format("Boids: %d, FPS: %s", World.boids().size(), Gdx.graphics.getFramesPerSecond());
        bitmapFont.setColor(0, 0, 255, 1);
        shapeRenderer.begin();
        nConsole.draw(shapeRenderer);
        shapeRenderer.end();
        spriteBatch.begin();
        bitmapFont.draw(spriteBatch, details, 20, Gdx.graphics.getHeight() - 20);
        nConsole.draw(spriteBatch);
        spriteBatch.end();
    }

    public void drawText(String text, Color colour, int x, int y) {
        bitmapFont.setColor(colour);
        spriteBatch.begin();
        bitmapFont.draw(spriteBatch, text, x, y);
        spriteBatch.end();
    }

    public boolean isPaused() { return paused; }
}
