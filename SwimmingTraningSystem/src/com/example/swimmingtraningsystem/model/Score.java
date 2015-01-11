package com.example.swimmingtraningsystem.model;

import org.litepal.crud.DataSupport;

public class Score extends DataSupport {

	private long id;
	private int times;
	private String score;
	private String date;
	private Athlete athlete;
	private Plan p;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Athlete getAthlete() {
		return athlete;
	}

	public void setAthlete(Athlete athlete) {
		this.athlete = athlete;
	}

	public Plan getP() {
		return p;
	}

	public void setP(Plan p) {
		this.p = p;
	}

	@Override
	public String toString() {
		return "Score [id=" + id + ", times=" + times + ", score=" + score
				+ ", date=" + date + ", athlete=" + athlete.getId() + ", p="
				+ p.getId() + "]";
	}

}
