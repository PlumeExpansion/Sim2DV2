package me.plume.modules;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import me.plume.components.Marker;
import me.plume.components.Module;
import me.plume.components.Vessel;
import me.plume.drivers.WorldEngine;
import me.plume.vessels.FusedShell;
import me.plume.vessels.ProxyShell;
import me.plume.vessels.navigation.Navigator;

public class CIWSTurret extends Module {
	Stop[] stops;
	public Color ammoColor = Color.DIMGRAY;
	public Color bridgeColor = Color.GRAY;
	public Color barrelColor = Color.LIGHTGRAY;
	public Color baseColor = Color.SLATEGRAY;
	public Color thrusterColor = Color.DARKSLATEGRAY;
	public Color exhaustColor = Color.LIGHTCYAN;
	public double barrelLength = 1.5;
	public double barrelWidth = 0.2;
	public double bridgeWidth = 0.5;
	public double bridgeLength = 1.2;
	public double ammoGap = 0.2;
	public double ammoLength = 1;
	public double ammoWidth = 0.5;
	public double thrusterWidth = 0.8;
	public double thrusterLength = 0.2;
	public double exhaustWidth = 0.3;
	public double exhaustLength = 1.5;
	public double exhaustTime;
	public double minScale = 10;
	public Color iconColor = Color.GRAY;
	public double iconR = 0.7;
	public double iconW = 0.3;
	public double iconL = 1;
	public double minScaleWidth = 2;
	public double minScaleR = 1.5;
	WorldEngine world;
	public double maxTraverse = Math.toRadians(115);
	public double minAngle = 0, maxAngle = 2*Math.PI;
	double targetRad;
	private double angle;
	double delay, velocity, life, dispersion;
	private boolean shoot;
	public boolean auto;
	public List<Vessel> targets;
	public CIWSTurret(Vessel vessel, WorldEngine world, double delay, double velocity, double life, double dispersion) {
		super(vessel);
		this.world = world;
		this.delay = delay;
		exhaustTime = delay/2;
		this.velocity = velocity;
		this.life = life;
		this.dispersion = dispersion;
		calcStops();
	}
	double shootHold;
	int shot;
	boolean thrusting;
	double rad, rvx, rvy, rx, ry;
	public void update(double time, double dt) {
		if (auto && targets != null) {
			for (Vessel v : targets) {
				rx = v.x - (vessel.x+ox);
				ry = v.y - (vessel.y+oy);
				rvx = v.vx - vessel.vx;
				rvy = v.vy - vessel.vy;
				//TODO finish autotrack
			}
		}
		if (shoot) {
			if (shootHold == 0) shootHold = time;
			if (shot <= (time-shootHold)/delay) {
				shot++;
				rad = angle+2*(Math.random()-0.5)*dispersion;
				FusedShell s = new ProxyShell(
						vessel.x+ox+(bridgeLength/2+barrelLength)*Math.cos(angle), 
						vessel.y+oy+(bridgeLength/2+barrelLength)*Math.sin(angle), 
						vessel.vx+velocity*Math.cos(rad), 
						vessel.vy+velocity*Math.sin(rad), time, life, world);
				world.exclusiveColliders.add(s);
			}
			thrusting = (time-shootHold)%delay < exhaustTime;
		}
	}
	public void shoot(boolean shoot) {
		if (shoot) {
			if (this.shoot) return;
			else {
				shot = 0;
				shootHold = 0;
			}
		} else thrusting = false;
		this.shoot = shoot;
	}
	public void angle(double angle) {
		angle = Navigator.posRad(angle);
		while (angle < minAngle) angle+= 2*Math.PI;
		if (angle > maxAngle) {
			if (minAngle-(angle-2*Math.PI)>angle-maxAngle) angle=maxAngle;
			else angle = minAngle;
		}
		this.angle = angle;
	}
	public void calcStops() {
		stops = new Stop[] {new Stop(0, exhaustColor), new Stop(1, Color.TRANSPARENT)};
	}
	public void render(GraphicsContext c, double x, double y, double s) {
		if (s<minScale) {
			s = minScale;
			c.setStroke(Color.YELLOW);
			c.setLineWidth(minScaleWidth);
			c.strokeOval(x-minScaleR*s, y-minScaleR*s, minScaleR*2*s, minScaleR*2*s);
			c.setFill(iconColor);
			c.fillOval(x-iconR*s, y-iconR*s, 2*iconR*s, 2*iconR*s);
			rect(c, 0, iconW/2, iconL, iconW, x, y, s);
			return;
		}
		c.setFill(barrelColor);
		rect(c, bridgeLength/2, barrelWidth/2, barrelLength, barrelWidth, x, y, s);
		c.setFill(bridgeColor);
		rect(c, -bridgeLength/2, bridgeWidth/2, bridgeLength, bridgeWidth, x, y, s);
		c.setFill(ammoColor);
		rect(c, -ammoLength/2, ammoWidth+ammoGap/2, ammoLength, ammoWidth, x, y, s);
		rect(c, -ammoLength/2, -ammoGap/2, ammoLength, ammoWidth, x, y, s);
		c.setFill(thrusterColor);
		rect(c, -bridgeLength/2-thrusterLength, thrusterWidth/2, thrusterLength, thrusterWidth, x, y, s);
		if (thrusting) {
			c.setFill(new LinearGradient(x(-bridgeLength/2,0)*s+x, y(-bridgeLength/2,0)*s+y, 
					x(-bridgeLength/2-thrusterLength-exhaustLength,0)*s+x, 
					y(-bridgeLength/2-thrusterLength-exhaustLength,0)*s+y, false, CycleMethod.NO_CYCLE, stops));
			rect(c, -bridgeLength/2-thrusterLength-exhaustLength, exhaustWidth/2, exhaustLength, exhaustWidth, x, y, s);
		}
	}
	private void rect(GraphicsContext c, double u, double v, double w, double l, double x, double y, double s) {
		c.fillPolygon(new double[] {x(u,v)*s+x,x(u+w,v)*s+x,x(u+w,v-l)*s+x,x(u,v-l)*s+x}, 
				new double[] {y(u,v)*s+y,y(u+w,v)*s+y,y(u+w,v-l)*s+y,y(u,v-l)*s+y}, 4);
	}
	private double x(double x, double y) {
		return Marker.x(x, y, angle);
	}
	private double y(double x, double y) {
		return Marker.y(x, y, angle);
	}
}
