package com.example.swimmingtraningsystem.activity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.litepal.LitePalApplication;

import com.example.swimmingtraningsystem.util.Constants;

import android.app.Activity;

public class MyApplication extends LitePalApplication {

	private Map<String, Object> mMap;
	private List<Activity> mList = new LinkedList<Activity>();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 初始化全局变量

		mMap = new HashMap<String, Object>();

		// 保存游泳趟数，控制跳转
		mMap.put(Constants.SWIM_TIME, 1);
		// 保存是第几次计时，提醒用户是第几次计时之中
		mMap.put(Constants.CURRENT_SWIM_TIME, "");
		// 保存所选计划中多有少个运动员，用于秒表的点击次数
		mMap.put(Constants.ATHLETE_NUMBER, 0);
		// 保存所选计划中的运动院ID list
		mMap.put(Constants.ATHLTE_ID_LIST, null);
		// 保存所选的计划ID
		mMap.put(Constants.PLAN_ID, 0);
		// 保存开始计时的日期
		mMap.put(Constants.TEST_DATE, "");
		// 保存手动匹配计时按名次排行的运动员名字,方便除第一趟计时外不用再次拖动运动员进行排行
		mMap.put(Constants.DRAG_NAME_LIST, null);
		// 保存当前登录的用户id
		mMap.put(Constants.CURRENT_USER_ID, "");
		// 保存打开登录页面时检查是否可以连接服务器的状态
		mMap.put(Constants.IS_CONNECT_SERVICE, true);
	}

	/**
	 * 获取保存必须数据的map
	 * 
	 * @return
	 */
	public Map<String, Object> getMap() {
		return mMap;
	}

	public void setMap(Map<String, Object> map) {
		this.mMap = map;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	/**
	 * 关闭所有窗体，退出本应用
	 */
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

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		System.gc();
	}
}
