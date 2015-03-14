package com.example.swimmingtraningsystem.model;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

/**
 * �˶�Աʵ����
 * 
 * @author LittleByte
 * 
 */

public class Athlete extends DataSupport {

	/**
	 * �˶�Աid
	 */
	private long id;

	private int aid;
	/**
	 * �˶�Ա����
	 */
	private String name;
	/**
	 * �˶�Ա����
	 */
	private int age;
	/**
	 * �˶�Ա�Ա�
	 */
	private String gender;
	/**
	 * �˶�Ա�绰
	 */
	private String phone;
	/**
	 * �˶�Ա��ע
	 */
	private String extras;
	/**
	 * �˶�Ա�����Ľ���
	 */
	private User user;
	private List<Plan> plans = new ArrayList<Plan>();
	private List<Score> scores = new ArrayList<Score>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getExtras() {
		return extras;
	}

	public void setExtras(String extras) {
		this.extras = extras;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
		return "Athlete [id=" + id + ", aid=" + aid + ", name=" + name
				+ ", age=" + age + ", gender=" + gender + ", phone=" + phone
				+ ", extras=" + extras + ", user=" + user.getUid() + ", plans="
				+ plans + ", scores=" + scores + "]";
	}

}
