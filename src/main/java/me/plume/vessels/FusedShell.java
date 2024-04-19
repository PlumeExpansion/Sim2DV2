package me.plume.vessels;

import javafx.scene.paint.Color;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.components.markers.Dot;
import me.plume.drivers.WorldEngine;

public class FusedShell extends Vessel {
	static final double EXPLOSION_R_MAX = 10;
	static final double EXPLOSION_TIME = 0.2;
	static final double R = 0.2;
	static final Color C = Color.ORANGE;
	static final Color EXPLOSION_COLOR = Color.LIGHTGOLDENRODYELLOW;
	static final double EXPLOSION_V_FACTOR = 0.1;
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
	}
	public void update(double time, double dt) {
		if (time-spawn >= life) remove = true;
	}
	public void onRemove(double time, double dt) {
		Explosion exp = new Explosion(x, y, R, EXPLOSION_R_MAX, time, EXPLOSION_TIME, EXPLOSION_COLOR);
		exp.vx = vx*EXPLOSION_V_FACTOR;
		exp.vy = vy*EXPLOSION_V_FACTOR;
		world.effects.add(exp);
	};
	public Marker mark() {
		Dot d = new Dot(getId(), x, y, R, C);
		d.scale = true;
		return d;
	}
}
