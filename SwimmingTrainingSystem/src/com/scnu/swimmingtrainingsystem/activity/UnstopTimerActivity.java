package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.scnu.swimmingtrainingsystem.service.TimeService;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;

/**
 * ����Ъ��ʱ������
 * 
 * @author LittleByte
 * 
 */

@SuppressLint("HandlerLeak")
public class UnstopTimerActivity extends Activity {

	private MyApplication app;
	private int athleteNumber = 0;
	// ������̴���
	private int clickCount = 0;
	// �����ʾʱ��
	public TextView tvTime;

	private String strTime;
	// �ɼ��б�����ʾ
	private TextView tvTip;
	private TextView time_title;
	private Button resetButton;
	// �ɼ��б�
	private ListView scoreList;
	private Intent timeService = null;
	private UITimeReceiver receiver = null;
	private int athletes = 1;

	private ImageView min_progress_hand, second_progress_hand,
			hour_progress_hand;
	// ���롢���롢ʱ�붯��
	private Animation rotateAnimation, secondrotateAnimation,
			hourrotateAnimation;
	// ת���Ƕ�
	float predegree = 0;
	float secondpredegree = 0;
	float hourpredegree = 0;
	private Toast toast;
	private long condition=0L;
	// ����
	private RelativeLayout clockView;

	// ����ɼ���list
	private ArrayList<String> time = new ArrayList<String>();
	// �������γɼ����list
	private ArrayList<String> timesub = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		try {
			setupView();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	@SuppressWarnings("unchecked")
	private void setupView() {
		// TODO Auto-generated method stub
		app = (MyApplication) getApplication();
		long userID = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		time_title = (TextView) findViewById(R.id.time_title);
		tvTime = (TextView) findViewById(R.id.duocitvTime);
		tvTip = (TextView) findViewById(R.id.textwujici);
		resetButton = (Button) findViewById(R.id.resetbutton);
		resetButton.setVisibility(View.GONE);
		scoreList = (ListView) findViewById(R.id.duocijishilist);
		min_progress_hand = (ImageView) this
				.findViewById(R.id.duocimin_progress_hand);
		second_progress_hand = (ImageView) this
				.findViewById(R.id.duocisecond_progress_hand);
		hour_progress_hand = (ImageView) this
				.findViewById(R.id.duocihour_progress_hand);
		clockView = (RelativeLayout) findViewById(R.id.clcokview);

		int swimTime = ((Integer) app.getMap().get(Constants.CURRENT_SWIM_TIME)) + 1;
		time_title.setText("��" + swimTime + "�μ�ʱ");
		app.getMap().put(Constants.CURRENT_SWIM_TIME, swimTime);
		athleteNumber = ((List<String>) app.getMap().get(
				Constants.DRAG_NAME_LIST)).size();

		condition = (Long) app.getMap().get(Constants.JUMP_TIME);
		if (condition == 0L) {
			tvTime.setText("0:00'00''00");
		} else {
			registerBroadcastReceiver();
			//���������δ������������ʱ����
			if (!CommonUtils.isServiceRunning(this, "TimeService")) {
				startTimeService();
			}
			tvTime.setText(strTime);
		}

		clockView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��ʱ���Ƿ�������
				clickCount++;
				if (athletes <= athleteNumber * 2) {
					// ��ʼ��ʱ
					if (condition == 0 && clickCount == 1) {
						registerBroadcastReceiver();
						// ��������ʱ��ı���͹㲥��֪ͨUI���޸�ʱ��
						startTimeService();
					} else {
						tvTip.setVisibility(View.GONE);
						setlistview();
						if (athletes == (athleteNumber + 1)) {
							CommonUtils.showToast(UnstopTimerActivity.this,
									toast, "�ɼ�ȫ����¼��ɣ�");
						}
					}
				} else {
					CommonUtils.showToast(UnstopTimerActivity.this, toast,
							"�벻Ҫ����̫�಻��Ҫ�ĳɼ���");
				}
			}
		});

	}

	/**
	 * ����listview
	 */
	private void setlistview() {
		// TODO Auto-generated method stub
		time.add(athletes - 1, tvTime.getText().toString());
		if (athletes > 1) {
			// �����ɼ�֮��
			String substracion = CommonUtils.getScoreSubtraction(
					time.get(athletes - 1), time.get(athletes - 2));
			timesub.add(substracion);
		}

		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		for (int i = 1; i <= athletes; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("athlete_score", time.get(i - 1));
			if (i == 1) {
				// ��һ��
				map.put("score_between", "");
				map.put("athlete_ranking", "��1��");
			} else {
				map.put("score_between", timesub.get(i - 2));
				map.put("athlete_ranking", "��" + i + "��");
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
		app.getMap().put(Constants.JUMP_TIME, 0L);
		unregisterReceiver(receiver);
		// ֹͣ����
		stopService(timeService);
		finish();
	}

	/**
	 * ���ö���
	 */
	public void setAnimation(long count) {
		rotateAnimation = new RotateAnimation(predegree,
				(float) (0.006 * count), Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		secondrotateAnimation = new RotateAnimation(secondpredegree,
				(float) (0.36 * count), Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		hourrotateAnimation = new RotateAnimation(hourpredegree,
				(float) (count / 10000), Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(100);
		rotateAnimation.setFillAfter(true);
		hourrotateAnimation.setDuration(100);
		hourrotateAnimation.setFillAfter(true);
		secondrotateAnimation.setDuration(100);
		secondrotateAnimation.setFillAfter(true);
		min_progress_hand.startAnimation(rotateAnimation);
		second_progress_hand.startAnimation(secondrotateAnimation);
		hour_progress_hand.startAnimation(hourrotateAnimation);
		predegree = (float) (0.006 * count);
		secondpredegree = (float) (0.36 * count);
		hourpredegree = (float) (count / 10000);
	}

	/**
	 * ���ɼ����˶�Աƥ��
	 * 
	 * @param v
	 */
	public void matchAthlete(View v) {
		if (scoreList.getAdapter() != null
				&& scoreList.getAdapter().getCount() != 0) {
			Intent intent = new Intent(this, MatchScoreActivity.class);
			intent.putStringArrayListExtra("SCORES", time);
			app.getMap().put(Constants.JUMP_TIME, System.currentTimeMillis());
			startActivity(intent);
			finish();
		} else {
			CommonUtils.showToast(this, toast, "�뿪ʼ��ʱ����¼����һ�γɼ���");
		}
	}

	/**
	 * ע��㲥
	 */
	private void registerBroadcastReceiver() {
		receiver = new UITimeReceiver();
		IntentFilter filter = new IntentFilter(Constants.TIME_CHANGE_ACTION);
		registerReceiver(receiver, filter);
	}

	/**
	 * ��������
	 */
	private void startTimeService() {
		timeService = new Intent(this, TimeService.class);
		this.startService(timeService);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// �����ʱ����ȴ�����гɼ�ƥ���ֱ�ӷ���,Ҫ����ǰ�ڼ��μ�ʱ��0
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.out.println("onKeyDown");
			app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
			app.getMap().put(Constants.JUMP_TIME, 0L);
			unregisterReceiver(receiver);
			receiver = null;
			// ֹͣ����
			stopService(timeService);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class UITimeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (Constants.TIME_CHANGE_ACTION.equals(action)) {
				Bundle bundle = intent.getExtras();
				String strTime = bundle.getString("time");
				long count = bundle.getLong("count");
				tvTime.setText(strTime);
				setAnimation(count);
			}
		}
	}
}
