package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.adapter.ViewPargerAdpt;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.fragment.EachTimeScoreFragment;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.Score;
import com.scnu.swimmingtrainingsystem.model.SmallPlan;
import com.scnu.swimmingtrainingsystem.model.SmallScore;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.ScreenUtils;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

public class EachTimeScoreActivity extends FragmentActivity {
	private MyApplication myApplication;
	private DBManager mDbManager;
	private HorizontalScrollView scrollView;
	private LinearLayout layout;
	private ViewPager viewPager;
	private ArrayList<Fragment> fragmentsList;
	private int idex = 0;
	private ArrayList<String> list;
	private Toast mToast;
	private String date;
	private Plan plan;
	private Long userID;
	private RequestQueue mQueue;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_each_score);
		try {
			init();
			InitViewPager();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}

	}

	private void init() {
		myApplication = (MyApplication) getApplication();
		mDbManager = DBManager.getInstance();
		mQueue = Volley.newRequestQueue(this);
		date = (String) myApplication.getMap().get(Constants.TEST_DATE);
		userID = (Long) myApplication.getMap().get(Constants.CURRENT_USER_ID);
		Long planId = (Long) myApplication.getMap().get(Constants.PLAN_ID);
		plan = DataSupport.find(Plan.class, planId);
		scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView_home);
		layout = (LinearLayout) findViewById(R.id.ll_shouyebiaoqian);
		viewPager = (ViewPager) findViewById(R.id.viewparger_home);
		scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		list = new ArrayList<String>();
		int swimTimes = (Integer) myApplication.getMap().get(
				Constants.CURRENT_SWIM_TIME);
		for (int i = 1; i <= swimTimes; i++) {
			String str = "第" + i + "趟计时";
			list.add(str);
		}

	}

	private void InitViewPager() {
		if (list.size() <= 1) {
			layout.setVisibility(View.GONE);
		}
		fragmentsList = new ArrayList<Fragment>();
		int width;
		// 这里设置标签最多显示四个
		if (list.size() > 4) {
			width = ScreenUtils.getScreenWidth(this) / 4;
		} else {
			width = ScreenUtils.getScreenWidth(this) / list.size();
		}

		for (int i = 0; i < list.size(); i++) {
			SharedPreferences sp = getSharedPreferences(Constants.LOGININFO,
					MODE_PRIVATE);
			int currentDistance = sp.getInt(Constants.CURRENT_DISTANCE
					+ (i + 1), 0);
			String scoresJsonString = sp.getString(Constants.SCORESJSON
					+ (i + 1), "");
			String namesJsonString = sp.getString(Constants.ATHLETEJSON
					+ (i + 1), "");

			EachTimeScoreFragment homeItemfragment = new EachTimeScoreFragment(
					i, currentDistance, scoresJsonString, namesJsonString);
			fragmentsList.add(homeItemfragment);
			LinearLayout linearLayout = new LinearLayout(this);
			linearLayout.setGravity(Gravity.CENTER);
			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(width,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			TextView tv = new TextView(this);
			tv.setText(list.get(i));
			tv.setTextSize(18);
			tv.setGravity(Gravity.CENTER);
			tv.setPadding(20, 10, 20, 10);
			tv.setTextColor(Color.parseColor("#bf242b2f"));
			tv.setLayoutParams(new LinearLayout.LayoutParams(170,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			tv.setSingleLine();
			linearLayout.setId(i);
			if (i == 0) {
				linearLayout.setBackgroundColor(Color.parseColor("#f1f1f1"));
			}
			linearLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					viewPager.setCurrentItem(arg0.getId());
				}
			});
			linearLayout.addView(tv);
			layout.addView(linearLayout);
		}
		viewPager.setAdapter(new ViewPargerAdpt(getSupportFragmentManager(),
				fragmentsList));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			int width = ScreenUtils.getScreenWidth(EachTimeScoreActivity.this) / 4;
			for (int i = 0; i < layout.getChildCount(); i++) {

				layout.getChildAt(i).setBackgroundColor(
						Color.parseColor("#ffffff"));
				if (arg0 == i) {
					layout.getChildAt(i).setBackgroundColor(
							Color.parseColor("#f1f1f1"));
				}
			}
			if (idex < arg0 && arg0 > 2) {
				scrollView.smoothScrollBy(width, 0);

			} else {
				if (fragmentsList.size() - arg0 > 3) {

					scrollView.smoothScrollBy(-width, 0);
				}
			}
			idex = arg0;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

	}

	private void createDialog() {
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		build.setTitle("系统提示").setMessage("是否放弃保存本轮成绩");
		build.setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		build.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				myApplication.getMap().put(Constants.COMPLETE_NUMBER, 0);
				finish();
			}
		}).show();

	}

	public void finishModify(View v) throws JSONException {
		int length = fragmentsList.size();
		int i = 0;
		for (i = 0; i < length; i++) {
			Map<String, Object> result = ((EachTimeScoreFragment) fragmentsList
					.get(i)).check();
			int number = (Integer) myApplication.getMap().get(
					Constants.COMPLETE_NUMBER);
			int resCode = (Integer) result.get("resCode");
			int position = (Integer) result.get("position");
			if (resCode == 1 && number != length) {
				viewPager.setCurrentItem(position);
				CommonUtils.showToast(this, mToast, "该趟成绩当前成绩距离为0！");
				break;
			} else if (resCode == 2 && number != length) {
				viewPager.setCurrentItem(position);
				CommonUtils.showToast(this, mToast, "成绩与运动员人数不对应！");
				break;
			} else if (resCode == 0 && number != length) {
				// 以下只是保存length-1次
				saveScore(i, result);
				viewPager.setCurrentItem(position + 1);
			} else if (resCode == 0 && number == length) {
				saveScore(i, result);
				viewPager.setCurrentItem(length - 1);
				CommonUtils.showToast(this, mToast, "匹配完成！");

				boolean isConnect = (Boolean) myApplication.getMap().get(
						Constants.IS_CONNECT_SERVER);
				if (isConnect) {
					// 如果可以联通服务器则发送添加成绩请求
					if (loadingDialog == null) {
						loadingDialog = LoadingDialog.createDialog(this);
						loadingDialog.setMessage("正在同步...");
						loadingDialog.setCanceledOnTouchOutside(false);
					}
					loadingDialog.show();
					addScoreRequest();
				} else {
					Intent intent = new Intent(this, ShowScoreActivity.class);
					startActivity(intent);
					finish();
				}
				break;
			}
		}

	}

	/**
	 * 保存成绩到数据库
	 * 
	 * @param i
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	private void saveScore(int i, Map<String, Object> result) {
		User user = mDbManager.getUser(userID);
		String curDistance = (String) result.get("distance");
		int distance = Integer.parseInt(curDistance);
		List<String> scoresList = (List<String>) result.get("scores");
		List<String> namesList = (List<String>) result.get("names");
		int scoresNumber = scoresList.size();
		for (int l = 0; l < scoresNumber; l++) {
			Score score = new Score();
			score.setDate(date);
			score.setType(Constants.NORMALSCORE);
			score.setTimes(i + 1);
			score.setDistance(distance);
			score.setP(plan);
			score.setScore(scoresList.get(l));
			Athlete athlete = mDbManager.getAthleteByName(userID,
					namesList.get(l));
			score.setAthlete(athlete);
			score.setUser(user);
			score.save();
		}
	}

	/**
	 * 创建保存本轮计时成绩的请求
	 * 
	 * @param jsonString
	 *            本轮所有成绩的json字符串
	 */
	private void addScoreRequest() {

		SmallPlan sp = new SmallPlan();
		sp.setDistance(plan.getDistance());
		sp.setPool(plan.getPool());
		sp.setExtra(plan.getExtra());

		List<SmallScore> smallScores = new ArrayList<SmallScore>();
		List<Score> scoresResult = mDbManager.getScoreByDate(date);
		for (Score s : scoresResult) {
			SmallScore smScore = new SmallScore();
			smScore.setScore(s.getScore());
			smScore.setDate(s.getDate());
			smScore.setDistance(s.getDistance());
			smScore.setType(s.getType());
			smScore.setTimes(s.getTimes());
			smallScores.add(smScore);
		}
		List<Integer> aidList = mDbManager.getAthlteAidInScoreByDate(date);
		User user = mDbManager.getUser(userID);
		Map<String, Object> scoreMap = new HashMap<String, Object>();
		scoreMap.put("score", smallScores);
		scoreMap.put("plan", sp);
		scoreMap.put("uid", user.getUid());
		scoreMap.put("athlete_id", aidList);
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
										EachTimeScoreActivity.this, mToast,
										"成功同步至服务器!");
							} else {
								CommonUtils.showToast(
										EachTimeScoreActivity.this, mToast,
										"同步失敗！");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent = new Intent(EachTimeScoreActivity.this,
								ShowScoreActivity.class);
						startActivity(intent);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			createDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
}
