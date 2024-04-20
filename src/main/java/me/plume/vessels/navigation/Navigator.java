package me.plume.vessels.navigation;

import me.plume.components.Vessel;
import me.plume.vessels.Missile;

public abstract class Navigator {
	Missile missile;
	Vessel target;
	public Navigator(Missile missile, Vessel vessel) {
		this.missile = missile;
		target = vessel;
	}
	public abstract void tick(double time, double dt);
	public static double posRad(double rad) {
		rad %= 2*Math.PI;
		return rad>0? rad : rad+2*Math.PI;
	}
}
