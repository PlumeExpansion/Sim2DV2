package me.plume.vessels;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.components.markers.Heading;
import me.plume.drivers.WorldEngine;
import me.plume.vessels.navigation.Navigator;

public class Missile extends Vessel {
	static final double BURN_TIME = 6;
	static final double ACCEL = 1372/BURN_TIME;
	public static final double SIZE = 3;
	public static final double LIFE = 30;
	public static final double MAX_ROT_RATE = Math.toRadians(120);
	static final double MIN_SCALE = 3;
	static final double MIN_SCALE_WIDTH = 2;
	static final double VELOCITY_SCALAR = 0.1;
	static final double EXPLOSION_R_MIN = 5;
	static final double EXPLOSION_R_MAX = 100;
	static final double EXPLOSION_TIME = 1;
	static final Color EXPLOSION_COLOR = Color.LIGHTGOLDENRODYELLOW;
	static final double COLLISION_R_BUFFER = 0.5;
	static final double PROXY_R = 1.5;
	static final double EXPLOSION_V_FACTOR = 0.1;
	Color color;
	Target target;
	public Navigator navigator;
	public double angle, v;
	double size;
	double spawn, life;
	WorldEngine world;
	public Missile(double x, double y, double size, double spawn, double life, Target target, Color c, WorldEngine world) {
		super(x, y);
		this.size = size;
		this.spawn = spawn;
		this.life = life;
		this.target = target;
		this.color = c;
		angle = Math.atan2(target.y-y, target.x-x);
		this.world = world;
	}
	public void update(double time, double dt) {
		if (remove) return;
		if (time-spawn>=life) remove = true;;
		world.vessels.stream().filter(v -> v!= this).forEach(v -> {
			if (dist(v)>size+PROXY_R) return;
			remove = true;
			if (!v.immune) v.remove = true;
		});
		world.exclusiveColliders.forEach(v -> {
			if (dist(v)>size+COLLISION_R_BUFFER) return;
			remove = true;
			v.remove = true;
		});
		if (time-spawn<=BURN_TIME) v += ACCEL*dt;
		
		if (navigator != null) navigator.tick(time, dt);
		
		vx = Math.cos(angle)*v;
		vy = Math.sin(angle)*v;
	}
	public void onRemove(double time, double dt) {
		Explosion exp = new Explosion(x, y, EXPLOSION_R_MIN, EXPLOSION_R_MAX, time, EXPLOSION_TIME, EXPLOSION_COLOR);
		exp.vx = vx*EXPLOSION_V_FACTOR;
		exp.vy = vy*EXPLOSION_V_FACTOR;
		world.effects.add(exp);
	}
	public Marker mark() {
		return new MissileMarker(getId(), x, y, this);
	}
	public static double posRad(double rad) {
		rad %= 2*Math.PI;
		return rad>0? rad : rad+2*Math.PI;
	}
}
class MissileMarker extends Heading {
	Missile m;
	double minScaleR;
	public MissileMarker(int id, double x, double y, Missile missile) {
		super(id, x, y, Missile.SIZE, missile.color);
		m = missile;
		scale = true;
		angle = missile.angle;
		minScaleR = 2*a*1.5;
	}
	public void render(GraphicsContext c, double x, double y, double s) {
		if (s<Missile.MIN_SCALE) {
			s = Missile.MIN_SCALE;
			c.setStroke(Color.YELLOW);
			c.setLineWidth(Missile.MIN_SCALE_WIDTH);
			c.strokeOval(x-minScaleR*s, y-minScaleR*s, minScaleR*2*s, minScaleR*2*s);
		}
		length = m.v*Missile.VELOCITY_SCALAR;
		super.render(c, x, y, s);
	}
}