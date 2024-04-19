package me.plume.components.markers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.plume.components.Marker;

public class Dot extends Marker {
	public Color color;
	public double r;
	public boolean scale;
	public Dot(int id, double x, double y, double r, Color c) {
		super(id, x, y);
		this.r = r;
		this.color = c;
	}
	public void render(GraphicsContext c, double x, double y, double s) {
		c.setFill(color);
		if (!scale) s=1;
		c.fillOval(x-r*s, y-r*s, 2*r*s, 2*r*s);
	}
}
