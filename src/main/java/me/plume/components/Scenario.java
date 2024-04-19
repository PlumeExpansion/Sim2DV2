package me.plume.components;

import javafx.scene.Scene;
import me.plume.drivers.Launcher;
import me.plume.drivers.ViewEngine;
import me.plume.drivers.WorldEngine;

public abstract class Scenario {
	public Launcher launcher;
	public Scene scene;
	public ViewEngine view;
	public WorldEngine world;
	public Scenario(Launcher instance) {
		launcher = instance;
		scene = launcher.scene;
		view = launcher.view;
		world = launcher.world;
	}
	public abstract void init();
	public abstract void tick(double time, double dt);
}
