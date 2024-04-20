package me.plume.vessels.navigation;

import me.plume.components.Vessel;
import me.plume.vessels.Missile;

public class PurePursuit extends Navigator {
	public PurePursuit(Missile missile, Vessel vessel) {
		super(missile, vessel);
	}
	double theta, angle;
	public void tick(double time, double dt) {
		theta = Math.atan2(target.y-missile.y, target.x-missile.x);
		theta = Navigator.posRad(theta);
		angle = Navigator.posRad(missile.angle);
		if (Math.abs(theta-angle) <= Missile.MAX_ROT_RATE*dt) angle=theta;
		if (angle<theta) {
			if (theta-angle>Math.PI) angle-=Missile.MAX_ROT_RATE*dt;
			else angle+=Missile.MAX_ROT_RATE*dt;
		}
		if (angle>theta) {
			if (angle-theta>Math.PI) angle+=Missile.MAX_ROT_RATE*dt;
			else angle-=Missile.MAX_ROT_RATE*dt;
		}
		missile.angle = angle;
	}
}
