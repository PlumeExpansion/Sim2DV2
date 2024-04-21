package me.plume.vessels.aiming;

import me.plume.components.Vessel;
import me.plume.modules.CIWSTurret;

public class CIWSLeadIntercept extends Aiming {
	double xb, yb, xm, ym;
	double rad, vrx, vry, vr, vb;
	double m, b, dx, dti, dxit, dyit, dot;
	double dxa1t, dya1t, dxa2t, dya2t, dta;
	double xa1, ya1, xa2, ya2, d1, d2, xa, ya, xi, yi;
	double A, B, C, k, Ad, Bd, Cd, Aa, Ba, Ca, discrim;
	double incptScore, score;
	CIWSTurret turret;
	public CIWSLeadIntercept(CIWSTurret turret) {
		super(turret);
		this.turret = turret;
	}
	public void tick(double time, double dt) {
		if (targets == null) {
			turret.shoot(false);
			return;
		}
		incptScore = -1;
		xb = turret.vessel.x + turret.ox;
		yb = turret.vessel.y + turret.oy;
		for (Vessel t : targets) {
			if (t.getId() == turret.vessel.getId()) continue;
			xm = t.x;
			ym = t.y;
			vrx = t.vx - turret.vessel.vx;
			vry = t.vy - turret.vessel.vy;
			vr = Math.sqrt(vrx*vrx + vry*vry);
			vb = turret.velocity;
			
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
				Aa = (vb*vb - vr*vr)*(1+m*m);
				Ba = 2*(vb*vb*(m*b-xm-m*ym) - vr*vr*(m*b-xb-m*yb));
				Ca = vb*vb*(xm*xm + ym*ym + b*b - 2*b*ym) - vr*vr*(xb*xb + yb*yb + b*b - 2*b*yb);
				
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
			turret.angle(Math.atan2(ya-yb, xa-xb), dt);
			turret.shoot(dta<=turret.life && Math.abs(turret.getTheta()-turret.getAngle())<=turret.minShootingAngleDiff);
		} else turret.shoot(false);
	}
}
