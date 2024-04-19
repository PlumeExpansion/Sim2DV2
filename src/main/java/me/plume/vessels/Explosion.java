package me.plume.vessels;

import javafx.scene.paint.Color;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.components.markers.Dot;

public class Explosion extends Vessel {
	double rInit, rMax;
	Color color;
	double opacity, radius;
	double spawn, life, percent;
	int r, g, b;
	public Explosion(double x, double y, double rInit, double rMax, double spawn, double life, Color c) {
		super(x, y);
		this.rInit = rInit;
		this.rMax = rMax;
		this.spawn = spawn;
		this.life = life;
		this.color = c;
		r = (int) (color.getRed()*255);
		g = (int) (color.getGreen()*255);
		b = (int) (color.getBlue()*255);
	}
	public void update(double time, double dt) {
		if (remove) return;
		percent = (time-spawn)/life;
		if (percent<1) {
			opacity=1-percent;
			radius = rInit+(rMax-rInit)*percent;
		}
		else remove = true;
	}
	public void onRemove(double time, double dt) {}
	public Marker mark() {
		return new ExplosionMarker(getId(), x, y, radius, Color.rgb(r, g, b, opacity));
	}

}
class ExplosionMarker extends Dot {
	public ExplosionMarker(int id, double x, double y, double r, Color c) {
		super(id, x, y, r, c);
		scale = true;
	}
}