package com.scnu.swimmingtrainingsystem.model;

public class SmallPlan {
	private int distance;
	private String pool;
	private String extra;

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	@Override
	public String toString() {
		return "SmallPlan [distance=" + distance + ", pool=" + pool
				+ ", extra=" + extra + "]";
	}

}
