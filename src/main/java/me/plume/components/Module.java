package me.plume.components;

import javafx.scene.canvas.GraphicsContext;

public abstract class Module {
	public double ox, oy;
	public Vessel vessel;
	public Module(Vessel vessel) {
		this.vessel = vessel;
	}
	public abstract void update(double time, double dt);
	public abstract void render(GraphicsContext c, double x, double y, double s);
}
