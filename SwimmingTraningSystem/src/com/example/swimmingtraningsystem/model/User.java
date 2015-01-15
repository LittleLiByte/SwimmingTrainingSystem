package com.example.swimmingtraningsystem.model;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

public class User extends DataSupport {
	private long id;
	private int uid;
	private String username;
	private String password;
	private String Email;
	private String phone;
	private List<Athlete> athletes = new ArrayList<Athlete>();
	private List<Plan> plans = new ArrayList<Plan>();

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

	@Override
	public String toString() {
		return "User [id=" + id + ", uid=" + uid + ", username=" + username
				+ ", password=" + password + ", Email=" + Email + ", phone="
				+ phone + ", athletes=" + athletes + ", plans=" + plans + "]";
	}

}
