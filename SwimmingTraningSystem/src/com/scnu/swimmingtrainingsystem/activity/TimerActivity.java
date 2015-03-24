package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.adapter.TimeLineListAdapter;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;

/**
 * 计时秒表界面
 * 
 * @author LittleByte
 * 
 */

public class TimerActivity extends Activity {

	private MyApplication app;
	private int athleteNumber = 0;
	// 点击表盘次数
	private int clickCount = 0;
	// 经过毫秒数
	private long mlCount = 0;
	// 秒表显示时间
	private TextView tvTime;
	// 成绩列表中提示
	private TextView tvTip;
	private TextView time_title;
	private Button resetButton;
	// 成绩列表
	private ListView scoreList;

	private int athletes = 1;

	private ImageView min_progress, min_progress_hand, second_progress_hand,
			second_progress, hour_progress_hand, hour_progress;
	// 分针、秒针、时针动画
	private Animation rotateAnimation, secondrotateAnimation,
			hourrotateAnimation;
	// 转动角度
	float predegree = 0;
	float secondpredegree = 0;
	float hourpredegree = 0;
	private Toast toast;
	private Handler handler;
	private Message msg;
	// 是否可以重置
	boolean okclear = false;
	// 表盘
	private RelativeLayout clockView;
	// 毫秒计数定时器
	private Timer timer;
	// 毫秒计数定时任务
	private TimerTask task = null;

	// 保存成绩的list
	private ArrayList<String> time = new ArrayList<String>();
	// 保存两次成绩差的list
	private ArrayList<String> timesub = new ArrayList<String>();
	private String strTime_count = "";
	private long time_cur;
	private long time_beg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		try {
			setupView();
			setupData();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	private void setupView() {
		// TODO Auto-generated method stub
		app = (MyApplication) getApplication();
		// 如果app中的全局变量被系统强制回收，通过以下改行代码会触发异常，直接将应用界面重启至登陆页面
		@SuppressWarnings("unused")
		long userID = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		time_title = (TextView) findViewById(R.id.time_title);
		tvTime = (TextView) findViewById(R.id.duocitvTime);
		tvTip = (TextView) findViewById(R.id.textwujici);
		resetButton = (Button) findViewById(R.id.resetbutton);
		scoreList = (ListView) findViewById(R.id.duocijishilist);
		min_progress = (ImageView) this.findViewById(R.id.duocimin_progress);
		min_progress_hand = (ImageView) this
				.findViewById(R.id.duocimin_progress_hand);
		second_progress_hand = (ImageView) this
				.findViewById(R.id.duocisecond_progress_hand);
		second_progress = (ImageView) this
				.findViewById(R.id.duocisecond_progress);
		hour_progress_hand = (ImageView) this
				.findViewById(R.id.duocihour_progress_hand);
		hour_progress = (ImageView) this.findViewById(R.id.duocihour_progress);
		clockView = (RelativeLayout) findViewById(R.id.clcokview);

		int swimTime = ((Integer) app.getMap().get(Constants.CURRENT_SWIM_TIME)) + 1;
		time_title.setText("第" + swimTime + "次计时");
		app.getMap().put(Constants.CURRENT_SWIM_TIME, swimTime);
		athleteNumber = ((List<String>) app.getMap().get(
				Constants.DRAG_NAME_LIST)).size();

		clockView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 计时器是否在运行
				clickCount++;
				if (athletes <= athleteNumber * 2) {
					// 开始计时
					if (clickCount == 1) {
						okclear = false;
						timer = new Timer(true);
						min_progress.setVisibility(View.VISIBLE);
						second_progress.setVisibility(View.VISIBLE);
						hour_progress.setVisibility(View.VISIBLE);
						task = new TimerTask() {
							@Override
							public void run() {
								msg = handler.obtainMessage();
								msg.what = 1;
								time_cur = System.currentTimeMillis();
								mlCount = time_cur - time_beg;
								getStrTime();
								msg.sendToTarget();
							}
						};
						time_beg = System.currentTimeMillis();
						timer.schedule(task, 1, 10);
					} else {
						tvTip.setVisibility(View.GONE);
						setlistview();
						if (athletes == (athleteNumber + 1)) {
							XUtils.showToast(TimerActivity.this, toast,
									"成绩全部记录完成！");
						}
					}
				} else {
					timerStop();
					XUtils.showToast(TimerActivity.this, toast,
							"请不要保存太多不必要的成绩！");
				}
			}
		});

	}

	@SuppressLint("HandlerLeak")
	private void setupData() {
		scoreList.setAdapter(null);
		tvTime.setText("0:00'00''00");
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					try {
						tvTime.setText(strTime_count);
						// 设置指针转动动画
						setAnimation();
						predegree = (float) (0.006 * mlCount);
						secondpredegree = (float) (0.36 * mlCount);
						hourpredegree = (float) (mlCount / 10000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}

			}
		};

	}

	/**
	 * 生成listview
	 */
	private void setlistview() {
		// TODO Auto-generated method stub
		okclear = true;
		// 如果超过60分钟
		if ((int) (mlCount) / 60000 >= 60) {
			time.add(athletes - 1, "超出计时范围！");
		} else {
			time.add(athletes - 1, tvTime.getText().toString());
			if (athletes > 1) {
				// 两个成绩之差
				String substracion = XUtils.getScoreSubtraction(
						time.get(athletes - 1), time.get(athletes - 2));
				timesub.add(substracion);
			}

		}
		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		for (int i = 1; i <= athletes; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("athlete_score", time.get(i - 1));
			if (i == 1) {
				// 第一名
				map.put("score_between", "");
				map.put("athlete_ranking", "第1名");
			} else {
				map.put("score_between", timesub.get(i - 2));
				map.put("athlete_ranking", "第" + i + "名");
			}
			listItem.add(map);
		}
		TimeLineListAdapter listItemAdapter = new TimeLineListAdapter(this,
				listItem);
		scoreList.setAdapter(listItemAdapter);
		scoreList.setSelection(athletes - 1);
		athletes++;

	}

	public void back(View v) {
		app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		finish();
	}

	/**
	 * 设置动画
	 */
	public void setAnimation() {
		rotateAnimation = new RotateAnimation(predegree,
				(float) (0.006 * mlCount), Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		secondrotateAnimation = new RotateAnimation(secondpredegree,
				(float) (0.36 * mlCount), Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		hourrotateAnimation = new RotateAnimation(hourpredegree,
				(float) (mlCount / 10000), Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(100);
		rotateAnimation.setFillAfter(true);
		hourrotateAnimation.setDuration(100);
		hourrotateAnimation.setFillAfter(true);
		secondrotateAnimation.setDuration(100);
		secondrotateAnimation.setFillAfter(true);

		min_progress_hand.startAnimation(rotateAnimation);
		min_progress.startAnimation(rotateAnimation);
		second_progress_hand.startAnimation(secondrotateAnimation);
		second_progress.startAnimation(secondrotateAnimation);
		hour_progress_hand.startAnimation(hourrotateAnimation);
		hour_progress.startAnimation(hourrotateAnimation);
	}

	/**
	 * 生成固定格式的时间字符串
	 * 
	 * @return
	 */
	protected String getStrTime() {
		// 秒数
		long time_count_s = mlCount / 1000;
		// 小时数
		long hour = time_count_s / 3600;
		// 分
		long min = time_count_s / 60 - hour * 60;
		// 秒
		long sec = time_count_s - hour * 3600 - min * 60;
		// 毫秒
		long msec = mlCount % 1000/10;

		return strTime_count = String.format("%1$01d:%2$02d'%3$02d''%4$02d",
				hour, min, sec, msec);
		// %1$01d:%2$02d'%3$ 03d''%4$ 03d
	}

	/**
	 * 重置计时器
	 * 
	 * @param v
	 */
	public void reset(View v) {
		if (okclear) {
			okclear = false;
			clickCount = 0;
			time.clear();
			timesub.clear();
			predegree = 0;
			secondpredegree = 0;
			hourpredegree = 0;
			mlCount = 0;
			tvTip.setVisibility(View.VISIBLE);
			athletes = 1;
			timerStop();
			setupData();
			setAnimation();
		}
	}

	/**
	 * 暂停计时器
	 */
	public void timerStop() {
		if (null != task && null != timer) {
			task.cancel();
			task = null;
			timer.cancel();
			timer.purge();
			timer = null;
			handler.removeMessages(msg.what);
		}
	}

	/**
	 * 将成绩与运动员匹配
	 * 
	 * @param v
	 */
	public void matchAthlete(View v) {
		if (scoreList.getAdapter() != null
				&& scoreList.getAdapter().getCount() != 0) {
			Intent intent = new Intent(this, MatchScoreActivity.class);
			intent.putStringArrayListExtra("SCORES", time);
			startActivity(intent);
			finish();
		} else {
			XUtils.showToast(this, toast, "请开始计时并记录至少一次成绩！");
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		reset(resetButton);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// 进入计时界面却不进行成绩匹配而直接返回,要将当前第几次计时置0
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
