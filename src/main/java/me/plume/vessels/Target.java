package me.plume.vessels;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import me.plume.components.Marker;
import me.plume.components.Vessel;
import me.plume.components.markers.Dot;
import me.plume.components.markers.Heading;

public class Target extends Vessel {
	public static final double ACCEL = 70;
	static final double MIN_SCALE = 2;
	static final double MIN_SCALE_WIDTH = 2;
	public static final double BARREL_LENGTH = 4;
	Color color;
	public Color status;
	public double angle;
	public boolean left, right, up, down;
	public Target(double x, double y, double r, Color c) {
		super(x, y);
		this.r = r;
		this.color = c;
		immune = true;
	}
	public void update(double time, double dt) {
		if (left) vx-=ACCEL*dt;
		if (right) vx+=ACCEL*dt;
		if (up) vy+=ACCEL*dt;
		if (down) vy-=ACCEL*dt;
	}
	public void onRemove(double time, double dt) {}
	public Marker mark() {
		return new TargetMarker(getId(), x, y, this);
	}
}
class TargetMarker extends Dot {
	static final Stop[] stops = {new Stop(0, Color.WHITE), new Stop(1, Color.TRANSPARENT)};
	static final LinearGradient left = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
	static final LinearGradient right = new LinearGradient(1, 0, 0, 0, true, CycleMethod.NO_CYCLE, stops);
	static final LinearGradient up = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
	static final LinearGradient down = new LinearGradient(0, 1, 0, 0, true, CycleMethod.NO_CYCLE, stops);
	Target t;
	double w, l;
	double minScaleR, statusR;
	Heading heading;
	public TargetMarker(int id, double x, double y, Target target) {
		super(id, x, y, target.r, target.color);
		t = target;
		heading = new Heading(id, x, y, target.r, target.color);
		heading.length = Target.BARREL_LENGTH;
		heading.angle = t.angle;
		heading.scale = true;
		scale = true;
		w = r/2;
		l = w*7+r*0.75;
		minScaleR = r*1.5;
		statusR = r*0.75;
	}
	public void render(GraphicsContext c, double x, double y, double s) {
		if (s<Target.MIN_SCALE) {
			s = Target.MIN_SCALE;
			c.setStroke(Color.YELLOW);
			c.setLineWidth(Target.MIN_SCALE_WIDTH);
			c.strokeOval(x-minScaleR*s, y-minScaleR*s, minScaleR*2*s, minScaleR*2*s);
		}
		if (t.left) {
			c.setFill(left);
			c.fillRect(x, -w/2*s+y, l*s, w*s);
		}
		if (t.right) {
			c.setFill(right);
			c.fillRect(-l*s+x, -w/2*s+y, l*s, w*s);
		}
		if (t.up) {
			c.setFill(up);
			c.fillRect(-w/2*s+x, y, w*s, l*s);
		}
		if (t.down) {
			c.setFill(down);
			c.fillRect(-w/2*s+x, -l*s+y, w*s, l*s);
		}
		super.render(c, x, y, s);
		heading.render(c, x, y, s);
		if (t.status != null) {
			c.setFill(t.status);
			c.fillOval(x-statusR*s, y-statusR*s, statusR*2*s, statusR*2*s);
		}
	}
}
