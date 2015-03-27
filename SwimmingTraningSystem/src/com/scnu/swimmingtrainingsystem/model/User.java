package com.scnu.swimmingtrainingsystem.model;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

/**
 * �û�ʵ����
 * 
 * @author LittleByte
 * 
 */
public class User extends DataSupport {
	/**
	 * �û�id
	 */
	private long id;
	private int uid;
	/**
	 * �û���
	 */
	private String username;
	/**
	 * �û�����
	 */
	private String password;
	/**
	 * �û�����
	 */
	private String Email;
	/**
	 * �û��ֻ�
	 */
	private String phone;

	private List<Athlete> athletes = new ArrayList<Athlete>();
	private List<Plan> plans = new ArrayList<Plan>();
	private List<Score> scores = new ArrayList<Score>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Athlete> getAthletes() {
		return athletes;
	}

	public void setAthletes(List<Athlete> athletes) {
		this.athletes = athletes;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", uid=" + uid + ", username=" + username
				+ ", password=" + password + ", Email=" + Email + ", phone="
				+ phone + ", athletes=" + athletes + ", plans=" + plans
				+ ", scores=" + scores + "]";
	}
}
