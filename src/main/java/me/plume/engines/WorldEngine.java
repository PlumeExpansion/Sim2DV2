package me.plume.engines;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import me.plume.components.Marker;
import me.plume.components.Vessel;

public class WorldEngine {
	static final double DELAY = 0.001;
	Launcher launcher;
	public List<Vessel> vessels = new LinkedList<>();
	public List<Marker> markers = new LinkedList<>();
	boolean requested;
	Thread thread;
	boolean terminated;
	public WorldEngine(Launcher instance) {
		launcher = instance;
	}
	long last, now;
	double dt;
	public void start() {
		thread = new Thread(() -> {
			while (!terminated) {
				now = System.currentTimeMillis();
				dt = (double) (now-last)/1000;
				if (dt < DELAY) continue;
				last = now;
				Iterator<Vessel> itrt = vessels.iterator();
				while (itrt.hasNext()) {
					Vessel v = itrt.next();
					v.update(last, now, dt);
					v.x += v.vx*dt;
					v.y += v.vy*dt;
					if (v.remove) {
						Vessel.removeVessel(v.getId());
						itrt.remove();
					}
				}
				launcher.scenario.tick(last, now, dt);
				if (requested) {
					markers = markers.stream().filter(m -> m.getId()<0).collect(Collectors.toList());
					markers.addAll(vessels.stream().map(v -> v.mark()).collect(Collectors.toList()));
					requested = false;
					launcher.view.render();
				}
			}
		});
		thread.start();
	}
	public void stop() {
		terminated = true;
	}
	public void requestMarkers() {
		requested = true;
	}
}
