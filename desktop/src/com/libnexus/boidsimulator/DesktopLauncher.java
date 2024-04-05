package com.libnexus.boidsimulator;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setTitle("Boid Simulator GDX");
		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

		BoidSimulator boidSimulator = new BoidSimulator();
		boidSimulator.speed = 120 / Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate;

		new Lwjgl3Application(boidSimulator, config);
	}
}
