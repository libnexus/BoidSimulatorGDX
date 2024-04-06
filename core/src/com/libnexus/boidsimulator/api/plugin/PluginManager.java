package com.libnexus.boidsimulator.api.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.libnexus.boidsimulator.BoidSimulator;

import java.util.LinkedList;
import java.util.List;

public class PluginManager {
    private final PluginLoader pluginLoader;
    private final List<Plugin> plugins = new LinkedList<>();
    private final BoidSimulator simulator;

    public PluginManager(BoidSimulator simulator, FileHandle pluginDirectory) {
        this.simulator = simulator;
        pluginLoader = new PluginLoader(simulator, pluginDirectory);
    }

    public void loadPlugins() {
        plugins.addAll(pluginLoader.loadPluginsFromDirectory());
        for (Plugin plugin : plugins)
            plugin.init();
        simulator.initAgencies();
    }

    public void unloadPlugin(Plugin plugin) {
        plugin.pruneMyBoids();
        plugin.pruneMyEffects();
        plugin.allAgenciesKillAll();
        plugin.pruneMyAgencies();
        plugin.dispose();
    }

    public void unloadPlugins() {
        for (Plugin plugin : plugins)
            unloadPlugin(plugin);
        plugins.clear();
    }

    public void reloadPlugins() {
        unloadPlugins();
        loadPlugins();
    }

    public List<Plugin> plugins() {
        return plugins;
    }
}
