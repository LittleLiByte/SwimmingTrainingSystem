package com.scnu.swimmingtrainingsystem.model;

public class SmallPlan {
	private int distance;
	private String pool;
	private String extra;
	private String type;
	
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "SmallPlan [distance=" + distance + ", pool=" + pool
				+ ", extra=" + extra + ", type=" + type + "]";
	}
}
