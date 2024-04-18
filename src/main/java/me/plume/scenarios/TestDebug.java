package me.plume.scenarios;

import javafx.scene.paint.Color;
import me.plume.components.Scenario;
import me.plume.engines.Launcher;
import me.plume.markers.Dot;
import me.plume.markers.Heading;

public class TestDebug extends Scenario {
	public TestDebug(Launcher instance) {
		super(instance);
		launcher.runWorld = false;
		launcher.runView = false;
	}
	public void init() {
		Dot d = new Dot(-1, 0, 0, 25, Color.RED);
		d.scale = true;
		Heading h = new Heading(-1, 100, 100, 40, Color.CYAN);
		h.length = 30;
		h.scale = true;
		h.angle = Math.PI/6;
		world.markers.add(d);
		world.markers.add(h);
	}
	public void tick(long last, long now, double dt) {}
}
