package me.plume.drivers.scenarios;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import me.plume.components.Scenario;
import me.plume.components.Vessel;
import me.plume.drivers.Launcher;
import me.plume.vessels.Missile;
import me.plume.vessels.Target;
import me.plume.vessels.navigation.LeadIntercept;

public class MissileTargetTrack extends Scenario {
	static final double TRACK_DIST = 50;
	static final double SPAWN_DELAY = 0.1;
	static final double TRACK_SWITCH_HOLD = 0.4;
	static final double TRACK_SWITCH_DELAY = 0.1;
	
	static final double SHOT_LIFE = 2;
	static final double SHOT_RECOIL = 1;
	static final double SHOOT_DELAY = 1.0/(4500/60);
	static final double SHOOT_VELOCITY = 1100;
	static final double DISPERSION = Math.toRadians(0.5);
	Target target;
	public MissileTargetTrack(Launcher instance) {
		super(instance);
		launcher.runWorld = true;
	}
	double sx, sy, x, y;
	boolean fire, track, breaking, next, prev, autobreak;
	int trackN=-1;
	public void init() {
		target = new Target(0, 0, 5, Color.CYAN, world);
		scene.setOnMouseMoved(e -> {
			logMouseCoord(e);
		});
		scene.setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.PRIMARY) logMouseCoord(e);
			if (e.getButton() == MouseButton.SECONDARY) logMouseCoord(e);
		});
		scene.setOnMousePressed(e -> {
			if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) track(e);
			if (e.getClickCount() == 1 && e.getButton() == MouseButton.PRIMARY) if (!target.turret.auto) target.turret.shoot(true);
			if (e.getButton() == MouseButton.SECONDARY) {
				fire = true;
				fireHold = 0;
				fired = 0;
			}
		});
		scene.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY) if (!target.turret.auto) target.turret.shoot(false);
			if (e.getButton() == MouseButton.SECONDARY) fire = false;
		});
		scene.setOnKeyPressed(e -> {
			KeyCode code = e.getCode();
			if (breaking && (code == KeyCode.W || code == KeyCode.A || code == KeyCode.S || code == KeyCode.D)) breakCheck(false);
			if (code == KeyCode.W) target.up = true;
			if (code == KeyCode.A) target.left = true;
			if (code == KeyCode.S) target.down = true;
			if (code == KeyCode.D) target.right = true;
			if (code == KeyCode.C) target.turret.auto = !target.turret.auto;
			if (code == KeyCode.B) {
				autobreak = !autobreak;
				if (autobreak && !breaking && !target.up && !target.left && !target.down && !target.right) breaking = true;
				if (!autobreak && breaking) breakCheck(false);
			}
			if (code == KeyCode.TAB) {
				if (next) return;
				next = true;
				switchHold = 0;
				switchN = 0;
			}
			if (code == KeyCode.SHIFT) {
				if (prev) return;
				prev = true;
				switchHold = 0;
				switchN = 0;
			}
			if (code == KeyCode.BACK_SPACE) {
				breakCheck(!breaking);
			}
			if (code == KeyCode.ESCAPE) {
				view.trackId = null;
			}
		});
		scene.setOnKeyReleased(e -> {
			KeyCode code = e.getCode();
			if (code == KeyCode.W) target.up = false;
			if (code == KeyCode.A) target.left = false;
			if (code == KeyCode.S) target.down = false;
			if (code == KeyCode.D) target.right = false;
			if (autobreak && (code == KeyCode.W || code == KeyCode.A || code == KeyCode.S || code == KeyCode.D)) {
				breaking = !target.up && !target.left && !target.down && !target.right;
			}
			if (code == KeyCode.TAB) next = false;
			if (code == KeyCode.SHIFT) prev = false;
		});
		world.vessels.add(target);
	}
	private void track(MouseEvent e) {
		if (world.vessels.isEmpty()) return;
		for (int i = 0; i < world.vessels.size(); i++) {
			Vessel v = world.vessels.get(i);
			double dx = (v.x+view.offsetX)*view.scale-e.getSceneX();
			double dy = -(v.y+view.offsetY)*view.scale-e.getSceneY();
			double d = Math.sqrt(dx*dx+dy*dy);
			if (d <= TRACK_DIST) {
				view.trackId = v.getId();
				trackN = i;
				break;
			}
		}
	}
	private void moveTrack() {
		if (Vessel.vessels.isEmpty()) return;
		if (next) trackN++;
		if (prev) trackN--;
		if (trackN >= world.vessels.size()) trackN=0;
		if (trackN < 0) trackN = world.vessels.size()-1;
		view.trackId = world.vessels.get(trackN).getId();
	}
	private void logMouseCoord(MouseEvent e) {
		sx = e.getSceneX();
		sy = e.getSceneY();
	}
	private void breakCheck(boolean breaking) {
		this.breaking = breaking;
		if (!breaking) {
			target.up = false;
			target.down = false;
			target.left = false;
			target.right = false;
		}
	}
	private void statusCheck() {
		if (target.up || target.left || target.right || target.down) target.status = Color.CORNFLOWERBLUE;
		else target.status = null;
		if (breaking) target.status = Color.SALMON;
	}
	double fireHold;
	int fired;
	double switchHold;
	int switchN;
	public void tick(double time, double dt) {
		x = sx/view.scale-view.offsetX;
		y = -sy/view.scale-view.offsetY;
		if (!target.turret.auto) target.turret.angle((view.trackId != null && target.getId() == view.trackId)? Math.atan2(-sy+scene.getHeight()/2, sx-scene.getWidth()/2): Math.atan2(y-target.y, x-target.x));
		if (fire) {
			if (fireHold == 0) fireHold = time;
			if (fired <= (time-fireHold)/SPAWN_DELAY) {
				fired++;
				Missile m = new Missile(x, y, 
						Missile.SIZE, time, Missile.LIFE, target, Color.RED, world);
				m.navigator = new LeadIntercept(m, target);
				world.vessels.add(m);
			}
		}
		if (breaking) {
			if (Math.abs(target.vx) <= Target.ACCEL*dt) target.vx=0;
			if (Math.abs(target.vy) <= Target.ACCEL*dt) target.vy=0;
			target.left = target.vx>0;
			target.right = target.vx<0;
			target.up = target.vy<0;
			target.down = target.vy>0;
		}
		statusCheck();
		if (next || prev) {
			if (switchN == 0) {
				switchHold = time+TRACK_SWITCH_HOLD;
			} else if (switchN > (time-switchHold)/TRACK_SWITCH_DELAY) return;
			switchN++;
			moveTrack();
		}
	}
}
