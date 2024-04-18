package me.plume.vessels;

import javafx.scene.paint.Color;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.markers.Dot;

public class FusedShell extends Vessel {
	static final double R = 2;
	static final Color C = Color.ORANGE;
	long spawn;
	double life;
	public FusedShell(double x, double y, double vx, double vy, long spawn, double life) {
		super(x, y);
		this.spawn = spawn;
		this.life = life;
		this.vx = vx;
		this.vy = vy;
	}
	public void update(long last, long now, double dt) {
		if (now-spawn >= life*1000) remove = true;
	}
	public Marker mark() {
		Dot d = new Dot(getId(), x, y, R, C);
		d.scale = true;
		return d;
	}
}
