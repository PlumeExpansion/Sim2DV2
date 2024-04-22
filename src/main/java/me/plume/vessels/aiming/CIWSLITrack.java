package me.plume.vessels.aiming;

import java.util.LinkedHashMap;
import java.util.Map;

import me.plume.components.Vessel;
import me.plume.modules.CIWSTurret;

public class CIWSLITrack {
	static final double MIN_DTI = 2;
	static final double SHOOT_TIME = 0.25;
	static final double RAD_ON_TARGET = Math.toRadians(1);
	double incptScore;
	double dta;
	double xa, ya;
	int deligN;
	private int trackId;
	double firstShot;
	double lastShot;
	Map<CIWSTurret, Double> availables = new LinkedHashMap<>();
	Map<CIWSTurret, Double> backups = new LinkedHashMap<>();
	public CIWSLITrack(int trackId) {
		this.trackId = trackId;
	}
	double xb, yb, xm, ym;
	double vrx, vry, vr;
	double dx, dti, dxit, dyit, dot;
	double xi, yi;
	double A, B, C, k, Ad, Bd, Cd;
	public CIWSLITrack calcTrack(Vessel v, Vessel t, double time, double dt) {
		xb = v.x;
		yb = v.y;
		xm = t.x;
		ym = t.y;
		vrx = t.vx - v.vx;
		vry = t.vy - v.vy;
		vr = Math.sqrt(vrx*vrx + vry*vry);
		
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
		
		incptScore = (dx+dti)*Math.signum(dot);
		
		if (dti < MIN_DTI) deligN = 2;
		else if (lastShot - firstShot > SHOOT_TIME && time-lastShot < dta) deligN = 0;
		else deligN = 1;
		return this;
	}
	public Double calcAngle(CIWSTurret turret) {
		double vb, m, b;
		double Aa, Ba, Ca;
		double discrim;
		double xa1, xa2, ya1, ya2, dxa1t, dxa2t, dya1t, dya2t;
		double d1, d2;
		
		vb = turret.velocity;
		
		m=vry/vrx;
		b = ym - m*xm;
		
		Aa = (vb*vb - vr*vr)*(1+m*m);
		Ba = 2*(vb*vb*(m*b-xm-m*ym) - vr*vr*(m*b-xb-m*yb));
		Ca = vb*vb*(xm*xm + ym*ym + b*b - 2*b*ym) - vr*vr*(xb*xb + yb*yb + b*b - 2*b*yb);
		
		discrim = Ba*Ba - 4*Aa*Ca;
		if (discrim < 0) return null;
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
		return Math.atan2(ya-yb, xa-xb);
	}
	public int getId() {return trackId;}
}
