package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.adapter.MyAndroidWheelAdapter;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.model.Score;
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.util.ScreenUtils;
import com.example.swimmingtraningsystem.util.XUtils;

/**
 * ��Ӿ����ʱActivity
 * 
 * @author LittleByte
 * 
 */
public class SeparateTimingActivity extends Activity {

	private MyApplication app;
	private DBManager dbManager;
	private int COUNT_MAX = 0;
	private int poolwidth;
	private Context context;
	private List<Long> athID;
	private long planID;
	/**
	 * Ҫ���м�ʱ���˶�Ա
	 */
	private List<Athlete> athletes;

	private LinearLayout poolway;
	private RelativeLayout rlStartCount;
	private TextView tv_clock;
	private TextView currentTimeTextView;

	/**
	 * �������м�ʱ�ɼ�
	 */
	private List<TextView> tv_times = new ArrayList<TextView>();
	/**
	 * ��������Ӿ����Ϣ
	 */
	private List<TextView> tv_tips = new ArrayList<TextView>();
	/**
	 * ����ڼ���Ӿ��
	 */
	private Set<String> poolways = new HashSet<String>();
	/**
	 * ����Ӿ���ϵ��˶�Ա����
	 */
	private Set<String> chooseAthletes = new HashSet<String>();

	private List<RelativeLayout> relativelayouts = new ArrayList<RelativeLayout>();
	private Dialog alertDialog;
	private int clickCount = 0;
	private RequestQueue mQueue;
	/**
	 * �Ƿ���Կ�ʼ��ʱ
	 */
	private boolean isBegin = false;
	/**
	 * ����������
	 */
	private long mlCount = 0;
	/**
	 * ���������ʱ��
	 */
	private Timer timer;
	/**
	 * ���������ʱ����
	 */
	private TimerTask task = null;
	protected long time_beg;
	protected long time_cur;
	private String strTime_count = "";
	private Message msg;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				tv_clock.setText(strTime_count);
				break;
			}

		}
	};
	protected boolean scrolling = false;
	private Toast toast;

	@SuppressLint("HandlerLeak")
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seperatetiming);

		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	private void init() {
		app = (MyApplication) getApplication();
		context = SeparateTimingActivity.this;
		dbManager = DBManager.getInstance();
		mQueue = Volley.newRequestQueue(getApplicationContext());
		int height = ScreenUtils.getScreenHeight(this)
				- ScreenUtils.dip2px(this, 50);
		tv_clock = (TextView) findViewById(R.id.tv_clcok);
		currentTimeTextView = (TextView) findViewById(R.id.number_tip);
		rlStartCount = (RelativeLayout) findViewById(R.id.rl_clockview);
		athID = (List<Long>) app.getMap().get(Constants.ATHLTE_ID_LIST);
		planID = (Long) app.getMap().get(Constants.PLAN_ID);
		int swimTime = ((Integer) app.getMap().get(Constants.CURRENT_SWIM_TIME)) + 1;
		currentTimeTextView.setText("��" + swimTime + "�μ�ʱ");
		app.getMap().put(Constants.CURRENT_SWIM_TIME, swimTime);
		athletes = dbManager.getAthletes(athID);
		COUNT_MAX = athletes.size();
		poolwidth = height / COUNT_MAX;

		rlStartCount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int j = 0; j < COUNT_MAX; j++) {
					String info = tv_tips.get(j).getText().toString();
					poolways.add(info.substring(0, 3));
					chooseAthletes.add(info.substring(5));
				}

				if (chooseAthletes.size() != COUNT_MAX
						|| poolways.size() != COUNT_MAX) {
					XUtils.showToast(context, toast, "����Ӿ�����˶�Ա�ظ����䣡����ϸ���");
				} else {
					isBegin = true;
					startTimer();
					rlStartCount.setClickable(false);
				}

			}
		});
		initSwimingPool();
	}

	private void initSwimingPool() {
		// TODO Auto-generated method stub
		poolway = (LinearLayout) findViewById(R.id.poolway);
		// ��ʱ�ɼ��ı��Ĵ�С����,�����ڲ�������
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		// Ӿ���������ı�,�����ڲ��ֶ���
		RelativeLayout.LayoutParams lp_name = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp_name.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		// ����RelativeLayout�Ĵ�С����
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, poolwidth);
		for (int i = 0; i < COUNT_MAX; i++) {
			final RelativeLayout rl = new RelativeLayout(this);
			rl.setLayoutParams(lp1);
			rl.setPadding(10, 10, 10, 10);
			rl.setBackgroundResource(R.drawable.bg_singlepool);
			poolway.addView(rl);

			// Ӿ����ź�������ʾ
			final TextView tv_name = new TextView(this);
			tv_name.setLayoutParams(lp_name);
			tv_name.setTextSize(22);
			tv_name.setTextColor(getResources().getColor(R.color.aliceblue));
			tv_name.setText("��" + (i + 1) + "��" + "����"
					+ athletes.get(i).getName());
			rl.addView(tv_name);
			tv_tips.add(tv_name);
			// ��ʱ�ɼ�
			final TextView tv = new TextView(this);
			tv.setLayoutParams(lp);
			tv.setTextSize(25);
			tv.setTextColor(Color.GRAY);
			tv_times.add(tv);
			rl.addView(tv);
			relativelayouts.add(rl);
			rl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (!isBegin) {
						// ��δ�������ʱ���Ӿ����������
						createDialog(tv_name);
					} else {
						// ������ɺ���Ӿ��������ʾ��Ӿ���˶�Ա�ɼ�
						String currentTime = tv_clock.getText().toString();
						if (timer != null) {
							clickCount++;
							tv.setText(currentTime);
							rl.setClickable(false);
							if (clickCount == COUNT_MAX) {
								timerStop();
								showDialog();
								clickCount = 0;
							}
						}
					}
				}
			});

		}

	}

	/**
	 * ��ʱ��ϣ�ֹͣtimer
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
	 * �����˶�ԱӾ���Ի���
	 * 
	 * @param view
	 */
	private void createDialog(final TextView txt) {
		alertDialog = new Dialog(context, R.style.MyDialog);
		alertDialog.setContentView(View.inflate(context,
				R.layout.dialog_dispatch_athlete, null));
		alertDialog.show();
		Window window = alertDialog.getWindow();
		TextView title = (TextView) window.findViewById(R.id.dispatch_athlete);
		title.setText("�����Ӿ���˶�Ա");
		final WheelView poolwheel = (WheelView) window
				.findViewById(R.id.wv_poolway);
		final WheelView athletewheel = (WheelView) window
				.findViewById(R.id.wv_athleteName);

		poolwheel.setVisibleItems(4);
		poolwheel.setViewAdapter(new MyAndroidWheelAdapter(this));

		final List<String> aths = new ArrayList<String>();
		for (Athlete a : athletes) {
			aths.add(a.getName());
		}
		String[] allNames1 = aths.toArray(new String[COUNT_MAX]);
		final String allNames[][] = new String[][] { allNames1, allNames1,
				allNames1, allNames1, allNames1, allNames1, allNames1,
				allNames1 };
		athletewheel.setVisibleItems(5);
		poolwheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateCities(athletewheel, allNames, newValue);
				}
			}
		});

		poolwheel.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				scrolling = false;
			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateCities(athletewheel, allNames, poolwheel.getCurrentItem());
			}
		});

		poolwheel.setCurrentItem(1);

		Button ok = (Button) window.findViewById(R.id.deperate_back);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pickPool = Constants.countries[poolwheel
						.getCurrentItem()];
				String pickName = aths.get(athletewheel.getCurrentItem());
				txt.setText(pickPool + "����" + pickName);
				alertDialog.cancel();
			}
		});

	}

	/**
	 * Updates the wheel
	 */
	private void updateCities(WheelView city, String cities[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				cities[index]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
		city.setCurrentItem(cities[index].length / 2);
	}

	/**
	 * ��ʱ��Ϻ󵯳��ĶԻ���
	 */
	private void showDialog() {
		View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog,
				null);
		final Dialog dialog = new Dialog(this,
				R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// ������ʾ����
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		// ������������Ϊ�˱�֤��ť����ˮƽ����
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// ������ʾλ��
		dialog.onWindowAttributesChanged(wl);
		// ���ý�ֹ�����Χ��ɢ
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		Button reset = (Button) view.findViewById(R.id.dialog_reset);
		reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reset();
				dialog.dismiss();
			}
		});
		Button save = (Button) view.findViewById(R.id.dialog_save);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveScores();
				dialog.dismiss();
			}

		});
		Button cancle = (Button) view.findViewById(R.id.dialog_cancle);
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				((Activity) context).finish();
			}
		});

	}

	/**
	 * ���汾�ּ�ʱ�ɼ�
	 */
	private void saveScores() {
		// ��ȡ���μ�¼���Ե�����
		String date = (String) app.getMap().get(Constants.TEST_DATE);
		// �ڼ��˲���
		int nowCurrent = (Integer) app.getMap()
				.get(Constants.CURRENT_SWIM_TIME);
		Plan p = dbManager.queryPlan(planID);
		long userid = (Long) app.getMap().get(Constants.CURRENT_USER_ID);

		List<Map<String, Object>> scoresJson = new ArrayList<Map<String, Object>>();
		List<Score> sl = new ArrayList<Score>();
		for (int i = 0; i < COUNT_MAX; i++) {
			Map<String, Object> scoreMap = new HashMap<String, Object>();
			Score s = new Score();
			String athName = tv_tips.get(i).getText().toString().substring(5);
			Athlete ath = dbManager.getAthleteByName(userid, athName);
			s.setDate(date);
			s.setTimes(nowCurrent);
			s.setScore(tv_times.get(i).getText().toString());
			s.setAthlete(ath);
			s.setP(p);
			sl.add(s);
			scoreMap.put("score", tv_times.get(i).getText().toString());
			scoreMap.put("date", date);
			scoreMap.put("times", nowCurrent);
			scoreMap.put("plan", p.getPid());
			scoreMap.put("athlete", ath.getAid());
			scoresJson.add(scoreMap);
		}

		Collections.sort(sl, new ScoreComparable());
		for (Score s : sl) {
			s.save();
		}
		XUtils.showToast(context, toast, "����ɹ���");

		// �����������״̬��������������
		boolean isConnect = (Boolean) app.getMap().get(
				Constants.IS_CONNECT_SERVICE);
		if (isConnect) {
			// ������������
			saveScoreRequest(JsonTools.creatJsonString(scoresJson));
		}

		int swimTime = ((Integer) app.getMap().get(Constants.SWIM_TIME)) - 1;
		if (swimTime != 0) {
			app.getMap().put(Constants.SWIM_TIME, swimTime);
			reset();
			swimTime = ((Integer) app.getMap().get(Constants.CURRENT_SWIM_TIME)) + 1;
			currentTimeTextView.setText("��" + swimTime + "�μ�ʱ");
			app.getMap().put(Constants.CURRENT_SWIM_TIME, swimTime);
		} else {
			Intent i = new Intent(this, ShowScoreActivity.class);
			i.putExtra(Constants.TEST_DATE,
					(String) app.getMap().get(Constants.TEST_DATE));
			i.putExtra("Plan", p.getName() + "--" + p.getPool());
			startActivity(i);
			app.getMap().put(Constants.SWIM_TIME, 1);
			app.getMap().put(Constants.ATHLETE_NUMBER, 0);
			app.getMap().put(Constants.TEST_DATE, "");
			((Activity) context).finish();
		}

	}

	/**
	 * �������汾�ּ�ʱ������
	 * 
	 * @param jsonString
	 */
	public void saveScoreRequest(final String jsonString) {

		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "addScores", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("addScores", response);
						if (response.equals("1")) {

						} else {

						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.i("addScores", "����ʧ��");
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// �����������
				Map<String, String> map = new HashMap<String, String>();
				map.put("scoresJson", jsonString);
				return map;
			}
		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	/**
	 * ��ʼ��ʱ
	 */
	private void startTimer() {
		if (isBegin) {
			timer = new Timer(true);
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
			XUtils.showToast(context, toast, "Ӿ�����ô�������ϸ�����ٿ�ʼ��ʱ");
		}

	}

	/**
	 * ���ü�ʱ��
	 */
	private void reset() {
		chooseAthletes.clear();
		for (int i = 1; i <= tv_times.size(); i++) {
			tv_times.get(i - 1).setText("");
			relativelayouts.get(i - 1).setClickable(true);
		}
		tv_clock.setText("00��00��000");
		rlStartCount.setClickable(true);
	}

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		timerStop();

	}

	/**
	 * �ɼ��Ƚ���
	 * 
	 * @author LittleByte
	 * 
	 */
	class ScoreComparable implements Comparator<Score> {

		@Override
		public int compare(Score lhs, Score rhs) {
			// TODO Auto-generated method stub
			Score score1 = lhs;
			Score score2 = rhs;
			int num = score1.getScore().compareTo(score2.getScore());
			if (num == 0)
				return (int) (score1.getId() - score2.getId());
			return num;
		}

	}
}
