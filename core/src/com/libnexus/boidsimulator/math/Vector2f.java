package com.libnexus.boidsimulator.math;

public class Vector2f {
    public float x = 0;
    public float y = 0;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f() {

    }

    public Vector2f add(Vector2f vector) {
        x += vector.x;
        y += vector.y;
        return this;
    }

    public Vector2f add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2f addedTo(Vector2f vector) {
        return new Vector2f(x + vector.x, y + vector.y);
    }

    public Vector2f subtracted(Vector2f vector2f) {
        return new Vector2f(x - vector2f.x, y - vector2f.y);
    }

    public Vector2f multiplyBy(Vector2f vector) {
        x *= vector.x;
        y *= vector.y;
        return this;
    }

    public Vector2f multiplyBy(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vector2f multipliedBy(Vector2f vector) {
        return new Vector2f(x * vector.x, y * vector.y);
    }

    public Vector2f multipliedBy(float scalar) {
        return new Vector2f(x * scalar, y * scalar);
    }

    public Vector2f divideBy(Vector2f vector) {
        x /= vector.x != 0 ? vector.y : 1;
        y /= vector.y != 0 ? vector.y : 1;
        return this;
    }

    public Vector2f divideBy(float scalar) {
        x /= scalar != 0 ? scalar : 1;
        y /= scalar != 0 ? scalar : 1;
        return this;
    }

    public Vector2f dividedBy(Vector2f vector) {
        return new Vector2f(x / vector.x != 0 ? vector.y : 1, y / vector.y != 0 ? vector.y : 1);
    }

    public Vector2f dividedBy(float scalar) {
        return new Vector2f(x / scalar != 0 ? scalar : 1, y / scalar != 0 ? scalar : 1);
    }

    public float distance(Vector2f vector) {
        float xd = vector.x - x;
        float yd = vector.y - y;
        return (float) Math.sqrt(xd * xd + yd * yd);
    }

    public float abs() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2f opposite() {
        return new Vector2f(-x, -y);
    }

    public Vector2f set(Vector2f vector) {
        this.x = vector.x;
        this.y = vector.y;
        return this;
    }

    public Vector2f copy() {
        return new Vector2f(x, y);
    }

    public boolean between(Vector2f a, Vector2f b, float threshold) {
        Vector2f max, min;
        max = max(a, b);
        min = min(a, b);
        return Math.abs(distance(min) + distance(max) - min.distance(max)) < threshold;
    }

    public Vector2f normalize() {
        float length = abs();

        x /= length;
        y /= length;

        return this;
    }

    public static Vector2f max(Vector2f a, Vector2f b) {
        return a.abs() > b.abs() ? a : b;
    }

    public static Vector2f min(Vector2f a, Vector2f b) {
        return a.abs() < b.abs() ? a : b;
    }

    public static float dot(Vector2f a, Vector2f b) {
        return a.x * a.y + b.x * b.y;
    }
}
