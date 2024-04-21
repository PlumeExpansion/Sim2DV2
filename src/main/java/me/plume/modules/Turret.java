package me.plume.modules;

import javafx.scene.canvas.GraphicsContext;
import me.plume.components.Vessel;
import me.plume.components.VesselModule;
import me.plume.drivers.WorldEngine;
import me.plume.vessels.navigation.Navigator;

public abstract class Turret extends VesselModule {
	public double maxAngleRate = 2*Math.PI;
	public double minAngle = 0, maxAngle = 2*Math.PI;
	public WorldEngine world;
	public Vessel vessel;
	protected double theta, angle, delay;
	protected boolean shoot;
	private double shootHold;
	private int shot;
	public Turret(Vessel vessel, WorldEngine world, double delay) {
		super(vessel);
		this.world = world;
		this.delay = delay;
	}
	public void update(double time, double dt) {
		if (shoot) {
			if (shootHold == 0) shootHold = time;
			if (shot <= (time-shootHold)/delay) {
				shot++;
				shoot();
			}
		}
	}
	protected abstract void shoot();
	public void shoot(boolean shoot) {
		if (shoot) {
			if (this.shoot) return;
			else {
				shot = 0;
				shootHold = 0;
			}
		}
		this.shoot = shoot;
	}
	public void angle(double input, double dt) {
		theta = input;
		theta = Navigator.posRad(theta)-2*Math.PI;
		while (theta < minAngle) theta+= 2*Math.PI;
		if (theta > maxAngle) {
			if (Math.abs(Navigator.posRad(minAngle)-Navigator.posRad(theta))>theta-maxAngle) theta=maxAngle;
			else theta = minAngle;
		}
		if (maxAngle-minAngle >= 2*Math.PI) {
			theta = Navigator.posRad(theta);
			angle = Navigator.posRad(angle);
			if (Math.abs(theta-angle) <= maxAngleRate*dt) angle=theta;
			if (angle<theta) {
				if (theta-angle>Math.PI) angle-=maxAngleRate*dt;
				else angle+=maxAngleRate*dt;
			}
			if (angle>theta) {
				if (angle-theta>Math.PI) angle+=maxAngleRate*dt;
				else angle-=maxAngleRate*dt;
			}
		} else {
			if (Math.abs(theta-angle) <= maxAngleRate*dt) angle=theta;
			if (angle>theta) angle -= maxAngleRate*dt;
			if (angle<theta) angle += maxAngleRate*dt;
		}
	}
	public abstract void render(GraphicsContext c, double x, double y, double s);
}
