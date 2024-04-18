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
	static final double FRAME_DEL = 1.0/60;
	static final double DEFAULT_SCALE = 10;
	Launcher launcher;
	Scene scene;
	GraphicsContext c;
	AnimationTimer timer;
	double offsetX, offsetY;
	double ox, oy, ix, iy, os;
	double scale = DEFAULT_SCALE;
	boolean mcs;
	public ViewEngine(Launcher instance) {
		launcher = instance;
		offsetX = Launcher.WIDTH/2/DEFAULT_SCALE;
		offsetY = -Launcher.HEIGHT/2/DEFAULT_SCALE;
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
				scale = DEFAULT_SCALE;
				offsetX = scene.getWidth()/2/DEFAULT_SCALE;
				offsetY = -scene.getHeight()/2/DEFAULT_SCALE;
				render();
			}
		});
		render();
	}
	public void render() {
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
