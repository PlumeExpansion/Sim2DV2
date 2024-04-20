package me.plume.vessels;

import me.plume.drivers.WorldEngine;

public class ProxyShell extends FusedShell {
	static final double PROXY_R = 3;
	public ProxyShell(double x, double y, double vx, double vy, double spawn, double life, WorldEngine world) {
		super(x, y, vx, vy, spawn, life, world);
		this.r += PROXY_R;
	}
}
