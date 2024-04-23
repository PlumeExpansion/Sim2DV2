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
		tracks.forEach((id, track) -> {
			if (time-track.lastShot>=track.dta) track.firstShot = 0;
		});
		// checking turret availability
		tracks.values().forEach(track -> {
			track.availables.clear();
			turrets.forEach(t -> {
				if (!t.auto) return;
				double theta = track.calcAngle(t);
				double angleFromBounds = t.angleFromBounds(theta);
				if (angleFromBounds>0) track.availables.put(t, Navigator.angleDiff(theta, t.getAngle()));
				else track.backups.put(t, angleFromBounds);
			});
		});
		occupied.clear();
		//TODO less dangerous missiles with deligN=2 seem to be hogging turrets
		// assining available turrets
		tracks.values().stream().sorted((a, b) -> a.incptScore<b.incptScore? -1 : 1).forEach(track -> {
			if (track.deligN == 0) return;
			assign(track, track.availables, track.deligN, time, dt);
		});
		// aiming free turrets
		if (occupied.size() < turrets.size()) {
			tracks.values().stream().sorted((a, b) -> a.dta-(time-a.lastShot) < b.dta-(time-b.lastShot)? -1 : 1).forEach(track -> {
				assign(track, track.availables, 1, time, dt);
			});
		}
		// aiming unavailable turrets
		if (occupied.size() < turrets.size()) {
			turrets.forEach(t -> {
				if (!occupied.contains(t)) t.shoot(false);
			});
		}
	}
	private void assign(CIWSLITrack track, Map<CIWSTurret, Double> availables, int n, double time, double dt) {
		List<Entry<CIWSTurret, Double>> remaining = availables.entrySet().stream()
				.filter(e -> !occupied.contains(e.getKey()))
				.sorted((a, b) -> a.getValue()<b.getValue()? -1 : 1)
				.collect(Collectors.toList());
		for(int i = 0; i < Math.min(remaining.size(), n); i++) {
			Entry<CIWSTurret, Double> e = remaining.get(i);
			CIWSTurret t = e.getKey();
			occupied.add(t);
			t.angle(track.calcAngle(t), dt);
			if (e.getValue() <= CIWSLITrack.RAD_ON_TARGET && track.dta <= t.life) {
				track.lastShot = time;
				if (track.firstShot == 0) track.firstShot = time;
				t.shoot(true);
			} else t.shoot(false);
		}
	}
}
