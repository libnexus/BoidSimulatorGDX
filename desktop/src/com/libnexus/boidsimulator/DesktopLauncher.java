package com.libnexus.boidsimulator;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import javax.swing.*;
import java.util.HashMap;


// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setTitle("Boid Simulator GDX");
        Graphics.DisplayMode displayMode = getDisplayChoice();
        if (displayMode == null)
            return;
        config.setFullscreenMode(displayMode);
        BoidSimulator boidSimulator = new BoidSimulator();

        new Lwjgl3Application(boidSimulator, config);
    }

    public static Graphics.DisplayMode getDisplayChoice() {
        HashMap<String, Graphics.DisplayMode> displayModes = new HashMap<>();

        for (Graphics.Monitor monitor : Lwjgl3ApplicationConfiguration.getMonitors()) {
            Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode(monitor);
            displayModes.put(String.format("%s %dHz (%dx%d)", monitor.name, displayMode.refreshRate, displayMode.width, displayMode.height), displayMode);
        }

        String[] monitorNames = displayModes.keySet().toArray(new String[0]);

        int response = JOptionPane.showOptionDialog(null, "Please choose a monitor to display the game in fullscreen on", "Choose a monitor", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, monitorNames, monitorNames[0]);
        if (response == -1)
            return null;
        else
            return displayModes.get(monitorNames[response]);
    }
}
