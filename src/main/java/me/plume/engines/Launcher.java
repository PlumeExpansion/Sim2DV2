package me.plume.engines;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import me.plume.components.Scenario;
import me.plume.scenarios.TurretCIWS;

public class Launcher extends Application {
	static final double WIDTH = 1200, HEIGHT = 800;
	public ViewEngine view;
	public WorldEngine world;
	public GraphicsContext c;
	public Canvas canvas;
	public Scene scene;
	public Stage window;
	public Scenario scenario;
	public boolean runWorld, runView;
	public void start(Stage window) {
		canvas = new Canvas(WIDTH, HEIGHT);
		c = canvas.getGraphicsContext2D();
		c.setFill(Color.BLACK);
		c.fillRect(0, 0, WIDTH, HEIGHT);
		
		Pane pane = new Pane();
		pane.getChildren().add(canvas);
		
		scene = new Scene(pane, WIDTH, HEIGHT);
		
		world = new WorldEngine(this);
		scenario = new TurretCIWS(this);
		scenario.init();
		view = new ViewEngine(this);
		if (runWorld) world.start();
		if (runView) view.start();
		
		this.window = window;
		window.setTitle("Sim2DV2 - ");
		window.setScene(scene);
		window.setOnCloseRequest(e -> world.stop());
		window.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
