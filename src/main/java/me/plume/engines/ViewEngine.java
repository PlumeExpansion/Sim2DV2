package me.plume.engines;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class ViewEngine {
	static final int FPS_N = 10;
	static final double SCALE_FACTOR = 1.1;
	static final int MAX_FPS = 120;
	Launcher launcher;
	Scene scene;
	GraphicsContext c;
	AnimationTimer timer;
	double offsetX, offsetY;
	double ox, oy, ix, iy, os;
	double scale = 1;
	boolean mcs;
	public ViewEngine(Launcher instance) {
		launcher = instance;
		offsetX = Launcher.WIDTH/2;
		offsetY = -Launcher.HEIGHT/2;
		scene = launcher.scene;
		c = launcher.c;
		scene.widthProperty().addListener((obv, ov, nv) -> {
			launcher.canvas.setWidth(nv.doubleValue());
			render();
		});
		scene.heightProperty().addListener((obv, ov, nv) -> {
			launcher.canvas.setHeight(nv.doubleValue());
			render();
		});
		var pressHandler = scene.getOnMousePressed();
		scene.setOnMousePressed(e -> {
			if (pressHandler != null) pressHandler.handle(e);
			ix = e.getSceneX();
			iy = e.getSceneY();
			ox = offsetX;
			oy = offsetY;
		});
		var dragHandler = scene.getOnMouseDragged();
		scene.setOnMouseDragged(e -> {
			if (dragHandler != null) dragHandler.handle(e);
			offsetX = ox+(e.getSceneX()-ix)/scale;
			offsetY = oy-(e.getSceneY()-iy)/scale;
			render();
		});
		var scrollHandler = scene.getOnScroll();
		scene.setOnScroll(e -> {
			if (scrollHandler != null) scrollHandler.handle(e);
			os = scale;
			if (e.getDeltaY() > 0) scale *= SCALE_FACTOR;
			else scale /= SCALE_FACTOR;
			if (mcs) {
				offsetX -= e.getSceneX()*(1/os - 1/scale);
				offsetY += e.getSceneY()*(1/os - 1/scale);
			} else {
				offsetX -= scene.getWidth()/2*(1/os - 1/scale);
				offsetY += scene.getHeight()/2*(1/os - 1/scale);
			}
			render();
		});
		var keyHandler = scene.getOnKeyPressed();
		scene.setOnKeyPressed(e -> {
			if (keyHandler != null) keyHandler.handle(e);
			if (e.getCode() == KeyCode.M) mcs = !mcs;
			if (e.getCode() == KeyCode.DECIMAL) {
				scale = 1;
				offsetX = scene.getWidth()/2;
				offsetY = -scene.getHeight()/2;
				render();
			}
		});
		render();
	}
	long last, dt;
	double fpsSum;
	int fpsN;
	public void start() {
		if (timer != null) return;
		timer = new AnimationTimer() {
			public void handle(long now) {
				if (last == 0) {
					last = now;
					return;
				}
				dt = now-last;
				if (dt < 1E9/MAX_FPS) return;
				fpsSum += 1E9/dt;
				fpsN++;
				if (fpsN >= FPS_N) {
					launcher.window.setTitle(launcher.window.getTitle().split(" - ")[0]+" - "+(int) (fpsSum/fpsN)+" fps");
					fpsN = 0;
					fpsSum = 0;
				}
				launcher.world.requestMarkers();
				last = now;
			}
		};
		timer.start();
	}
	public void render() {
		if (launcher.world.requested) return;
		Platform.runLater(() -> {
			c.setFill(Color.BLACK);
			c.fillRect(0, 0, scene.getWidth(), scene.getHeight());
			Grid.render(scene, c, offsetX, offsetY, scale);
			launcher.world.markers.forEach(m -> {
				m.render(c, (m.x+offsetX)*scale, -(m.y+offsetY)*scale, scale);
			});
		});
	}
}
