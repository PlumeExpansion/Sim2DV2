package me.plume.drivers.scenarios;

import javafx.scene.paint.Color;
import me.plume.components.Scenario;
import me.plume.components.markers.Dot;
import me.plume.components.markers.Heading;
import me.plume.drivers.Launcher;

public class TestDebug extends Scenario {
	public TestDebug(Launcher instance) {
		super(instance);
		launcher.runWorld = false;
	}
	public void init() {
		Dot d = new Dot(-1, 0, 0, 2, Color.RED);
		d.scale = true;
		Heading h = new Heading(-1, 5, 5, 3, Color.CYAN);
		h.length = 5;
		h.scale = true;
		h.angle = Math.PI/6;
		world.markers.add(d);
		world.markers.add(h);
	}
	public void tick(double time, double dt) {}
}
