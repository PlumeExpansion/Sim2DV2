package me.plume.vessels.targets;

import java.util.List;

import javafx.scene.paint.Color;
import me.plume.components.Vessel;

public abstract class TurretedRCSTarget extends RCSTarget {
	public TurretedRCSTarget(double x, double y, double r, Color c) {
		super(x, y, r, c);
	}
	public abstract void setTargets(List<Vessel> targets);
	public abstract void setAuto(boolean auto);
	public abstract boolean auto();
	public abstract void setAim(double x, double y, double dt);
	public abstract void setShoot(boolean shoot);
}