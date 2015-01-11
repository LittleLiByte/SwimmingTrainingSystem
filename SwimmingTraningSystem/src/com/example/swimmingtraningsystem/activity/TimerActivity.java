package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.util.XUtils;

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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 秒表界面
 * 
 * @author LittleByte
 * 
 */

public class TimerActivity extends Activity {

	private MyApplication app;
	private int COUNT_MAX = 0;
	/**
	 * 点击表盘次数
	 */
	private int clickCount = 0;
	/**
	 * 经过毫秒数
	 */
	private long mlCount = 0;
	/**
	 * 秒表显示时间
	 */
	private TextView tvTime;
	/**
	 * 成绩列表中提示
	 */
	private TextView tvTip;
	private TextView time_title;

	private Button resetBtn;
	private Button match;
	/**
	 * 成绩列表
	 */
	private ListView scoreList;

	private int athletes = 1;
	private ImageView min_progress, min_progress_hand, second_progress_hand,
			second_progress, hour_progress_hand, hour_progress;

	/**
	 * 分针、秒针、时针动画
	 */
	private Animation rotateAnimation, secondrotateAnimation,
			hourrotateAnimation;

	/**
	 * 转动角度
	 */
	float predegree = 0;
	float secondpredegree = 0;
	float hourpredegree = 0;

	private Handler handler;
	private Message msg;
	/**
	 * 是否可以重置
	 */
	boolean okclear = false;
	/**
	 * 表盘
	 */
	private RelativeLayout clockView;

	/**
	 * 毫秒计数定时器
	 */
	private Timer timer;
	/**
	 * 毫秒计数定时任务
	 */
	private TimerTask task = null;

	private String[] time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		setupView();
		setupData();
	}

	private void setupView() {
		// TODO Auto-generated method stub
		app = (MyApplication) getApplication();
		time_title = (TextView) findViewById(R.id.time_title);
		tvTime = (TextView) findViewById(R.id.duocitvTime);
		tvTip = (TextView) findViewById(R.id.textwujici);
		resetBtn = (Button) findViewById(R.id.resetbutton);
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
		match = (Button) findViewById(R.id.match_people);
		clockView = (RelativeLayout) findViewById(R.id.clcokview);

		int swimTime = ((Integer) app.getMap().get("current")) + 1;
		time_title.setText("第" + swimTime + "次计时");
		app.getMap().put("current", swimTime);

		COUNT_MAX = (Integer) app.getMap().get("athleteCount");

		time = new String[COUNT_MAX];
		clockView.setOnClickListener(new OnClickListener() {

			private Toast toast;

			@Override
			public void onClick(View v) {
				clickCount++;

				if (athletes < COUNT_MAX) {
					// 开始计时
					if (clickCount == 1) {
						if (null == timer) {
							if (null == task) {
								okclear = false;
								min_progress.setVisibility(View.VISIBLE);
								second_progress.setVisibility(View.VISIBLE);
								hour_progress.setVisibility(View.VISIBLE);
								task = new TimerTask() {
									@Override
									public void run() {
										if (null == msg) {
											msg = new Message();
										} else {
											msg = Message.obtain();
										}
										msg.what = 1;
										handler.sendMessage(msg);
									}
								};
							}
							timer = new Timer(true);
							timer.schedule(task, 1, 1);
						}
					} else {
						tvTip.setVisibility(View.GONE);
						setlistview();
					}

				} else {
					okclear = true;
					if (athletes == COUNT_MAX) {
						setlistview();
						timerStop();
						match.setVisibility(View.VISIBLE);
						XUtils.showToast(TimerActivity.this, toast, "成绩全部记录完成！");
					} else {
						XUtils.showToast(TimerActivity.this, toast, "成绩全部已经记录完成！");
					}
				}
			}
		});
	}

	private void setupData() {

		tvTime.setText("00分00秒000");
		scoreList.setAdapter(null);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					mlCount++;
					// 毫秒数
					int msec = (int) (mlCount % 1000);

					// 总共多少秒
					int totalSec = (int) (mlCount / 1000);
					int min = (totalSec / 60);
					if (min >= 60) {
						// 如果超过60分钟，重置计时器
						reset(resetBtn);
					}

					int sec = (totalSec % 60);

					try {
						// 设置指针转动动画
						setAnimation();
						// 设置时间显示变更
						tvTime.setText(String.format("%1$02d分%2$02d秒%3$03d",
								min, sec, msec));
						predegree = (float) (0.006 * mlCount);
						secondpredegree = (float) (0.36 * mlCount);
						hourpredegree = (float) (mlCount / 10000);
					} catch (Exception e) {
						tvTime.setText("" + min + "分" + sec + "秒" + msec);
						e.printStackTrace();
					}
					break;
				default:
					break;
				}

			}
		};

	}

	private void setlistview() {
		// TODO Auto-generated method stub
		okclear = true;
		// 如果超过60分钟
		if ((int) (mlCount) / 60000 >= 60) {
			time[athletes - 1] = "超出计时范围！";
		} else {
			time[athletes - 1] = tvTime.getText().toString();
		}
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < athletes; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("jishicishu", String.valueOf(1 + i));
			map.put("jicitime", time[i]);
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.list_item, new String[] { "jishicishu", "jicitime" },
				new int[] { R.id.jishicishu, R.id.jicitime });
		scoreList.setAdapter(listItemAdapter);
		scoreList.setSelection(athletes);
		athletes++;

	}

	public void back(View v) {
		app.getMap().put("current", 0);
		finish();
	}

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
	 * 重置计时器
	 * 
	 * @param v
	 */
	public void reset(View v) {
		if (okclear) {
			okclear = false;
			clickCount = 0;
			timerStop();
			predegree = 0;
			secondpredegree = 0;
			hourpredegree = 0;
			mlCount = 0;
			tvTip.setVisibility(View.VISIBLE);
			athletes = 1;
			setupData();
			setAnimation();
		}
	}

	/**
	 * 计时完毕，停止动画，显示结果
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
		Intent intent = new Intent(this, MatchScoreActivity.class);
		intent.putExtra("SCORES", time);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		timerStop();
		clockView.clearAnimation();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// 进入计时界面却不进行成绩匹配而直接返回,要将当前第几次计时置0
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			app.getMap().put("current", 0);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
