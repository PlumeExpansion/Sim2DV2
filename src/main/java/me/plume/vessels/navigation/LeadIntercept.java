package me.plume.vessels.navigation;

import me.plume.components.Vessel;
import me.plume.vessels.Missile;

public class LeadIntercept extends Navigator {
	public LeadIntercept(Missile missile, Vessel vessel) {
		super(missile, vessel);
	}
	double discrim;
	double h, k, r;
	double dx, dy;
	double x1, y1, x2, y2, trueX, trueY;
	double dot1, dot2;
	double dist1, dist2;
	double theta, angle;
	boolean incpt;
	public void tick(double time, double dt) {
		incpt = false;
		h = target.x + target.vx;
		k = target.y + target.vy;
		r = missile.v;
		dx = target.x - missile.x;
		dy = target.y - missile.y;
		
		double slope = dy/dx;
		double yIncpt = missile.y - slope*missile.x;
		
		double a = slope*slope+1;
		double b = 2*(slope*yIncpt - slope*k - h);
		double c = yIncpt*yIncpt + k*k + h*h - r*r - 2*yIncpt*k;
		
		discrim = b*b - 4*a*c;
		if (discrim == 0 && missile.v != Math.sqrt(target.vx*target.vx + target.vy*target.vy)) {
			trueX = -b/(2*a);
			trueY = slope*trueX + yIncpt;
			incpt = true;
		}
		if (discrim>0) {
			x1 = (-b + Math.sqrt(discrim)) / (2*a);
			x2 = (-b - Math.sqrt(discrim)) / (2*a);
			y1 = slope*x1 + yIncpt;
			y2 = slope*x2 + yIncpt;
			
			dot1 = Vessel.dot(x1-target.x, y1-target.y, -dx, -dy);
			dot2 = Vessel.dot(x2-target.x, y2-target.y, -dx, -dy);
			
			if (dot1>0 || dot2>0) {
				if (dot1==0) {
					trueX = x2;
					trueY = y2;
				} else if (dot2 == 0) {
					trueX = x1;
					trueY = y1;
				} else if (dot1>0 && dot2<0) {
					trueX = x1;
					trueY = y1;
				} else if (dot1<0 && dot2>0) {
					trueX = x2;
					trueY = y2;
				} else {
					dist1 = target.dist(x1, y1);
					dist2 = target.dist(x2, y2);
					if (dist1 > dist2) {
						trueX = x1;
						trueY = y1;
					} else {
						trueX = x2;
						trueY = y2;
					}
				}
				incpt = true;
			}
		}
		theta = incpt? Math.atan2(k - trueY, h - trueX) : Math.atan2(dy, dx); 
		
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
