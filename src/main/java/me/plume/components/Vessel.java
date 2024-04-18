package me.plume.components;

import java.util.LinkedHashMap;

public abstract class Vessel {
	static final LinkedHashMap<Integer, Vessel> vessels = new LinkedHashMap<>();
	private int id;
	public double x, y;
	public double vx, vy;
	public boolean remove;
	public Vessel(double x, double y) {
		this.x = x;
		this.y = y;
		int n = 0;
		while (vessels.containsKey(n)) n++;
		id = n;
		vessels.put(n, this);
	}
	public int getId() {
		return id;
	}
	public abstract void update(double time, double dt);
	public abstract Marker mark();
	public static Vessel getVessel(int id) {
		return vessels.get(id);
	}
	public static void removeVessel(int id) {
		vessels.remove(id);
	}
}
