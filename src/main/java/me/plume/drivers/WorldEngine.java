package me.plume.drivers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import me.plume.components.Marker;
import me.plume.components.Vessel;

public class WorldEngine {
	static final double MAX_STEP_DIST = 1;
	Launcher launcher;
	public List<Vessel> vessels = Collections.synchronizedList(new LinkedList<>());
	public List<Vessel> exclusiveColliders = Collections.synchronizedList(new LinkedList<>());
	public List<Vessel> effects = Collections.synchronizedList(new LinkedList<>());
	public List<Marker> markers = Collections.synchronizedList(new LinkedList<>());
	public Vessel track;
	Thread thread;
	boolean terminated;
	public WorldEngine(Launcher instance) {
		launcher = instance;
	}
	long now, lastFrame, lastUpdate;
	double dtF, fpsSum, buffer;
	int fpsN, fps, updateCount;
	double time;
	double delay, remainder, maxV, vel;
	boolean waiting;
	public void start() {
		thread = new Thread(() -> {
			while (!terminated) {
				now = System.currentTimeMillis();
				if (!waiting) {
					maxV=0;
					Arrays.asList(vessels, exclusiveColliders).forEach(list -> list.forEach(v -> {
						vel = Math.sqrt(v.vx*v.vx + v.vy*v.vy);
						if (vel > maxV) maxV = vel;
					}));
					delay = ViewEngine.FRAME_DEL;
					if (maxV!=0 && MAX_STEP_DIST / maxV < ViewEngine.FRAME_DEL) {
						delay = MAX_STEP_DIST / maxV;
						updateCount = (int) (ViewEngine.FRAME_DEL / delay);
						remainder = ViewEngine.FRAME_DEL % delay;
						for (int i = 0; i < updateCount; i++) update(delay);
						update(remainder);
					} else {
						updateCount = 1;
						update(ViewEngine.FRAME_DEL);
					}
					buffer = (now-lastUpdate)/1000.0;
					lastUpdate = System.currentTimeMillis();
					waiting=true;
				}
				dtF = (double) (now-lastFrame)/1000;
				if (dtF < ViewEngine.FRAME_DEL) continue;
				waiting=false;
				lastFrame = now;
				fpsSum+=1.0/dtF;
				fpsN++;
				if (fpsN >= ViewEngine.FPS_N) {
					fps = (int)fpsSum/fpsN;
					fpsSum=0;
					fpsN=0;
					Platform.runLater(() -> {
						launcher.window.setTitle(Launcher.TITLE
						+" - scale: "+(int) (launcher.view.scale*100)/100.0
						+" - fps: "+fps
						+" - updateCount: "+updateCount
						+" - buffer: "+buffer
						+" - trackId: "+(track!=null? track.getId() : "null"));
					});
				}
				markers = markers.stream().filter(m -> m.getId()<0).collect(Collectors.toList());
				markers.addAll(vessels.stream().map(v -> v.mark()).collect(Collectors.toList()));
				markers.addAll(exclusiveColliders.stream().map(v -> v.mark()).collect(Collectors.toList()));
				markers.addAll(effects.stream().map(v -> v.mark()).collect(Collectors.toList()));
				launcher.view.track(track);
				launcher.view.render();
			}
		});
		thread.start();
	}
	private void update(double dt) {
		time+=dt;
		List<List<Vessel>> lists = Arrays.asList(vessels, exclusiveColliders, effects);
		for (List<Vessel> list : lists) {
			Iterator<Vessel> itrt = list.iterator();
			while (itrt.hasNext()) {
				Vessel v = itrt.next();
				v.update(time, dt);
				v.x += v.vx*dt;
				v.y += v.vy*dt;
				if (v.remove) {
					v.onRemove(time, dt);
					Vessel.removeVessel(v.getId());
					itrt.remove();
				}
			}
		}
		launcher.view.realTimeTrack(track);
		launcher.scenario.tick(time, dt);
	}
	public void stop() {
		terminated = true;
	}
}
