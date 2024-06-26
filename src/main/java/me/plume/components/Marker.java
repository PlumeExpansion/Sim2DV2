package me.plume.components;

import javafx.scene.canvas.GraphicsContext;

public abstract class Marker { 
	private int id;
	public double x, y;
	public Marker(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	public int getId() {
		return id;
	}
	public abstract void render(GraphicsContext c, double x, double y, double s);
	public static double x(double x, double y, double angle) {
		return x*Math.cos(angle)-y*Math.sin(angle);
	}
	public static double y(double x, double y, double angle) {
		return -x*Math.sin(angle)-y*Math.cos(angle);
	}
}
