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
		// ��ʼ��ȫ�ֱ���

		map = new HashMap<String, Object>();

		// ������Ӿ������������ת
		map.put("swimTime", 1);
		// �����ǵڼ��μ�ʱ�������û��ǵڼ��μ�ʱ֮��
		map.put("current", 0);
		// ������ѡ�ƻ��ж����ٸ��˶�Ա���������ĵ������
		map.put("athleteCount", 0);
		// ������ѡ�ƻ��е��˶�ԺID list
		map.put("athIDList", null);
		// ������ѡ�ļƻ�ID
		map.put("planID", 0);
		// ���濪ʼ��ʱ������
		map.put("testDate", "");
		// �����ֶ�ƥ���ʱ���������е��˶�Ա����,�������һ�˼�ʱ�ⲻ���ٴ��϶��˶�Ա��������
		map.put("dragList", null);
		// ���浱ǰ��¼���û�id
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
