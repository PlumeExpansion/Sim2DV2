package me.plume.drivers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import me.plume.components.Scenario;
import me.plume.drivers.scenarios.MissileTargetTrack;

public class Launcher extends Application {
	static final String TITLE = "Sim2DV2";
	static final double WIDTH = 1200, HEIGHT = 800;
	public ViewEngine view;
	public WorldEngine world;
	public GraphicsContext c;
	public Canvas canvas;
	public Scene scene;
	public Stage window;
	public Scenario scenario;
	public boolean runWorld;
	public void start(Stage window) {
		canvas = new Canvas(WIDTH, HEIGHT);
		c = canvas.getGraphicsContext2D();
		c.setFill(Color.BLACK);
		c.fillRect(0, 0, WIDTH, HEIGHT);
		
		Pane pane = new Pane();
		pane.getChildren().add(canvas);
		
		scene = new Scene(pane, WIDTH, HEIGHT);
		
		world = new WorldEngine(this);
		view = new ViewEngine(this);
		
		scenario = new MissileTargetTrack(this);
		scenario.init();
		
		view.addSceneListeners();
		if (runWorld) world.start();
		
		this.window = window;
		window.setTitle(TITLE);
		window.setScene(scene);
		window.setOnCloseRequest(e -> world.stop());
		window.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
