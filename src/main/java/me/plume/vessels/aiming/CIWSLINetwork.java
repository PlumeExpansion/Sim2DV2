package me.plume.vessels.aiming;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.plume.components.Vessel;
import me.plume.modules.CIWSTurret;

public class CIWSLINetwork {
	List<CIWSTurret> turrets;
	Map<Integer, CIWSLITrack> tracks = new LinkedHashMap<>();
	public CIWSLINetwork(List<CIWSTurret> turrets) {
		this.turrets = turrets;
	}
	public void updateTracks(List<Vessel> targets) {
		tracks = targets.stream().map(t -> {
			if (tracks.containsKey(t.getId())) return CIWSLITrack.calcTrack(tracks.get(t.getId()), t);
			else return CIWSLITrack.calcTrack(new CIWSLITrack(t.getId()), t);
		}).collect(Collectors.toMap(track -> track.getId(), track -> track));
	}
}
