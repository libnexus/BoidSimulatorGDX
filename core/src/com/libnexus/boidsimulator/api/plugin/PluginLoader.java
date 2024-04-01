package com.libnexus.boidsimulator.api.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.libnexus.boidsimulator.BoidSimulator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    private final BoidSimulator simulator;
    private final FileHandle pluginDirectory;

    public PluginLoader(BoidSimulator simulator, FileHandle pluginDirectory) {
        this.simulator = simulator;
        this.pluginDirectory = pluginDirectory;
    }

    public List<Plugin> loadPluginsFromDirectory() {
        List<Plugin> plugins = new LinkedList<>();

        if (!pluginDirectory.exists() || !pluginDirectory.isDirectory()) {
            return plugins;
        }

        File[] files = pluginDirectory.file().listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) {
            return plugins;
        }

        for (File file : files) {
            Plugin plugin = loadPluginFromFile(file);
            if (plugin == null)
                System.err.printf("Could not load plugin '%s', it has no plugin class entry point%n", file.getName());
            else
                plugins.add(plugin);
        }

        return plugins;
    }

    private Plugin loadPluginFromFile(File file) {
        try {
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});

            for (String className : getClassNamesFromJar(file)) {
                Class<?> clazz = classLoader.loadClass(className);

                if (Plugin.class.isAssignableFrom(clazz)) {
                    classLoader.close();
                    return (Plugin) clazz.getDeclaredConstructor(BoidSimulator.class).newInstance(simulator);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private List<String> getClassNamesFromJar(File jarFile) {
        List<String> classNames = new ArrayList<>();

        JarFile jar;
        try {
            jar = new JarFile(jarFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            String name = entries.nextElement().getName();
            if (name.endsWith(".class")) {
                classNames.add(name.replace("/", ".").replaceAll("\\.class$", ""));
            }
        }

        return classNames;
    }
}
