package me.plume.vessels.targets;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.drivers.WorldEngine;
import me.plume.modules.CIWSTurret;

public class MonoTurretRCSTarget extends TurretedRCSTarget {
	static final double SHOT_LIFE = 2;
	static final double SHOT_DELAY = 1.0/(4500/60);
	static final double SHOT_VELOCITY = 1100;
	static final double DISPERSION = Math.toRadians(0.5);
	CIWSTurret turret;
	public MonoTurretRCSTarget(double x, double y, double r, Color c, WorldEngine world) {
		super(x, y, r, c);
		turret = new CIWSTurret(this, world, SHOT_DELAY, SHOT_VELOCITY, SHOT_LIFE, DISPERSION);
		turret.maxAngleRate = Math.toRadians(115);
	}
	public void setTargets(List<Vessel> targets) {
		turret.aiming.targets = targets;
	}
	public void setAuto(boolean auto) {
		turret.auto = auto;
	}
	public boolean auto() {return turret.auto;}
	public void setAim(double x, double y, double dt) {
		turret.angle(Math.atan2(y-this.y, x-this.x), dt);
	}
	public void setShoot(boolean shoot) {
		turret.shoot(shoot);
	}
	public void update(double time, double dt) {
		super.update(time, dt);
		turret.update(time, dt);
	}
	public Marker mark() {
		return new MonoTurretRCSTargetMarker(getId(), x, y, this);
	}
}
class MonoTurretRCSTargetMarker extends RCSTargetMarker {
	MonoTurretRCSTarget t;
	public MonoTurretRCSTargetMarker(int id, double x, double y, MonoTurretRCSTarget target) {
		super(id, x, y, target);
		t = target;
	}
	public void render(GraphicsContext c, double x, double y, double s) {
		super.render(c, x, y, s);
		t.turret.render(c, x, y, s);
	}
}