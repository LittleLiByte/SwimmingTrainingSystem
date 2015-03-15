package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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

import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;

/**
 * ��ʱ������
 * 
 * @author LittleByte
 * 
 */

public class TimerActivity extends Activity {

	private MyApplication app;
	private int count_max = 0;
	/**
	 * ������̴���
	 */
	private int clickCount = 0;
	/**
	 * ����������
	 */
	private long mlCount = 0;
	/**
	 * �����ʾʱ��
	 */
	private TextView tvTime;
	/**
	 * �ɼ��б�����ʾ
	 */
	private TextView tvTip;
	private TextView time_title;

	private Button match;
	/**
	 * �ɼ��б�
	 */
	private ListView scoreList;

	private int athletes = 1;
	private ImageView min_progress, min_progress_hand, second_progress_hand,
			second_progress, hour_progress_hand, hour_progress;

	/**
	 * ���롢���롢ʱ�붯��
	 */
	private Animation rotateAnimation, secondrotateAnimation,
			hourrotateAnimation;

	/**
	 * ת���Ƕ�
	 */
	float predegree = 0;
	float secondpredegree = 0;
	float hourpredegree = 0;
	private Toast toast;
	private Handler handler;
	private Message msg;
	/**
	 * �Ƿ��������
	 */
	boolean okclear = false;
	/**
	 * ����
	 */
	private RelativeLayout clockView;

	/**
	 * ���������ʱ��
	 */
	private Timer timer;
	/**
	 * ���������ʱ����
	 */
	private TimerTask task = null;

	private String[] time;

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
		//���app�е�ȫ�ֱ�����ϵͳǿ�ƻ��գ�ͨ�����¸��д���ᴥ���쳣��ֱ�ӽ�Ӧ�ý�����������½ҳ��
		long userID = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		time_title = (TextView) findViewById(R.id.time_title);
		tvTime = (TextView) findViewById(R.id.duocitvTime);
		tvTip = (TextView) findViewById(R.id.textwujici);
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

		int swimTime = ((Integer) app.getMap().get(Constants.CURRENT_SWIM_TIME)) + 1;
		time_title.setText("��" + swimTime + "�μ�ʱ");
		app.getMap().put(Constants.CURRENT_SWIM_TIME, swimTime);

		count_max = (Integer) app.getMap().get(Constants.ATHLETE_NUMBER);

		time = new String[count_max];
		clockView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickCount++;
				if (athletes <= count_max) {
					// ��ʼ��ʱ
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
						if (athletes == (count_max + 1)) {
							timerStop();
							match.setVisibility(View.VISIBLE);
							XUtils.showToast(TimerActivity.this, toast,
									"�ɼ�ȫ����¼��ɣ�");
						}
					}
				}
			}
		});
	}

	private void setupData() {

		tvTime.setText("00��00��000");
		scoreList.setAdapter(null);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					try {
						tvTime.setText(strTime_count);
						// ����ָ��ת������
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
	 * ����listview
	 */
	private void setlistview() {
		// TODO Auto-generated method stub
		okclear = true;
		// �������60����
		if ((int) (mlCount) / 60000 >= 60) {
			time[athletes - 1] = "������ʱ��Χ��";
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
		app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		finish();
	}

	/**
	 * ���ö���
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
	 * ���ɹ̶���ʽ��ʱ���ַ���
	 * 
	 * @return
	 */
	protected String getStrTime() {
		// ����
		long time_count_s = mlCount / 1000;
		// Сʱ��
		long hour = time_count_s / 3600;
		// ��
		long min = time_count_s / 60 - hour * 60;
		// ��
		long sec = time_count_s - hour * 3600 - min * 60;
		// ����
		long msec = mlCount % 1000;

		return strTime_count = String.format("%1$02d��%2$02d��%3$03d", min, sec,
				msec);
	}

	/**
	 * ���ü�ʱ��
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
	 * ��ʱ��ϣ�ֹͣ��������ʾ���
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
	 * ���ɼ����˶�Աƥ��
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
		// �����ʱ����ȴ�����гɼ�ƥ���ֱ�ӷ���,Ҫ����ǰ�ڼ��μ�ʱ��0
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
