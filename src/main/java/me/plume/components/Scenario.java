package me.plume.components;

import javafx.scene.Scene;
import me.plume.engines.Launcher;
import me.plume.engines.ViewEngine;
import me.plume.engines.WorldEngine;

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
	public abstract void tick(long last, long now, double dt);
}
