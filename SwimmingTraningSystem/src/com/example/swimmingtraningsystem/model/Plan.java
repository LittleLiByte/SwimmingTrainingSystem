package com.example.swimmingtraningsystem.model;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

/**
 * �ƻ�ʵ����
 * 
 * @author LittleByte
 * 
 */

public class Plan extends DataSupport {
	private long id;
	private int pid;
	/**
	 * �ƻ�����
	 */
	private String name;
	/**
	 * Ӿ�ش�С
	 */
	private String pool;
	/**
	 * ��Ӿ������
	 */
	private int time;

	
	/**
	 * �����ƻ����û�
	 */
	private User user;

	/**
	 * �ƻ��е��˶�Ա
	 */
	private List<Athlete> athlete = new ArrayList<Athlete>();

	/**
	 * ʹ�øüƻ��ĳɼ�
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
