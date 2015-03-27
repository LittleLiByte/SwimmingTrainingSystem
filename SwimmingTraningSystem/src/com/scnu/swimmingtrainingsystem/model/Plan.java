package com.scnu.swimmingtrainingsystem.model;

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
	 * Ӿ�ش�С
	 */
	private String pool;

	/**
	 * �üƻ�Ԥ����Ӿ���е��ܾ���
	 */
	private int distance;

	/**
	 * �üƻ��ı�ע��������Ĳ����ֳɼ�
	 */
	private String extra;

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
