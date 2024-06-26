package me.plume.vessels.aiming;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import me.plume.components.Vessel;
import me.plume.modules.CIWSTurret;
import me.plume.vessels.navigation.Navigator;

public class CIWSLINetwork {
	Vessel v;
	List<CIWSTurret> turrets;
	private Map<Integer, CIWSLITrack> tracks = new LinkedHashMap<>();
	private List<CIWSTurret> occupied = new LinkedList<>();
	public CIWSLINetwork(Vessel v, List<CIWSTurret> turrets) {
		this.v = v;
		this.turrets = turrets;
	}
	public void updateTracks(List<Vessel> targets, double time, double dt) {
		// upating tracks
		tracks = targets.stream().map(t -> {
			if (tracks.containsKey(t.getId())) return tracks.get(t.getId()).calcTrack(v, t, time, dt);
			else return new CIWSLITrack(t.getId()).calcTrack(v, t, time, dt);
		}).filter(track -> track.incptScore>0).collect(Collectors.toMap(CIWSLITrack::getId, Function.identity()));
		// checking turret availability
		tracks.values().forEach(track -> {
			track.availables.clear();
			turrets.forEach(t -> {
				if (!t.auto || !t.networked) return;
				Double theta = track.calcAngle(t);
				if (theta == null) return;
				double angleFromBounds = t.angleFromBounds(theta);
				if (angleFromBounds>0) track.availables.put(t, Navigator.angleDiff(theta, t.getAngle()));
				else track.backups.put(t, angleFromBounds);
			});
		});
		occupied.clear();
		// assining available turrets
		tracks.values().stream().filter(track -> track.deligN!=0).sorted((a, b) -> a.incptScore<b.incptScore? -1 : 1).forEach(track -> {
			assign(track, track.availables, track.deligN, time, dt, false);
		});
		// aiming free turrets
		if (occupied.size() < turrets.size()) {
			tracks.values().stream().filter(track -> track.deligN==0).sorted((a, b) -> {
				boolean aShooting = a.lastShot-a.firstShot<CIWSLITrack.SHOOT_TIME;
				boolean bShooting = b.lastShot-b.firstShot<CIWSLITrack.SHOOT_TIME;
				if (aShooting && !bShooting) return -1;
				if (bShooting && !aShooting) return 1;
				if (aShooting && bShooting) return a.lastShot-a.firstShot>b.lastShot-b.firstShot? -1 : 1;
				return a.dta-(time-a.lastShot) < b.dta-(time-b.lastShot)? -1 : 1;
			}).forEach(track -> {
				assign(track, track.availables, 1, time, dt, true);
			});
		}
		// aiming unavailable turrets
		if (occupied.size() < turrets.size()) {
			turrets.stream().filter(t -> t.auto && t.networked).forEach(t -> {
				if (!occupied.contains(t)) t.shoot(false);
			});
		}
	}
	private void assign(CIWSLITrack track, Map<CIWSTurret, Double> availables, int n, double time, double dt, boolean forceAim) {
		List<Entry<CIWSTurret, Double>> remaining = availables.entrySet().stream()
				.filter(e -> !occupied.contains(e.getKey()))
				.sorted((a, b) -> a.getValue()<b.getValue()? -1 : 1)
				.collect(Collectors.toList());
		for(int i = 0; i < Math.min(remaining.size(), n); i++) {
			Entry<CIWSTurret, Double> e = remaining.get(i);
			CIWSTurret t = e.getKey();
			if (track.dta > t.life) continue;
			occupied.add(t);
			t.angle(track.calcAngle(t), dt);
			if (e.getValue() <= CIWSLITrack.RAD_ON_TARGET) {
				if (track.firstShot == 0 || track.lastShot-track.firstShot>CIWSLITrack.SHOOT_TIME) track.firstShot = time;
				t.shoot(true);
				track.lastShot = time;
			} else t.shoot(false);
		}
	}
}
