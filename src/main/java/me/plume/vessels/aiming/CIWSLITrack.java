package me.plume.vessels.aiming;

import java.util.List;

import me.plume.components.Vessel;
import me.plume.modules.CIWSTurret;

public class CIWSLITrack {
	double incptScore;
	double dta;
	double xa, ya;
	int deligN;
	private int trackId;
	double firstShot;
	double lastShot;
	List<CIWSTurret> availables;
	public CIWSLITrack(int trackId) {
		this.trackId = trackId;
	}
	public static CIWSLITrack calcTrack(CIWSLITrack track, Vessel v) {
		
		return track;
	}
	public int getId() {return trackId;}
}
