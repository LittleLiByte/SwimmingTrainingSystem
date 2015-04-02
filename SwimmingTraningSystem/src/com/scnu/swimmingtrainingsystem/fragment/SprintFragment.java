package com.scnu.swimmingtrainingsystem.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.activity.MatchSprintScoreActivity;
import com.scnu.swimmingtrainingsystem.adapter.TimeLineListAdapter;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;

/**
 * 冲刺计时fragment
 * @author LittleByte
 *
 */
@SuppressLint("HandlerLeak")
public class SprintFragment extends Fragment implements OnClickListener {

	private Activity activity;

	private ListView listView;
	private Button reset, modify;
	// 点击表盘次数
	private int clickCount = 0;
	// 经过毫秒数
	private long mlCount = 0;
	// 秒表显示时间
	private TextView tvTime;

	private ImageView min_progress, min_progress_hand, second_progress_hand,
			second_progress, hour_progress_hand, hour_progress;
	// 分针、秒针、时针动画
	private Animation rotateAnimation, secondrotateAnimation,
			hourrotateAnimation;
	// 转动角度
	float predegree = 0;
	float secondpredegree = 0;
	float hourpredegree = 0;
	private Handler handler;
	private Message msg;
	// 表盘
	private RelativeLayout clockView;
	// 毫秒计数定时器
	private Timer timer;
	// 毫秒计数定时任务
	private TimerTask task = null;

	private String strTime_count = "";
	private long time_cur;
	private long time_beg;
	// 保存成绩的list
	private ArrayList<String> time = new ArrayList<String>();
	// 保存两次成绩差的list
	private ArrayList<String> timesub = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sprint, null);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
		resetData();
	}

	/**
	 * 初始化控件及数据
	 */
	private void init() {
		activity = getActivity();
		tvTime = (TextView) activity.findViewById(R.id.tvTime);
		reset = (Button) activity.findViewById(R.id.bt_dash_reset);
		modify = (Button) activity.findViewById(R.id.bt_dash_modify);
		reset.setOnClickListener(this);
		modify.setOnClickListener(this);
		listView = (ListView) activity.findViewById(R.id.sprint_score_list);
		// ----------------计时器动画相关--------------
		min_progress = (ImageView) activity.findViewById(R.id.min_progress);
		min_progress_hand = (ImageView) activity
				.findViewById(R.id.min_progress_hand);
		second_progress_hand = (ImageView) activity
				.findViewById(R.id.second_progress_hand);
		second_progress = (ImageView) activity
				.findViewById(R.id.second_progress);
		hour_progress_hand = (ImageView) activity
				.findViewById(R.id.hour_progress_hand);
		hour_progress = (ImageView) activity.findViewById(R.id.hour_progress);
		clockView = (RelativeLayout) activity.findViewById(R.id.dash_area);

		clockView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickCount++;
				// 开始计时
				if (clickCount == 1) {
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
					setlistview();
				}

			}
		});
	}

	/**
	 * 生成成绩列表
	 */
	private void setlistview() {
		// TODO Auto-generated method stub
		time.add(tvTime.getText().toString());
		if (time.size() > 1) {
			// 两个成绩之差
			String substracion = CommonUtils.getScoreSubtraction(
					time.get(time.size() - 1), time.get(time.size() - 2));
			timesub.add(substracion);
		}

		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		for (int i = 1; i <= time.size(); i++) {
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
		TimeLineListAdapter listItemAdapter = new TimeLineListAdapter(activity,
				listItem);
		listView.setAdapter(listItemAdapter);
		listView.setSelection(time.size() - 1);
	}

	/**
	 * 重置数据
	 */
	private void resetData() {
		listView.setAdapter(null);
		tvTime.setText("点击表盘开始计时，再次点击记录成绩");
		tvTime.setTextSize(14);
		tvTime.setTextColor(getResources().getColor(R.color.gray_1));
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				tvTime.setTextSize(24);
				tvTime.setTextColor(getResources().getColor(R.color.black));
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
	 * 设置动画
	 */
	private void setAnimation() {
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
		long msec = mlCount % 1000 / 10;

		return strTime_count = String.format("%1$01d:%2$02d'%3$02d''%4$02d",
				hour, min, sec, msec);
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
	 * 重置计时器
	 */
	private void resetTimer() {
		clickCount = 0;
		predegree = 0;
		secondpredegree = 0;
		hourpredegree = 0;
		mlCount = 0;
		timerStop();
		resetData();
		setAnimation();
		time.clear();
		timesub.clear();
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_dash_reset:
			resetTimer();
			break;
		case R.id.bt_dash_modify:
			Intent intent = new Intent(activity, MatchSprintScoreActivity.class);
			intent.putStringArrayListExtra("SCORES", time);
			activity.startActivity(intent);
			activity.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		resetTimer();
	}
}
