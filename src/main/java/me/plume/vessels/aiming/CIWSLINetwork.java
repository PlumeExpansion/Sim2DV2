package me.plume.vessels.aiming;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.plume.components.Vessel;
import me.plume.modules.CIWSTurret;

public class CIWSLINetwork {
	Vessel v;
	List<CIWSTurret> turrets;
	Map<Integer, CIWSLITrack> tracks = new LinkedHashMap<>();
	public CIWSLINetwork(Vessel v, List<CIWSTurret> turrets) {
		this.v = v;
		this.turrets = turrets;
	}
	public void updateTracks(List<Vessel> targets, double time, double dt) {
		tracks = targets.stream().map(t -> {
			if (tracks.containsKey(t.getId())) return tracks.get(t.getId()).calcTrack(v, t, time, dt);
			else return new CIWSLITrack(t.getId()).calcTrack(v, t, time, dt);
		}).collect(Collectors.toMap(track -> track.getId(), track -> track));
		
	}
}
