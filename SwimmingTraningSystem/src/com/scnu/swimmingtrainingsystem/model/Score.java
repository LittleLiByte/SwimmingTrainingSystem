package com.scnu.swimmingtrainingsystem.model;

import org.litepal.crud.DataSupport;

/**
 * 成绩实体类
 * 
 * @author LittleByte
 * 
 */
public class Score extends DataSupport {

	/**
	 * 成绩id
	 */
	private long id;
	/**
	 * 本次成绩是第几轮测试结果
	 */
	private int times;
	/**
	 * 具体成绩
	 */
	private String score;
	/**
	 * 创建成绩的日期
	 */
	private String date;

	/**
	 * 创建本次成绩时运动员游泳的距离
	 */
	private int distance;
	
	/**
	 * 成绩类型
	 */
	private int type;

	/**
	 * 本次成绩所属的运动员
	 */
	private Athlete athlete;
	/**
	 * 本次成绩对应的计划
	 */
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
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
				+ ", date=" + date + ", distance=" + distance + ", type="
				+ type + ", athlete=" + athlete + ", p=" + p + "]";
	}
}
