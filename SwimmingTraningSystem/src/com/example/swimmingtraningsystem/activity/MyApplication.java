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
		// ��ʼ��ȫ�ֱ���

		mMap = new HashMap<String, Object>();

		// ������Ӿ������������ת
		mMap.put(Constants.SWIM_TIME, 1);
		// �����ǵڼ��μ�ʱ�������û��ǵڼ��μ�ʱ֮��
		mMap.put(Constants.CURRENT_SWIM_TIME, "");
		// ������ѡ�ƻ��ж����ٸ��˶�Ա���������ĵ������
		mMap.put(Constants.ATHLETE_NUMBER, 0);
		// ������ѡ�ƻ��е��˶�ԺID list
		mMap.put(Constants.ATHLTE_ID_LIST, null);
		// ������ѡ�ļƻ�ID
		mMap.put(Constants.PLAN_ID, 0);
		// ���濪ʼ��ʱ������
		mMap.put(Constants.TEST_DATE, "");
		// �����ֶ�ƥ���ʱ���������е��˶�Ա����,�������һ�˼�ʱ�ⲻ���ٴ��϶��˶�Ա��������
		mMap.put(Constants.DRAG_NAME_LIST, null);
		// ���浱ǰ��¼���û�id
		mMap.put(Constants.CURRENT_USER_ID, "");
		// ����򿪵�¼ҳ��ʱ����Ƿ�������ӷ�������״̬
		mMap.put(Constants.IS_CONNECT_SERVICE, true);
	}

	/**
	 * ��ȡ����������ݵ�map
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
	 * �ر����д��壬�˳���Ӧ��
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
