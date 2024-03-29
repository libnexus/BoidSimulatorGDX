package com.libnexus.boidsimulator.api.plugin;

import com.badlogic.gdx.Input;
import com.libnexus.boidsimulator.World;
import com.libnexus.boidsimulator.entity.boid.Boid;
import com.libnexus.boidsimulator.entity.boid.BoidAgency;
import com.libnexus.boidsimulator.entity.boid.DefaultBoidAgency;
import com.libnexus.boidsimulator.math.Vector2f;
import jdk.jpackage.internal.CLIHelp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PluginBoidAgency extends BoidAgency {
    protected Plugin plugin;
    public PluginBoidAgency(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void killAll() {
        for (Boid boid : World.getBoidsOfAgency(this))
            plugin.removeBoid(boid);
    }

    @Override
    public void kill(Boid boid) { plugin.removeBoid(boid); }
}

