package com.example.swimmingtraningsystem.model;

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
	 * 计划名字
	 */
	private String name;
	/**
	 * 泳池大小
	 */
	private String pool;
	/**
	 * 游泳的趟数
	 */
	private int time;

	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public List<Athlete> getAthlete() {
		return athlete;
	}

	public void setAthlete(List<Athlete> athlete) {
		this.athlete = athlete;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	@Override
	public String toString() {
		return "Plan [id=" + id + ", pid=" + pid + ", name=" + name + ", pool="
				+ pool + ", time=" + time + ", user=" + user + ", athlete="
				+ athlete + ", scores=" + scores + "]";
	}

}
