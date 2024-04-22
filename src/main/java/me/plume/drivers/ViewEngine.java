package me.plume.drivers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import me.plume.components.Vessel;

public class ViewEngine {
	static final int FPS_N = 10;
	static final double SCALE_FACTOR = 1.1;
	static final double FRAME_DEL = 1.0/60;
	static final double DEFAULT_SCALE = 10;
	Launcher launcher;
	Scene scene;
	GraphicsContext c;
	AnimationTimer timer;
	private double offsetX, offsetY;
	double ox, oy, ix, iy, os;
	public double scale = DEFAULT_SCALE;
	boolean mcs;
	boolean tracking;
	private double realTimeTrackX, realTimeTrackY;
	private double trackX, trackY;
	private double camX, camY;
	public double trackX() {return trackX;}
	public double trackY() {return trackY;}
	public double camX() {return camX;}
	public double camY() {return camY;}
	public double offsetX() {return offsetX;}
	public double offsetY() {return offsetY;}
	public double realTimeOffsetX() {return realTimeTrackX + camX;}
	public double realTimeOffsetY() {return realTimeTrackY + camY;}
	public ViewEngine(Launcher instance) {
		launcher = instance;
		scene = launcher.scene;
		c = launcher.c;
		center();
		render();
	}
	public void addSceneListeners() {
		scene.widthProperty().addListener((obv, ov, nv) -> {
			launcher.canvas.setWidth(nv.doubleValue());
			camX += (nv.doubleValue()-ov.doubleValue())/scale/2;
			render();
		});
		scene.heightProperty().addListener((obv, ov, nv) -> {
			launcher.canvas.setHeight(nv.doubleValue());
			camY -= (nv.doubleValue()-ov.doubleValue())/scale/2;
			render();
		});
		var pressHandler = scene.getOnMousePressed();
		scene.setOnMousePressed(e -> {
			if (pressHandler != null) pressHandler.handle(e);
			if (e.getButton() != MouseButton.MIDDLE) return;
			ix = e.getSceneX();
			iy = e.getSceneY();
			ox = camX;
			oy = camY;
		});
		var dragHandler = scene.getOnMouseDragged();
		scene.setOnMouseDragged(e -> {
			if (dragHandler != null) dragHandler.handle(e);
			if (e.getButton() != MouseButton.MIDDLE) return;
			camX = ox+(e.getSceneX()-ix)/scale;
			camY = oy-(e.getSceneY()-iy)/scale;
			render();
		});
		var scrollHandler = scene.getOnScroll();
		scene.setOnScroll(e -> {
			if (scrollHandler != null) scrollHandler.handle(e);
			os = scale;
			if (e.getDeltaY() > 0) scale *= SCALE_FACTOR;
			else scale /= SCALE_FACTOR;
			if (mcs) {
				camX -= e.getSceneX()*(1/os - 1/scale);
				camY += e.getSceneY()*(1/os - 1/scale);
			} else {
				camX -= scene.getWidth()/2*(1/os - 1/scale);
				camY += scene.getHeight()/2*(1/os - 1/scale);
			}
			render();
		});
		var keyHandler = scene.getOnKeyPressed();
		scene.setOnKeyPressed(e -> {
			if (keyHandler != null) keyHandler.handle(e);
			if (e.getCode() == KeyCode.M) mcs = !mcs;
			if (e.getCode() == KeyCode.DECIMAL) {
				scale = DEFAULT_SCALE;
				center();
				render();
			}
		});
	}
	private void center() {
		camX = scene.getWidth()/2/scale;
		camY = -scene.getHeight()/2/scale;
	}
	public void track(Vessel track) {
		if (track != null) {
			if (!tracking) center();
			trackX = -track.x;
			trackY = -track.y;
			tracking = true;
		} else if (tracking) {
			camX += trackX;
			camY += trackY;
			trackX = 0;
			trackY = 0;
			tracking = false;
		}
	}
	public void realTimeTrack(Vessel track) {
		if (track != null) {
			realTimeTrackX = -track.x;
			realTimeTrackY = -track.y;
		} else {
			realTimeTrackX = 0;
			realTimeTrackY = 0;
		}
	}
	public void render() {
		Platform.runLater(() -> {
			c.setFill(Color.BLACK);
			c.fillRect(0, 0, scene.getWidth(), scene.getHeight());
			offsetX = camX + trackX;
			offsetY = camY + trackY;
			Grid.render(scene, c, offsetX, offsetY, scale);
			try {
				launcher.world.markers.forEach(m -> m.render(c, (m.x+offsetX)*scale, -(m.y+offsetY)*scale, scale));
			} catch (Exception e) {}
		});
	}
}
