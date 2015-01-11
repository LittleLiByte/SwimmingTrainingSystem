package com.example.swimmingtraningsystem.model;

public class Temp {

	private String score;
	private String athleteName;

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getAthleteName() {
		return athleteName;
	}

	public void setAthleteName(String athleteName) {
		this.athleteName = athleteName;
	}

	@Override
	public String toString() {
		return "Temp [score=" + score + ", athleteName=" + athleteName + "]";
	}

}
