package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
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
import com.scnu.swimmingtrainingsystem.adapter.NameListAdapter;
import com.scnu.swimmingtrainingsystem.adapter.ScoreListAdapter;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Score;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;
import com.scnu.swimmingtrainingsystem.view.DragListView;
import com.scnu.swimmingtrainingsystem.view.SlideCutListView;
import com.scnu.swimmingtrainingsystem.view.SlideCutListView.RemoveDirection;
import com.scnu.swimmingtrainingsystem.view.SlideCutListView.RemoveListener;

public class MatchScoreActivity extends Activity implements RemoveListener{

	private MyApplication app;
	private SlideCutListView scoreListView;
	private DragListView nameListView;
	private ScoreListAdapter adapter;
	private ArrayList<String> scores = new ArrayList<String>();
	private NameListAdapter dragAdapter;
	private RequestQueue mQueue;
	private List<ListView> viewList;
	private List<String> dragDatas;

	private Toast toast;
	private DBManager mDbManager;
	private User mUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matchscore);
		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	@SuppressWarnings("unchecked")
	private void init() {
		// TODO Auto-generated method stub
		app = (MyApplication) getApplication();
		mDbManager = DBManager.getInstance();
		mQueue = Volley.newRequestQueue(getApplicationContext());
		Long userID = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		mUser = mDbManager.getUser(userID);
		Intent result = getIntent();
		scores = result.getStringArrayListExtra("SCORES");
		scoreListView = (SlideCutListView) findViewById(R.id.matchscore_list);
		nameListView = (DragListView) findViewById(R.id.matchName_list);
		dragDatas = (List<String>) app.getMap().get(Constants.DRAG_NAME_LIST);
		int current = (Integer) app.getMap().get(Constants.CURRENT_SWIM_TIME);

		viewList = new ArrayList<ListView>();
		viewList.add(scoreListView);
		viewList.add(nameListView);
		MyScrollListener mListener = new MyScrollListener();
		scoreListView.setOnScrollListener(mListener);
		nameListView.setOnScrollListener(mListener);
		scoreListView.setRemoveListener(this);
		adapter = new ScoreListAdapter(this, scores);

		if (current == 1) {
			dragAdapter = new NameListAdapter(this, dragDatas);
		} else {
			dragAdapter = new NameListAdapter(this, dragDatas);
		}
		scoreListView.setAdapter(adapter);
		nameListView.setAdapter(dragAdapter);
	}

	/**
	 * 点击成绩保存事件
	 * 
	 * @param v
	 */
	public void matchDone(View v) {
		String date = (String) app.getMap().get(Constants.TEST_DATE);
		List<Athlete> athletes = mDbManager.getAthleteByNames(dragAdapter
				.getList());
		int nowCurrent = (Integer) app.getMap()
				.get(Constants.CURRENT_SWIM_TIME);
		// p = DBManager.getInstance().queryPlan(planID);
		List<String> names = new ArrayList<String>();

		List<Map<String, Object>> scoresJson = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", mUser.getUid());
		for (int i = 0; i < scores.size(); i++) {
			Map<String, Object> scoreMap = new HashMap<String, Object>();
			Athlete a = athletes.get(i);
			names.add(a.getName());
			Score s = new Score();
			s.setDate(date);
			s.setTimes(nowCurrent);
			s.setScore(scores.get(i));
			s.setAthlete(a);
			// s.setP(p);
			s.save();

			scoreMap.put("score", scores.get(i));
			scoreMap.put("up_time", date);
			scoreMap.put("times", nowCurrent);
			// scoreMap.put("plan_id", p.getPid());
			scoreMap.put("athlete_id", a.getAid());
			scoresJson.add(scoreMap);
		}
		map.put("scoreList", scoresJson);

		// 如果处在联网状态，则发送至服务器
		boolean isConnect = (Boolean) app.getMap().get(
				Constants.IS_CONNECT_SERVICE);
		if (isConnect) {
			// 发送至服务器
			addScoreRequest(JsonTools.creatJsonString(map));
		}

		int swimTime = ((Integer) app.getMap().get(Constants.SWIM_TIME)) - 1;
		if (swimTime != 0) {
			app.getMap().put(Constants.SWIM_TIME, swimTime);
			app.getMap().put(Constants.DRAG_NAME_LIST, names);
			Intent i = new Intent(this, TimerActivity.class);
			startActivity(i);
		}

	}

	/**
	 * 結束本轮计时并显示总的计时情况
	 * 
	 * @param v
	 */
	public void finishTiming(View v) {
		app.getMap().put(Constants.DRAG_NAME_LIST, null);
		app.getMap().put(Constants.TEST_DATE, "");
		Intent i = new Intent(this, ShowScoreActivity.class);
		startActivity(i);
		finish();
	}

	/**
	 * 退出当前窗体事件
	 * 
	 * @param v
	 */
	public void matchBack(View v) {
		app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		finish();
	}

	private class MyScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			// 关键代码
			View subView = view.getChildAt(0);
			if (subView != null) {
				final int top = subView.getTop();
				for (ListView item : viewList) {
					item.setSelectionFromTop(firstVisibleItem, top);
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// 关键代码
			if (scrollState == SCROLL_STATE_IDLE
					|| scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				View subView = view.getChildAt(0);
				if (subView != null) {
					final int top = subView.getTop();
					final int position = view.getFirstVisiblePosition();
					for (ListView item : viewList) {
						item.setSelectionFromTop(position, top);
					}
				}
			}
		}

	}

	/**
	 * 创建保存本轮计时成绩的请求
	 * 
	 * @param jsonString
	 *            本轮所有成绩的json字符串
	 */
	public void addScoreRequest(final String jsonString) {

		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "addScores", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("addScores", response);
						JSONObject obj;
						try {
							obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								XUtils.showToast(MatchScoreActivity.this,
										toast, "保存成功！");
							} else {
								XUtils.showToast(MatchScoreActivity.this,
										toast, "服务器错误！请重新计时！");
							}
							finish();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

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
		// 进入计时界面却不进行成绩匹配而直接返回,要将当前第几次计时置0
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void removeItem(RemoveDirection direction, int position) {
		// TODO Auto-generated method stub
		// 至少要保留一个成绩，否则本次计时无意义
		if (adapter.getCount() > 1) {
			scores.remove(position);
			adapter.notifyDataSetChanged();
		} else {
			XUtils.showToast(this, toast, "至少要保留一个成绩");
		}

	}

}
