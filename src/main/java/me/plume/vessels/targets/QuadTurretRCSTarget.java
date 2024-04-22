package me.plume.vessels.targets;

import java.util.Arrays;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.drivers.WorldEngine;
import me.plume.modules.CIWSTurret;
import me.plume.vessels.aiming.CIWSLINetwork;

public class QuadTurretRCSTarget extends TurretedRCSTarget {
	static final double SHOT_LIFE = 2;
	static final double SHOT_DELAY = 1.0/(4500/60);
	static final double SHOT_VELOCITY = 1100;
	static final double DISPERSION = Math.toRadians(0.5);
	static final double RADIAL_OFFSET = 5;
	public List<CIWSTurret> turrets;
	boolean auto;
	CIWSLINetwork network;
	WorldEngine world;
	public QuadTurretRCSTarget(double x, double y, double r, Color c, WorldEngine world) {
		super(x, y, r, c);
		this.world = world;
		CIWSTurret tr = new CIWSTurret(this, world, SHOT_DELAY, SHOT_VELOCITY, SHOT_LIFE, DISPERSION);
		tr.ox = RADIAL_OFFSET*Math.sqrt(2)/2;
		tr.oy = RADIAL_OFFSET*Math.sqrt(2)/2;
		tr.minAngle = -Math.PI/6;
		tr.maxAngle = 2*Math.PI/3;
		CIWSTurret tl = new CIWSTurret(this, world, SHOT_DELAY, SHOT_VELOCITY, SHOT_LIFE, DISPERSION);
		tl.ox = -RADIAL_OFFSET*Math.sqrt(2)/2;
		tl.oy = RADIAL_OFFSET*Math.sqrt(2)/2;
		tl.minAngle = Math.PI/3;
		tl.maxAngle = 7*Math.PI/6;
		CIWSTurret bl = new CIWSTurret(this, world, SHOT_DELAY, SHOT_VELOCITY, SHOT_LIFE, DISPERSION);
		bl.ox = -RADIAL_OFFSET*Math.sqrt(2)/2;
		bl.oy = -RADIAL_OFFSET*Math.sqrt(2)/2;
		bl.minAngle = 5*Math.PI/6;
		bl.maxAngle = 5*Math.PI/3;
		CIWSTurret br = new CIWSTurret(this, world, SHOT_DELAY, SHOT_VELOCITY, SHOT_LIFE, DISPERSION);
		br.ox = RADIAL_OFFSET*Math.sqrt(2)/2;
		br.oy = -RADIAL_OFFSET*Math.sqrt(2)/2;
		br.minAngle = -2*Math.PI/3;
		br.maxAngle = Math.PI/6;
		turrets = Arrays.asList(tl, tr, bl, br);
		network = new CIWSLINetwork(this, turrets);
		turrets.forEach(t -> {
			t.maxAngleRate = Math.toRadians(180);
			t.networked = true;
		});
	}
	public void setTargets(List<Vessel> targets) {
		turrets.forEach(t -> t.aiming.targets = targets);
	}
	public void setAuto(boolean auto) {
		this.auto = auto;
		turrets.forEach(t -> t.auto = auto);
	}
	public boolean auto() {
		return auto;
	}
	public void setAim(double x, double y, double dt) {
		turrets.forEach(t -> t.angle(Math.atan2(y-(this.y+t.oy), x-(this.x+t.ox)), dt));
	}
	public void setShoot(boolean shoot) {
		turrets.forEach(t -> t.shoot(shoot));
	}
	public void update(double time, double dt) {
		super.update(time, dt);
		network.updateTracks(world.vessels, time, dt);
		turrets.forEach(t -> t.update(time, dt));
	}
	public Marker mark() {
		return new QuadTurretRCSTargetMarker(getId(), x, y, this);
	}
}
class QuadTurretRCSTargetMarker extends RCSTargetMarker {
	QuadTurretRCSTarget t;
	public QuadTurretRCSTargetMarker(int id, double x, double y, QuadTurretRCSTarget target) {
		super(id, x, y, target);
		t = target;
	}
	public void render(GraphicsContext c, double x, double y, double s) {
		super.render(c, x, y, s);
		t.turrets.forEach(t -> t.render(c, x, y, s));
	}
}