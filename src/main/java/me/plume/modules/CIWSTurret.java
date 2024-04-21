package me.plume.modules;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.drivers.WorldEngine;
import me.plume.vessels.FusedShell;
import me.plume.vessels.ProxyShell;
import me.plume.vessels.aiming.Aiming;
import me.plume.vessels.aiming.CIWSLeadIntercept;

public class CIWSTurret extends Turret {
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
	public double minShootingAngleDiff = Math.toRadians(15);
	double rad;
	public double velocity, life, dispersion;
	public boolean auto;
	public Aiming aiming;
	public CIWSTurret(Vessel vessel, WorldEngine world, double delay, double velocity, double life, double dispersion) {
		super(vessel, world, delay);
		exhaustTime = delay/2;
		this.velocity = velocity;
		this.life = life;
		this.dispersion = dispersion;
		calcStops();
		aiming = new CIWSLeadIntercept(this);
	}
	boolean thrusting;
	public void update(double time, double dt) {
		if (auto) aiming.tick(time, dt);
		super.update(time, dt);
		if (shoot) thrusting = (time-shootHold)%delay < exhaustTime;
	}
	protected void shoot(double time, double dt) {
		rad = getAngle()+2*(Math.random()-0.5)*dispersion;
		FusedShell s = new ProxyShell(vessel.x+ox, vessel.y+oy, 
				vessel.vx+velocity*Math.cos(rad), 
				vessel.vy+velocity*Math.sin(rad), time, life, world);
		world.exclusiveColliders.add(s);
	}
	public void shoot(boolean shoot) {
		super.shoot(shoot);
		if (!shoot) thrusting = false;
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
		return Marker.x(x, y, getAngle());
	}
	private double y(double x, double y) {
		return Marker.y(x, y, getAngle());
	}
}
