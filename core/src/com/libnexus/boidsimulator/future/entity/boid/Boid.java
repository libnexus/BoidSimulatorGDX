package com.libnexus.boidsimulator.future.entity.boid;

import com.libnexus.boidsimulator.future.engine.CanvasArtist;
import com.libnexus.boidsimulator.future.util.Vec2f;

/**
 * An abstract representation of what a boid must be able to do for it to be able to
 * have the engine take advantage of it fully
 */
public interface Boid {
    enum FilterOperator {
        EQUALS, LESS_THAN, MORE_THAN
    }

    /**
     * Should set the bounds of the boid, to avoid errors or unexpected behaviour in the engine,
     * the boid should not at all appear to be outside the given boundaries.
     *
     * @param lx the extreme lowest x coordinate (minimum) that may be allowed (can be negative)
     * @param ly the extreme lowest y coordinate (minimum) that may be allowed (can be negative)
     * @param hx the extreme highest x coordinate (maximum) that may be allowed
     * @param hy the extreme highest y coordinate (maximum) that may be allowed
     */
    void setBounds(float lx, float ly, float hx, float hy);

    /**
     * @return the location of the boid as a vector
     */
    Vec2f location();

    /**
     * Called once per frame for the boid to display anything it wants to display using a set of drawing tools
     */
    void draw(CanvasArtist artist);

    /**
     * Should evaluate method arguments as a simple boolean expression having the boid as the contextual
     * reference point. E.g. <br>
     * <blockquote><pre>
     *     attribute = "speed";
     *     operator = LESS_THAN;
     *     value = "10";
     * </pre></blockquote>
     *  may be interpreted as (in an abstract implementation of the boid class) <br>
     * <blockquote><pre>
     *     return this.speed < 10;
     * </pre></blockquote>
     *
     * @param attribute the name of the attribute of the boid to check
     * @param operator the type of comparison that should take place
     * @param value the value that the attribute should be checked against (implementation should be prepared to convert string to other data types)
     *
     * @return the result of the comparison
     */
    boolean filtered(String attribute, FilterOperator operator, String value);
}
