package com.example.swimmingtraningsystem.model;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

/**
 * 运动员类
 * 
 * @author LittleByte
 * 
 */

public class Athlete extends DataSupport {

	private long id;
	private String name;
	private int age;
	private String phone;
	private String extras;
	private User user;
	private List<Plan> plans = new ArrayList<Plan>();
	private List<Score> scores = new ArrayList<Score>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
		return "Athlete [id=" + id + ", name=" + name + ", age=" + age
				+ ", phone=" + phone + ", extras=" + extras + ", user="
				+ user.getId() + ", plans=" + plans + ", scores=" + scores
				+ "]";
	}

}
