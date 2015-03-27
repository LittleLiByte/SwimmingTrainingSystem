package com.scnu.swimmingtrainingsystem.model;


/**
 * 防止出现循环引用的临时成绩类
 * @author LittleByte
 *
 */
public class SmallScore {
	private String date;
	private String score;
	private int type;
	private int times;
	private int distance;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "SmallScore [date=" + date + ", score=" + score + ", type="
				+ type + ", times=" + times + ", distance=" + distance + "]";
	}

}