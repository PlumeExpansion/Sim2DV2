package me.plume.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Vessel {
	public static final Map<Integer, Vessel> vessels = Collections.synchronizedMap(new HashMap<>());
	private int id;
	public double x, y;
	public double vx, vy;
	public boolean remove;
	public boolean immune;
	public double r;
	public static AtomicInteger counter = new AtomicInteger(0);
	public Vessel(double x, double y) {
		this.x = x;
		this.y = y;
		this.id = counter.getAndIncrement();
		vessels.put(this.id, this);
	}
	public int getId() {
		return id;
	}
	public abstract void update(double time, double dt);
	public abstract void onRemove(double time, double dt);
	public abstract Marker mark();
	public static Vessel getVessel(int id) {
		return vessels.get(id);
	}
	public static void removeVessel(int id) {
		vessels.remove(id);
	}
	public static double dist(Vessel v1, Vessel v2) {
		double dx = v1.x - v2.x;
		double dy = v1.y - v2.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	public static double dist(double x1, double y1, double x2, double y2) {
		double dx = x1-x2;
		double dy = y1-y2;
		return Math.sqrt(dx*dx + dy*dy);
	}
	public double dist(Vessel v) {
		double dx = v.x - x;
		double dy = v.y - y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	public double dist(double x, double y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	public static double dot(Vessel v1, Vessel v2) {
		return v1.x*v2.x + v1.y+v2.y;
	}
	public static double dot(double x1, double y1, double x2, double y2) {
		return x1*x2 + y1*y2;
	}
	public double dot(Vessel v) {
		return x*v.x + y*v.y;
	}
	public double dot(double x, double y) {
		return this.x*x + this.y*y;
	}
}
