package me.plume.scenarios;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import me.plume.components.Scenario;
import me.plume.engines.Launcher;
import me.plume.markers.Heading;
import me.plume.vessels.FusedShell;

public class TurretCIWS extends Scenario {
	static final double COOLDOWN = 0.013;
	static final double ANGLE_RATE = 0.01;
	static final double VELOCITY = 1800;
	static final double LIFE = 2;
	public TurretCIWS(Launcher instance) {
		super(instance);
		launcher.runWorld = true;
		launcher.runView = true;
	}
	boolean left, right, shoot;
	Heading h;
	public void init() {
		h = new Heading(-1, 0, 0, 30, Color.LIGHTGRAY);
		h.scale = true;
		h.length = 20;
		world.markers.add(h);
		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.A) left = true;
			if (e.getCode() == KeyCode.D) right = true;
			if (e.getCode() == KeyCode.SPACE) shoot = true;
		});
		scene.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.A) left = false;
			if (e.getCode() == KeyCode.D) right = false;
			if (e.getCode() == KeyCode.SPACE) shoot = false;
		});
	}
	long lastShot;
	public void tick(long last, long now, double dt) {
		if (left) h.angle += ANGLE_RATE;
		if (right) h.angle -= ANGLE_RATE;
		if (!shoot || now-lastShot < COOLDOWN*1000) return;
		world.vessels.add(new FusedShell((h.length+2*h.a)*Math.cos(h.angle), (h.length+2*h.a)*Math.sin(h.angle), VELOCITY*Math.cos(h.angle), VELOCITY*Math.sin(h.angle), now, LIFE));
		lastShot = now;
	}
}
