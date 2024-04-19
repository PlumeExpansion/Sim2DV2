package me.plume.components.markers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.plume.components.Marker;

public class Heading extends Marker {
	public Color color;
	public double side;
	public double a, lx, w;
	public double length;
	public double angle;
	public boolean scale;
	public Heading(int id, double x, double y, double s, Color c) {
		super(id, x, y);
		this.side = s;
		this.color = c;
		a = s/2/Math.sqrt(3);
		w = side/10;
		lx = 2*a-side/20*Math.sqrt(3);
	}
	public void render(GraphicsContext c, double x, double y, double s) {
		if (!scale) s = 1;
		c.setFill(color);
		c.fillPolygon(new double[] {x(-a, -side/2)*s+x, x(-a, side/2)*s+x, x(2*a, 0)*s+x}, 
				new double[] {y(-a, -side/2)*s+y, y(-a, side/2)*s+y, y(2*a, 0)*s+y}, 3);
		if (length == 0) return;
		c.fillPolygon(new double[] {x(lx, -w/2)*s+x, x(lx, w/2)*s+x, x(lx+length, w/2)*s+x, x(lx+length, -w/2)*s+x}, 
				new double[] {y(lx, -w/2)*s+y, y(lx, w/2)*s+y, y(lx+length, w/2)*s+y, y(lx+length, -w/2)*s+y}, 4);
	}
	private double x(double x, double y) {
		return x*Math.cos(angle)-y*Math.sin(angle);
	}
	private double y(double x, double y) {
		return -x*Math.sin(angle)-y*Math.cos(angle);
	}
}
