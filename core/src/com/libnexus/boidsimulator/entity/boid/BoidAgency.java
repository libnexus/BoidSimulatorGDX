package com.libnexus.boidsimulator.entity.boid;

import com.libnexus.boidsimulator.util.Vector2f;
import com.libnexus.boidsimulator.world.World;

import java.util.regex.Pattern;

public abstract class BoidAgency {
    private final Pattern DEFAULT_STRING_ATTRIBUTE_MATCHER = Pattern.compile("core:(curr_vel_abs|curr_loc_abs):(less_than|more_than|equals)$");

    /**
     * Should create a new boid and return it from the method
     *
     * @param location the location parameter for the boid
     * @return the created boid
     */
    public abstract Boid make(Vector2f location);

    /**
     * Should spawn the boid in to the world i.e. add it to the list of current boids
     *
     * @param boid the boid to add to the world
     */
    public void spawn(Boid boid) {
        World.GRID.place(boid);
    }

    /**
     * Must return a non-empty array starting with the namespace qualifier of the agency e.g. <code>core:default</code>
     *
     * @return an array of qualifiers for the boid agency
     */
    public abstract String[] qualifiers();

    /**
     * @return an array of Gdx key bindings that the agency will respond to
     */
    public abstract int[] keyBindings();

    /**
     * @return the name of the boid agency
     */
    public abstract String name();

    /**
     * @param boid the boid to check if the agency will take responsibility for
     * @return if the agency will take responsibility for the boid
     */
    public abstract boolean takeResponsibility(Boid boid);

    /**
     * Should kill all boids under the agencies purview
     */
    public abstract void killAll();

    /**
     * Should safely kill a boid
     */
    public abstract void kill(Boid boid);
}

