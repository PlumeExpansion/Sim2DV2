package me.plume.vessels.navigation;

import me.plume.components.Vessel;
import me.plume.vessels.Missile;

public class PropNav extends Navigator {
	static final double PROP_CONSTANT = 60;
	public PropNav(Missile missile, Vessel vessel) {
		super(missile, vessel);
	}
	double angleLOS, angleLOSprev;
	double xPrev, yPrev;
	double txPrev, tyPrev;
	double dist, distPrev;
	double dtPrev;
	double dx, dy, vClosing, rate;
	boolean running;
	public void tick(double time, double dt) {
		dx = target.x-missile.x;
		dy = target.y-missile.y;
		angleLOS = Navigator.posRad(Math.atan2(dy, dx));
		dist = Math.sqrt(dx*dx+dy*dy);
		if (running) {
			vClosing = (dist-distPrev)/dtPrev;
			rate = PROP_CONSTANT*vClosing*(angleLOS-angleLOSprev)*dt;
		} else running = true;
		angleLOSprev = angleLOS;
		xPrev = missile.x;
		yPrev = missile.y;
		txPrev = target.x;
		tyPrev = target.y;
		distPrev = dist;
		dtPrev = dt;
		missile.angle += Math.abs(rate) < Missile.MAX_ROT_RATE*dt? rate : Missile.MAX_ROT_RATE*dt;
	}
}
