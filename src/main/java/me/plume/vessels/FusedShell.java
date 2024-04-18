package me.plume.vessels;

import javafx.scene.paint.Color;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.markers.Dot;

public class FusedShell extends Vessel {
	static final double R = 0.2;
	static final Color C = Color.ORANGE;
	double spawn;
	double life;
	public FusedShell(double x, double y, double vx, double vy, double spawn, double life) {
		super(x, y);
		this.spawn = spawn;
		this.life = life;
		this.vx = vx;
		this.vy = vy;
	}
	public void update(double time, double dt) {
		if (time-spawn >= life) remove = true;
	}
	public Marker mark() {
		Dot d = new Dot(getId(), x, y, R, C);
		d.scale = true;
		return d;
	}
}
