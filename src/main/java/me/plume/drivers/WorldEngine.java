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
	static final double DELAY = 0.01;
	static final int TRIAL_N = (int) (ViewEngine.FRAME_DEL/DELAY);
	static final double TRIAL_R = ViewEngine.FRAME_DEL%DELAY;
	Launcher launcher;
	public List<Vessel> vessels = Collections.synchronizedList(new LinkedList<>());
	public List<Vessel> exclusiveColliders = Collections.synchronizedList(new LinkedList<>());
	public List<Vessel> effects = Collections.synchronizedList(new LinkedList<>());
	public List<Marker> markers = Collections.synchronizedList(new LinkedList<>());
	Thread thread;
	boolean terminated;
	public WorldEngine(Launcher instance) {
		launcher = instance;
	}
	long now, lastFrame;
	double dtF, fpsSum;
	int fpsN, fps;
	double time;
	boolean waiting;
	public void start() {
		thread = new Thread(() -> {
			while (!terminated) {
				if (!waiting) {
					for (int i = 0; i < TRIAL_N; i++) update(DELAY);
					update(TRIAL_R);
					waiting=true;
				}
				now = System.currentTimeMillis();
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
						launcher.window.setTitle(Launcher.TITLE+" - "+launcher.view.scale+" - "+fps+" - "+launcher.view.trackId);
					});
				}
				markers = markers.stream().filter(m -> m.getId()<0).collect(Collectors.toList());
				markers.addAll(exclusiveColliders.stream().map(v -> v.mark()).collect(Collectors.toList()));
				markers.addAll(vessels.stream().map(v -> v.mark()).collect(Collectors.toList()));
				markers.addAll(effects.stream().map(v -> v.mark()).collect(Collectors.toList()));
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
		launcher.scenario.tick(time, dt);
	}
	public void stop() {
		terminated = true;
	}
}
