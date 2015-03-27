package com.scnu.swimmingtrainingsystem.model;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

/**
 * 计划实体类
 * 
 * @author LittleByte
 * 
 */

public class Plan extends DataSupport {
	private long id;
	private int pid;
	/**
	 * 泳池大小
	 */
	private String pool;

	/**
	 * 该计划预计游泳进行的总距离
	 */
	private int distance;

	/**
	 * 该计划的备注，方便查阅并区分成绩
	 */
	private String extra;

	/**
	 * 创建计划的用户
	 */
	private User user;

	/**
	 * 计划中的运动员
	 */
	private List<Athlete> athlete = new ArrayList<Athlete>();

	/**
	 * 使用该计划的成绩
	 */
	private List<Score> scores = new ArrayList<Score>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Athlete> getAthlete() {
		return athlete;
	}

	public void setAthlete(List<Athlete> athlete) {
		this.athlete = athlete;
	}

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	@Override
	public String toString() {
		return "Plan [id=" + id + ", pid=" + pid + ", pool=" + pool
				+ ", distance=" + distance + ", extra=" + extra + "]";
	}
}
