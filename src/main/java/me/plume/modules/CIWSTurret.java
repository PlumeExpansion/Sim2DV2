package me.plume.modules;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import me.plume.components.Marker;
import me.plume.components.VesselModule;
import me.plume.components.Vessel;
import me.plume.drivers.WorldEngine;
import me.plume.vessels.FusedShell;
import me.plume.vessels.ProxyShell;
import me.plume.vessels.navigation.Navigator;

public class CIWSTurret extends VesselModule {
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
	public double maxAngleRate = Math.toRadians(115);
	public double minAngle = 0, maxAngle = 2*Math.PI;
	public double minShootingAngleDiff = Math.toRadians(15);
	double targetRad;
	private double theta, angle;
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
	double xb, yb, xm, ym;
	double rad, vrx, vry, vr;
	double m, b, dx, dti, dxit, dyit, dot;
	double dxa1t, dya1t, dxa2t, dya2t, dta;
	double xa1, ya1, xa2, ya2, d1, d2, xa, ya, xi, yi;
	double A, B, C, k, Ad, Bd, Cd, Aa, Ba, Ca, discrim;
	double incptScore, score;
	public void update(double time, double dt) {
		if (auto && targets != null) {
			incptScore = -1;
			xb = vessel.x + ox;
			yb = vessel.y + oy;
			for (Vessel t : targets) {
				if (t.getId() == vessel.getId()) continue;
				xm = t.x;
				ym = t.y;
				vrx = t.vx - vessel.vx;
				vry = t.vy - vessel.vy;
				vr = Math.sqrt(vrx*vrx + vry*vry);
				
				m=vry/vrx;
				b = ym - m*xm;
				
				A = vry;
				B = -vrx;
				C = vrx*ym - vry*xm;
				Ad = -vrx;
				Bd = -vry;
				Cd = vry*yb+vrx*xb;
				k = 1/(A*Bd - B*Ad);
				
				dx = Math.abs(A*xb+B*yb+C)/Math.sqrt(A*A+B*B);
				xi = k*(B*Cd - C*Bd);
				yi = k*(C*Ad - A*Cd);
				dxit = xi-xm;
				dyit = yi-ym;
				dti = Math.sqrt(dxit*dxit + dyit*dyit)/vr;
				dot = dxit*vrx + dyit*vry;
				
				score = (dx+dti)*Math.signum(dot);
				if (score >= 0 && (score<incptScore || incptScore < 0)) {
					Aa = (velocity*velocity - vr*vr)*(1+m*m);
					Ba = 2*(velocity*velocity*(m*b-xm-m*ym) - vr*vr*(m*b-xb-m*yb));
					Ca = velocity*velocity*(xm*xm + ym*ym + b*b - 2*b*ym) - vr*vr*(xb*xb + yb*yb + b*b - 2*b*yb);
					
					discrim = Ba*Ba - 4*Aa*Ca;
					if (discrim < 0) continue;
					if (discrim == 0) {
						xa = -Ba/(2*Aa);
						ya = m*xa+b;
					} else {
						xa1 = (-Ba+Math.sqrt(discrim))/(2*Aa);
						xa2 = (-Ba-Math.sqrt(discrim))/(2*Aa);
						ya1 = m*xa1+b;
						ya2 = m*xa2+b;
						dxa1t = xa1 - xm;
						dxa2t = xa2 - xm;
						dya1t = ya1 - ym;
						dya2t = ya2 - ym;
						d1 = Math.sqrt(dxa1t*dxa1t + dya1t*dya1t);
						d2 = Math.sqrt(dxa2t*dxa2t + dya2t*dya2t);
						if (d1<d2) {
							xa = xa1;
							ya = ya1;
							dta = d1/vr;
						} else {
							xa = xa2;
							ya = ya2;
							dta = d2/vr;
						}
					}
					incptScore = score;
				}
			}
			if (incptScore > 0) {
				angle(Math.atan2(ya-yb, xa-xb), dt);
				shoot(dta<=life && Math.abs(theta-angle)<=minShootingAngleDiff);
			} else shoot = false;
		}
		if (shoot) {
			if (shootHold == 0) shootHold = time;
			if (shot <= (time-shootHold)/delay) {
				shot++;
				rad = angle+2*(Math.random()-0.5)*dispersion;
				FusedShell s = new ProxyShell(vessel.x+ox, vessel.y+oy, 
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
