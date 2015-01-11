package com.example.swimmingtraningsystem.activity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.litepal.LitePalApplication;

import android.app.Activity;

public class MyApplication extends LitePalApplication {

	private Map<String, Object> map;
	private List<Activity> mList = new LinkedList<Activity>();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 初始化全局变量

		map = new HashMap<String, Object>();

		// 保存游泳趟数，控制跳转
		map.put("swimTime", 1);
		// 保存是第几次计时，提醒用户是第几次计时之中
		map.put("current", 0);
		// 保存所选计划中多有少个运动员，用于秒表的点击次数
		map.put("athleteCount", 0);
		// 保存所选计划中的运动院ID list
		map.put("athIDList", null);
		// 保存所选的计划ID
		map.put("planID", 0);
		// 保存开始计时的日期
		map.put("testDate", "");
		// 保存手动匹配计时按名次排行的运动员名字,方便除第一趟计时外不用再次拖动运动员进行排行
		map.put("dragList", null);
		// 保存当前登录的用户id
		map.put("CurrentUser", "");
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}
