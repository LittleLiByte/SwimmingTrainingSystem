package com.scnu.swimmingtrainingsystem.model;

import org.litepal.crud.DataSupport;

/**
 * �ɼ�ʵ����
 * 
 * @author LittleByte
 * 
 */
public class Score extends DataSupport {

	/**
	 * �ɼ�id
	 */
	private long id;
	/**
	 * ���γɼ��ǵڼ��ֲ��Խ��
	 */
	private int times;
	/**
	 * ����ɼ�
	 */
	private String score;
	/**
	 * �����ɼ�������
	 */
	private String date;

	/**
	 * �������γɼ�ʱ�˶�Ա��Ӿ�ľ���
	 */
	private int distance;
	
	/**
	 * �ɼ�����
	 */
	private int type;

	/**
	 * ���γɼ��������˶�Ա
	 */
	private Athlete athlete;
	/**
	 * ���γɼ���Ӧ�ļƻ�
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
