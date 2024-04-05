package com.libnexus.boidsimulator.util;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils extends Color {
    public static Color fromRGB(int r, int g, int b) {
        return new Color((float) r / 255, (float) g / 255, (float) b / 255, 1);
    }

    public static Color fromRGB(int r, int g, int b, int a) {
        return new Color((float) r / 255, (float) g / 255, (float) b / 255, (float) a / 255);
    }

    public static Color difference(Color c1, Color c2, float factor) {
        return new Color(c1.r + (c2.r - c1.r) * factor, c1.g + (c2.g - c1.g) * factor, c1.b + (c2.b - c1.b) * factor, c1.a + (c2.a - c1.a) * factor);
    }

    public static boolean equals(Color c1, Color c2) {
        return c1.r == c2.r && c1.g == c2.g && c1.b == c2.b && c1.a == c2.a;
    }

    public static Color invert(Color colour) {
        return new Color(1 - colour.r, 1 - colour.g, 1 - colour.b, 1 - colour.a);
    }
}
