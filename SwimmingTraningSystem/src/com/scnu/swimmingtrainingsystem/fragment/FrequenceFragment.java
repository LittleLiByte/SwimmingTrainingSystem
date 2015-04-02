package com.scnu.swimmingtrainingsystem.fragment;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.R;

/**
 * 三次计频fragment
 * 
 * @author LittleByte
 * 
 */
@SuppressLint("HandlerLeak")
public class FrequenceFragment extends Fragment implements OnClickListener {
	private Button btReset;
	private TextView tvResult;
	private Activity activity;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_frequence, null);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
		resetData();
	}

	private void init() {
		activity = getActivity();
		btReset = (Button) activity.findViewById(R.id.bt_frequen_reset);
		tvResult = (TextView) activity.findViewById(R.id.tv_frequen_result);
		tvTime = (TextView) activity.findViewById(R.id.duocitvTime);
		btReset.setOnClickListener(this);
		// ----------------计时器动画相关--------------
		min_progress = (ImageView) activity
				.findViewById(R.id.duocimin_progress);
		min_progress_hand = (ImageView) activity
				.findViewById(R.id.duocimin_progress_hand);
		second_progress_hand = (ImageView) activity
				.findViewById(R.id.duocisecond_progress_hand);
		second_progress = (ImageView) activity
				.findViewById(R.id.duocisecond_progress);
		hour_progress_hand = (ImageView) activity
				.findViewById(R.id.duocihour_progress_hand);
		hour_progress = (ImageView) activity
				.findViewById(R.id.duocihour_progress);
		clockView = (RelativeLayout) activity.findViewById(R.id.frequence_area);

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
					timerStop();
					float timePermsec = 3 * 60000 / (float) mlCount;
					DecimalFormat decimalFormat = new DecimalFormat(".00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
					String p = decimalFormat.format(timePermsec);
					tvResult.setText(p + " 次/分钟");
				}

			}
		});
	}

	private void resetData() {
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
						predegree = (float) (0.0058 * mlCount);
						secondpredegree = (float) (0.358 * mlCount);
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

	private void resetTimer() {
		clickCount = 0;
		predegree = 0;
		secondpredegree = 0;
		hourpredegree = 0;
		mlCount = 0;
		timerStop();
		resetData();
		setAnimation();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_frequen_reset:
			resetTimer();
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
