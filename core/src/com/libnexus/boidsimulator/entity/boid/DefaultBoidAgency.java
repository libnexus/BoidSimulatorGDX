package com.libnexus.boidsimulator.entity.boid;

import com.badlogic.gdx.Input;
import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.math.Vector2f;

public class DefaultBoidAgency extends BoidAgency {
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
        World.boids().removeIf(boid -> boid.getClass() == Boid.class);
    }

    @Override
    public void kill(Boid boid) {
        if (boid.getClass() == Boid.class)
            World.boids().remove(boid);
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
