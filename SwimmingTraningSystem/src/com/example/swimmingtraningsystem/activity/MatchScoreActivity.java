package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.adapter.DragListAdapter;
import com.example.swimmingtraningsystem.adapter.MatchAdapter;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.model.Score;
import com.example.swimmingtraningsystem.util.XUtils;
import com.example.swimmingtraningsystem.view.DragListView;

public class MatchScoreActivity extends Activity {

	private MyApplication app;
	private ListView listView;
	private DragListView dragListView;
	private MatchAdapter adapter;
	private String[] scores;

	private DragListAdapter dragAdapter;
	private RequestQueue mQueue;
	private List<ListView> viewList;
	private List<Long> athID;
	private List<Athlete> athletes;
	private List<String> dragDatas;
	private long planID;
	private Plan p;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matchscore);
		initView();

	}

	@SuppressWarnings("unchecked")
	private void initView() {
		// TODO Auto-generated method stub
		app = (MyApplication) getApplication();
		Intent result = getIntent();
		scores = result.getStringArrayExtra("SCORES");
		planID = (Long) app.getMap().get("planID");
		athID = (List<Long>) app.getMap().get("athIDList");
		dragDatas = (List<String>) app.getMap().get("dragList");
		int current = (Integer) app.getMap().get("current");
		athletes = DBManager.getInstance().getAthletes(athID);
		mQueue = Volley.newRequestQueue(getApplicationContext());
		listView = (ListView) findViewById(R.id.matchscore_list);
		dragListView = (DragListView) findViewById(R.id.matchName_list);
		viewList = new ArrayList<ListView>();
		viewList.add(listView);
		viewList.add(dragListView);
		MyScrollListener mListener = new MyScrollListener();
		listView.setOnScrollListener(mListener);
		dragListView.setOnScrollListener(mListener);
		adapter = new MatchAdapter(this, scores);

		if (current == 1) {
			dragAdapter = new DragListAdapter(this, athletes);
		} else {
			dragAdapter = new DragListAdapter(this, DBManager.getInstance()
					.getAthleteByNames(dragDatas));
		}
		listView.setAdapter(adapter);
		dragListView.setAdapter(dragAdapter);
	}

	public void matchDone(View v) {
		String date = (String) app.getMap().get("testDate");
		List<Athlete> athletes = dragAdapter.getList();
		int nowCurrent = (Integer) app.getMap().get("current");
		p = DBManager.getInstance().queryPlan(planID);
		List<String> names = new ArrayList<String>();

		List<Score> sLists = new ArrayList<Score>();
		for (int i = 0; i < scores.length; i++) {
			Athlete a = athletes.get(i);
			names.add(a.getName());
			Score s = new Score();
			s.setDate(date);
			s.setTimes(nowCurrent);
			s.setScore(scores[i]);
			s.setAthlete(a);
			s.setP(p);
			sLists.add(s);
		}
		String js = JsonTools.creatJsonString(sLists);
		for (Score s : sLists) {
			s.save();
		}
		XUtils.showToast(this, toast, "����ɹ���");
		// ������������
		createNewRequest(js);
		int swimTime = ((Integer) app.getMap().get("swimTime")) - 1;
		if (swimTime != 0) {
			app.getMap().put("swimTime", swimTime);
			app.getMap().put("dragList", names);
			Intent i = new Intent(this, TimerActivity.class);
			startActivity(i);
		} else {
			Intent i = new Intent(this, ShowScoreActivity.class);
			i.putExtra("testDate", (String) app.getMap().get("testDate"));
			i.putExtra("Plan", p.getName() + "--" + p.getPool());
			startActivity(i);
			app.getMap().put("dragList", null);
			app.getMap().put("swimTime", 1);
			app.getMap().put("athleteCount", 0);
			app.getMap().put("testDate", "");
		}
		finish();
	}

	public void matchBack(View v) {
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private class MyScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			// �ؼ�����
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

			// �ؼ�����
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

	public void createNewRequest(final String jsonString) {

		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "addScores", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("addScores", response);
						if (response.equals("ok")) {

						} else {
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.e("addScores", error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// �����������
				Map<String, String> map = new HashMap<String, String>();
				map.put("scoresJson", jsonString);
				return map;
			}

			@Override
			public RetryPolicy getRetryPolicy() {
				// TODO Auto-generated method stub
				// ��ʱ����
				RetryPolicy retryPolicy = new DefaultRetryPolicy(
						XUtils.SOCKET_TIMEOUT,
						DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
				return retryPolicy;
			}
		};

		mQueue.add(stringRequest);
	}

}
