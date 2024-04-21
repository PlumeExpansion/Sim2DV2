package me.plume.vessels.aiming;

import java.util.List;

import me.plume.components.Vessel;
import me.plume.modules.Turret;

public abstract class Aiming {
	Turret turret;
	public List<Vessel> targets;
	public Aiming(Turret turret) {
		this.turret = turret;
	}
	public abstract void tick(double time, double dt);
}
