package com.scnu.swimmingtrainingsystem.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Score;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

/**
 * 其他功能界面
 * 
 * @author LittleByte
 * 
 */
@SuppressLint("SimpleDateFormat")
public class OtherFunctionActivity extends Activity implements OnClickListener {
	private MyApplication app;
	private DBManager mDbManager;
	// Volley请求队列
	private RequestQueue mQueue;
	// 点击表盘次数
	private int clickCount = 0;
	// 经过毫秒数
	private long mlCount = 0;
	// 秒表显示时间
	private TextView tvTime;
	private Spinner fuctionSpinner;
	private Spinner athleteSpinner;
	private Button resetButton;
	private Button saveScoreButton;

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
	private List<Athlete> athletes;
	private Long userID;
	private Toast mToast;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_function);
		try {
			initView();
			initData();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	private void initView() {
		app = (MyApplication) getApplication();
		mDbManager = DBManager.getInstance();
		mQueue = Volley.newRequestQueue(this);
		// 如果app中的全局变量被系统强制回收，通过以下改行代码会触发异常，直接将应用界面重启至登陆页面
		userID = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		tvTime = (TextView) findViewById(R.id.duocitvTime);
		resetButton = (Button) findViewById(R.id.fuction_reset);
		resetButton.setOnClickListener(this);
		saveScoreButton = (Button) findViewById(R.id.fuction_save);
		saveScoreButton.setOnClickListener(this);
		// ----------------两个spinner相关--------------
		fuctionSpinner = (Spinner) findViewById(R.id.spn_function);
		athleteSpinner = (Spinner) findViewById(R.id.spn_athlete);
		List<String> fuctionList = new ArrayList<String>();
		fuctionList.add("三次计频");
		fuctionList.add("冲刺计时");
		ArrayAdapter<String> fuctionAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, fuctionList);
		fuctionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fuctionSpinner.setAdapter(fuctionAdapter);
		List<String> athleteNames = new ArrayList<String>();
		athletes = mDbManager.getAthletes(userID);
		for (Athlete ath : athletes) {
			athleteNames.add(ath.getName());
		}
		ArrayAdapter<String> athleteAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, athleteNames);
		athleteSpinner.setAdapter(athleteAdapter);

		// ----------------计时器动画相关--------------
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
		clockView = (RelativeLayout) findViewById(R.id.function_clock_area);

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
					tvTime.setTextSize(24);
					tvTime.setTextColor(getResources().getColor(R.color.black));
					int select = fuctionSpinner.getSelectedItemPosition();
					if (select == 0) {
						float timePermsec = 3 * 60000 / (float) mlCount;
						DecimalFormat decimalFormat = new DecimalFormat(".00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
						String p = decimalFormat.format(timePermsec);
						tvTime.setText(p + "次/分钟");
					} else {
						tvTime.setText(strTime_count);
					}

				}
			}
		});
	}

	@SuppressLint("HandlerLeak")
	private void initData() {
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

	private void resetTimer() {
		clickCount = 0;
		predegree = 0;
		secondpredegree = 0;
		hourpredegree = 0;
		mlCount = 0;
		timerStop();
		initData();
		setAnimation();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fuction_reset:
			resetTimer();
			break;
		case R.id.fuction_save:
			saveScore();
			break;
		default:
			break;
		}
	}

	private void saveScore() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());
		Athlete athlete = athletes
				.get(athleteSpinner.getSelectedItemPosition());
		Score s = new Score();
		s.setScore(tvTime.getText().toString());
		s.setDate(date);
		s.setDistance(0);
		s.setTimes(1);
		s.setType(fuctionSpinner.getSelectedItemPosition() + 2);
		s.setAthlete(athlete);
		s.setUser(mDbManager.getUser(userID));
		s.save();
		boolean isConnect = (Boolean) app.getMap().get(
				Constants.IS_CONNECT_SERVER);
		if (isConnect) {
			int aid = athlete.getAid();
			if (loadingDialog == null) {
				loadingDialog = LoadingDialog.createDialog(this);
				loadingDialog.setMessage("正在提交到服务器...");
				loadingDialog.setCanceledOnTouchOutside(false);
			}
			loadingDialog.show();
			addScoreRequest(aid);
		}
	}

	private void addScoreRequest(final int aid) {
		List<Score> scoresResult = new ArrayList<Score>();
		scoresResult.add(DataSupport.findLast(Score.class));
		User user = mDbManager.getUser(userID);
		Map<String, Object> scoreMap = new HashMap<String, Object>();
		scoreMap.put("score", scoresResult);
		scoreMap.put("plan", null);
		scoreMap.put("uid", user.getUid());
		scoreMap.put("athlete_id", aid);
		final String jsonString = JsonTools.creatJsonString(scoreMap);
		StringRequest stringRequest = new StringRequest(Method.POST,
				CommonUtils.HOSTURL + "addScores", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("addScores", response);
						loadingDialog.dismiss();
						JSONObject obj;
						try {
							obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								CommonUtils.showToast(
										OtherFunctionActivity.this, mToast,
										"成功同步至服务器!");
							} else {
								CommonUtils.showToast(
										OtherFunctionActivity.this, mToast,
										"同步失敗！");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finish();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// Log.e("addScores", error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
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

	public void othersGetBack(View v) {
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.slide_bottom_in,
					R.anim.slide_top_out);
			return false;
		}
		return false;
	}
}
