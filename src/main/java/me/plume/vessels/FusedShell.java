package me.plume.vessels;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.components.markers.Dot;
import me.plume.drivers.WorldEngine;

public class FusedShell extends Vessel {
	static final double R = 0.2;
	static final double EXPLOSION_R_MAX = 5;
	static final double EXPLOSION_TIME = 0.2;
	static final double MIN_SCALE = 10;
	static final double MIN_SCALE_WIDTH = 2;
	static final double MIN_SCALE_R = R*1.5;
	static final Color C = Color.ORANGE;
	static final Color EXPLOSION_COLOR = Color.DARKORANGE;
	static final double EXPLOSION_V_FACTOR = 0.1;
	static final double HIT_POINTS = 1;
	static final double DAMAGE = 10;
	double spawn;
	double life;
	WorldEngine world;
	public FusedShell(double x, double y, double vx, double vy, double spawn, double life, WorldEngine world) {
		super(x, y);
		this.spawn = spawn;
		this.life = life;
		this.vx = vx;
		this.vy = vy;
		this.world = world;
		this.r = R;
		this.hitpoints = HIT_POINTS;
		this.damage = DAMAGE;
	}
	public void update(double time, double dt) {
		if (remove) return;
		if (hitpoints <= 0) remove = true;
		if (time-spawn >= life) remove = true;
	}
	public void onRemove(double time, double dt) {
		Explosion exp = new Explosion(x, y, this.r, EXPLOSION_R_MAX, time, EXPLOSION_TIME, EXPLOSION_COLOR);
		exp.vx = vx*EXPLOSION_V_FACTOR;
		exp.vy = vy*EXPLOSION_V_FACTOR;
		world.effects.add(exp);
	}
	public Marker mark() {
		return new FusedShellMarker(getId(), x, y, R, C);
	}
}
class FusedShellMarker extends Dot {
	public FusedShellMarker(int id, double x, double y, double r, Color c) {
		super(id, x, y, r, c);
		scale = true;
	}
	public void render(GraphicsContext c, double x, double y, double s) {
		if (s<FusedShell.MIN_SCALE) {
			s = FusedShell.MIN_SCALE;
			c.setStroke(Color.YELLOW);
			c.setLineWidth(FusedShell.MIN_SCALE_WIDTH);
			c.strokeOval(x-FusedShell.MIN_SCALE_R*s, y-FusedShell.MIN_SCALE_R*s, FusedShell.MIN_SCALE_R*2*s, FusedShell.MIN_SCALE_R*2*s);
		}
		super.render(c, x, y, s);
	}
}
