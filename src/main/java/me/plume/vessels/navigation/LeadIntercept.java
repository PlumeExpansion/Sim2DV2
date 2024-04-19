package me.plume.vessels.navigation;

import me.plume.components.Vessel;
import me.plume.vessels.Missile;

public class LeadIntercept extends Navigator {
	static final double RADIAN_BUFFER = Math.toRadians(1);
	public LeadIntercept(Missile missile, Vessel vessel) {
		super(missile, vessel);
	}
	double discrim;
	double h, k, r;
	double x1, y1, x2, y2, trueX, trueY;
	double theta, angle, testRad;
	public void tick(double time, double dt) {
		theta = Math.atan2(target.y-missile.y, target.x-missile.x);
		h = target.x + target.vx;
		k = target.y + target.vy;
		r = missile.v;
		
		double slope = (missile.y-target.y)/(missile.x-target.x);
		double yIncpt = missile.y - slope*missile.x;
		
		double a = slope*slope+1;
		double b = 2*(slope*yIncpt - slope*k - h);
		double c = yIncpt*yIncpt + k*k + h*h - r*r - 2*yIncpt*k;
		
		discrim = b*b - 4*a*c;
		if (discrim>0) {
			x1 = (-b + Math.sqrt(discrim)) / (2*a);
			x2 = (-b - Math.sqrt(discrim)) / (2*a);
			y1 = slope*x1 + yIncpt;
			y2 = slope*x2 + yIncpt;
			testRad = Math.atan2(y1 - target.y, x1 - target.x);
			if (Math.abs(testRad - Math.atan2(missile.y-target.y, missile.x-target.x)) < RADIAN_BUFFER) {
				trueX = x1;
				trueY = y1;
			} else {
				trueX = x2;
				trueY = y2;
			}
			theta = Math.atan2(k - trueY, h - trueX);
			System.out.println("DISCRIM" + "\t" + Missile.posRad(theta) + "\t" + missile.v);
		} else System.out.println("LOS");
		
		theta = Missile.posRad(theta);
		angle = Missile.posRad(missile.angle);
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
