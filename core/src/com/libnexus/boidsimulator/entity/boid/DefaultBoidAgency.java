package com.libnexus.boidsimulator.entity.boid;

import com.badlogic.gdx.Input;
import com.libnexus.boidsimulator.util.Vector2f;
import com.libnexus.boidsimulator.world.World;

public class DefaultBoidAgency extends BoidAgency {
    public static final DefaultBoidAgency INSTANCE = new DefaultBoidAgency();

    private DefaultBoidAgency() {

    }

    @Override
    public Boid make(Vector2f location) {
        return new Boid(this, location);
    }

    @Override
    public String[] qualifiers() {
        return new String[]{"core:default", "boid", "default", "Boid"};
    }

    @Override
    public int[] keyBindings() {
        return new int[]{Input.Keys.NUM_1, Input.Keys.B};
    }

    @Override
    public String name() {
        return "Default Boid Agency";
    }

    @Override
    public boolean takeResponsibility(Boid boid) {
        return true;
    }

    @Override
    public void killAll() {

    }

    @Override
    public void kill(Boid boid) {

    }

    public void setCurrValues(String name, float value) {
        for (Boid boid : World.boids()) {
            switch (name) {
                case "op:boost": {
                    boid.currVelocity.multiplyBy(value);
                    break;
                }
                case "b:max_speed": {
                    boid.currSpeedLimitUpper = (int) value;
                    break;
                }
                case "b:min_speed": {
                    boid.currSpeedLimitLower = (int) value;
                    break;
                }
                case "b:matching_factor": {
                    boid.currMatchingFactor = (int) value;
                    break;
                }
                case "b:shyness_factor": {
                    boid.currShynessFactor = (int) value;
                    break;
                }
                case "b:shyness_threshold": {
                    boid.currShynessThreshold = (int) value;
                    break;
                }
                default:
            }
        }
    }
}
