package com.scnu.swimmingtrainingsystem.service;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.scnu.swimmingtrainingsystem.util.Constants;

@SuppressLint("DefaultLocale")
public class TimeService extends Service {
	private Timer timer = null;
	private TimerTask task;
	// 毫秒计数定时任务
	private Intent timeIntent = null;
	private Bundle bundle = null;
	private String curTime = "";
	private long time_cur;
	private long time_beg;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(Constants.TAG, "TimeService->createService");
		// 初始化
		this.init();
		// 定时器发送广播
		task=new TimerTask() {
			@Override
			public void run() {
				// 发送广播
				sendTimeChangedBroadcast();
			}
		};
		timer.schedule(task, 10, 10);
		time_beg = System.currentTimeMillis();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 相关变量初始化
	 */
	private void init() {
		timeIntent = new Intent();
		bundle = new Bundle();
		timer = new Timer(true);
	}

	/**
	 * 发送广播，通知UI层时间已改变
	 */
	private void sendTimeChangedBroadcast() {
		time_cur = System.currentTimeMillis();
		long mlCount = time_cur - time_beg;
		curTime = getStrTime(mlCount);
		bundle.putString("time", curTime);
		bundle.putLong("count", mlCount);
		timeIntent.putExtras(bundle);
		timeIntent.setAction(Constants.TIME_CHANGE_ACTION);
		// 发送广播，通知UI层时间改变了
		sendBroadcast(timeIntent);
	}

	/**
	 * 生成固定格式的时间字符串
	 * 
	 * @return
	 */
	protected String getStrTime(long mlCount) {
		// 秒数
		long time_count_s = mlCount / 1000;
		// 小时数
		long hour = time_count_s / 3600;
		// 分
		long min = time_count_s / 60 - hour * 60;
		// 秒
		long sec = time_count_s - hour * 3600 - min * 60;
		// 毫秒
		long msec = mlCount % 1000 / 10;

		return String.format("%1$01d:%2$02d'%3$02d''%4$02d", hour, min, sec,
				msec);
	}

	@Override
	public ComponentName startService(Intent service) {
		Log.i(Constants.TAG, "TimeService->createService");
		return super.startService(service);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(Constants.TAG, "TimeService->destroyService");
		task.cancel();
		task = null;
		timer.cancel();
		timer.purge();
		timer = null;
	}
}
