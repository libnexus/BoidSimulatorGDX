package com.libnexus.future.boidsimulator.util;

/**
 * The colour utility class for interacting with the displayed aspects of the simulator
 */
public class Colour {
    /**
     * The red value of the colour (0-1)
     */
    private float red = 0f;
    /**
     * The green value of the colour (0-1)
     */
    private float green = 0f;
    /**
     * The blue value of the colour (0-1)
     */
    private float blue = 0f;

    /**
     * Sets the red value of the colour to a floating point value between 0 and 1 (or 0 or 1); and returns itself for chaining. <br>
     * If below 0, is set to 0; if above 1, is set to 1.
     *
     * @param red the new red value (must be 0-1)
     * @return <code>this</code>
     */
    public Colour setRed(float red) {
        if (red >= 0 && red <= 1) this.red = red;
        else if (red > 1) this.red = 1;
        else this.red = 0;
        return this;
    }

    /**
     * Sets the red value of the colour to floating point equivalent of the RGB value from an integer value between 0 and 255 (or 0 or 255); and returns itself for chaining. <br>
     * If below 0, is set to 0; if above 255 is set to 255
     *
     * @param red the new red value (must be 0-255)
     * @return <code>this</code>
     */
    public Colour setRed(int red) {
        return setRed(red / 255f);
    }

    /**
     * @return the floating point red value (0-1)
     */
    public float getRed() {
        return red;
    }

    /**
     * @return the RGB red value (0-255)
     */
    public int getRedRGB() {
        return (int) red * 255;
    }

    /**
     * Sets the green value of the colour to a floating point value between 0 and 1 (or 0 or 1); and returns itself for chaining. <br>
     * If below 0, is set to 0; if above 1, is set to 1.
     *
     * @param green the new green value (must be 0-1)
     * @return <code>this</code>
     */
    public Colour setGreen(float green) {
        if (green >= 0 && green <= 1) this.green = green;
        else if (green > 1) this.green = 1;
        else this.green = 0;
        return this;
    }

    /**
     * Sets the green value of the colour to floating point equivalent of the RGB value from an integer value between 0 and 255 (or 0 or 255); and returns itself for chaining. <br>
     * If below 0, is set to 0; if above 255 is set to 255
     *
     * @param green the new green value (must be 0-255)
     * @return <code>this</code>
     */
    public Colour setGreen(int green) {
        return setGreen(green / 255f);
    }

    /**
     * @return the floating point green value (0-1)
     */
    public float getGreen() {
        return green;
    }

    /**
     * @return the RGB green value (0-255)
     */
    public int getGreenRGB() {
        return (int) green * 255;
    }

    /**
     * Sets the blue value of the colour to a floating point value between 0 and 1 (or 0 or 1); and returns itself for chaining. <br>
     * If below 0, is set to 0; if above 1, is set to 1.
     *
     * @param blue the new blue value (must be 0-1)
     * @return <code>this</code>
     */
    public Colour setBlue(float blue) {
        if (blue >= 0 && blue <= 1) this.blue = blue;
        else if (blue > 1) this.blue = 1;
        else this.blue = 0;
        return this;
    }

    /**
     * Sets the blue value of the colour to floating point equivalent of the RGB value from an integer value between 0 and 255 (or 0 or 255); and returns itself for chaining. <br>
     * If below 0, is set to 0; if above 255 is set to 255
     *
     * @param blue the new blue value (must be 0-255)
     * @return <code>this</code>
     */
    public Colour setBlue(int blue) {
        return setBlue(blue / 255f);
    }

    /**
     * @return the floating point blue value (0-1)
     */
    public float getBlue() {
        return blue;
    }

    /**
     * @return the RGB blue value (0-255)
     */
    public int getBlueRGB() {
        return (int) blue * 255;
    }

    /**
     * Calls {@link Colour#setRed(int)}, {@link Colour#setGreen(int)} and {@link Colour#setBlue(int)} for <code>r</code>, <code>g</code> and <code>b</code> respectively; and returns itself for chaining
     *
     * @param r the new red value (must be 0-255)
     * @param g the new green value (must be 0-255)
     * @param b the new blue value (must be 0-255)
     * @return <code>this</code>
     */
    public Colour setRGB(int r, int g, int b) {
        setRed(r);
        setGreen(g);
        setBlue(b);
        return this;
    }

    /**
     * Calls {@link Colour#setRed(float)}, {@link Colour#setGreen(float)} and {@link Colour#setBlue(float)} for <code>r</code>, <code>g</code> and <code>b</code> respectively; and returns itself for chaining
     *
     * @param r the new red value (must be 0-1)
     * @param g the new green value (must be 0-1)
     * @param b the new blue value (must be 0-1)
     * @return <code>this</code>
     */
    public Colour set(float r, float g, float b) {
        setRed(r);
        setGreen(g);
        setBlue(b);
        return this;
    }
}
