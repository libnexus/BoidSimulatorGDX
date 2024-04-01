package com.libnexus.boidsimulator.api.plugin;

import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;

public abstract class PluginBoidAgency extends BoidAgency {
    protected Plugin plugin;

    public PluginBoidAgency(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void killAll() {
        for (Boid boid : World.getBoidsOfAgency(this)) {
            plugin.removeBoid(boid);
        }
    }

    @Override
    public void kill(Boid boid) {
        plugin.removeBoid(boid);
    }
}

