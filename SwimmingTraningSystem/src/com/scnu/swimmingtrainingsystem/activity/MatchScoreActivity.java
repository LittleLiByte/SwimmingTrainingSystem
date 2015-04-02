package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
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
import com.mobeta.android.dslv.DragSortListView;
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.adapter.ScoreListAdapter;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.Score;
import com.scnu.swimmingtrainingsystem.model.SmallPlan;
import com.scnu.swimmingtrainingsystem.model.SmallScore;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

public class MatchScoreActivity extends Activity {

	private MyApplication app;
	private DragSortListView scoreListView;
	private DragSortListView nameListView;
	private ScoreListAdapter adapter;
	private ArrayList<String> scores = new ArrayList<String>();
	private ArrayAdapter<String> dragAdapter;
	private List<ListView> viewList;
	private List<String> dragDatas;
	private AutoCompleteTextView acTextView;
	private DBManager mDbManager;
	private boolean isConnected;
	private Long userId;
	private Plan plan;
	private LoadingDialog loadingDialog;
	private RequestQueue mQueue;
	private Toast mToast;
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item = dragAdapter.getItem(from);

			dragAdapter.notifyDataSetChanged();
			dragAdapter.remove(item);
			dragAdapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			if (dragDatas.size() > 1) {
				dragAdapter.remove(dragAdapter.getItem(which));
			} else {
				CommonUtils.showToast(MatchScoreActivity.this, mToast,
						"����Ҫ����һ���˶�Ա");
			}
			dragAdapter.notifyDataSetChanged();
		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) dragAdapter.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};

	private DragSortListView.RemoveListener onRemove2 = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			if (scores.size() > 1) {
				scores.remove(which);
			} else {
				CommonUtils.showToast(MatchScoreActivity.this, mToast,
						"����Ҫ����һ���ɼ�");
			}
			adapter.notifyDataSetChanged();
		}
	};

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
		isConnected = (Boolean) app.getMap().get(Constants.IS_CONNECT_SERVER);
		mQueue = Volley.newRequestQueue(this);
		Intent result = getIntent();
		scores = result.getStringArrayListExtra("SCORES");

		scoreListView = (DragSortListView) findViewById(R.id.matchscore_list);
		nameListView = (DragSortListView) findViewById(R.id.matchName_list);
		nameListView.setDropListener(onDrop);
		nameListView.setRemoveListener(onRemove);
		nameListView.setDragScrollProfile(ssProfile);
		scoreListView.setRemoveListener(onRemove2);
		userId = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		Long planId = (Long) app.getMap().get(Constants.PLAN_ID);
		plan = DataSupport.find(Plan.class, planId);
		// ��������Դ
		String[] autoStrings = new String[] { "25", "50", "75", "100", "125",
				"150", "175", "200", "225", "250", "275", "300" };
		acTextView = (AutoCompleteTextView) findViewById(R.id.match_act_current_distance);
		ArrayAdapter<String> tipsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoStrings);
		// ����AutoCompleteTextView��Adapter
		acTextView.setAdapter(tipsAdapter);
		acTextView.setDropDownHeight(350);
		acTextView.setThreshold(1);

		dragDatas = (List<String>) app.getMap().get(Constants.DRAG_NAME_LIST);
		viewList = new ArrayList<ListView>();
		viewList.add(scoreListView);
		viewList.add(nameListView);
		MyScrollListener mListener = new MyScrollListener();
		scoreListView.setOnScrollListener(mListener);
		nameListView.setOnScrollListener(mListener);
		adapter = new ScoreListAdapter(this, scores);

		dragAdapter = new ArrayAdapter<String>(this, R.layout.drag_list_item,
				R.id.drag_list_item_text, dragDatas);
		scoreListView.setAdapter(adapter);
		nameListView.setAdapter(dragAdapter);
	}

	/**
	 * ƥ����ϣ����Խ�����һ�˼�ʱ���߽��뱾���ܼ�
	 * 
	 * @param v
	 */
	public void matchDone(View v) {
		int nowCurrent = (Integer) app.getMap()
				.get(Constants.CURRENT_SWIM_TIME);
		String actv = acTextView.getText().toString().trim();
		int crrentDistance = 0;
		if (!TextUtils.isEmpty(actv)) {
			crrentDistance = Integer.parseInt(acTextView.getText().toString()
					.trim());
		}
		// ��ʱ���浽SharePreferences
		String scoresString = JsonTools.creatJsonString(scores);
		String athleteJson = JsonTools.creatJsonString(dragDatas);
		createDialog(this, nowCurrent, crrentDistance, scoresString,
				athleteJson);
	}

	/**
	 * �����ǰ�ɼ���Ŀ���˶�Ա�������,�����Ǹ�����Ӿֻ��һ����ֱ�ӱ��浽���ݿ�
	 * 
	 * @param date
	 * @param nowCurrent
	 * @param distance
	 */
	private void matchSuccess(String date, int nowCurrent, int distance) {
		User user = mDbManager.getUser(userId);
		List<Athlete> athletes = mDbManager.getAthleteByNames(dragDatas);
		for (int i = 0; i < scores.size(); i++) {
			Athlete a = athletes.get(i);
			Score s = new Score();
			s.setDate(date);
			s.setTimes(nowCurrent);
			s.setScore(scores.get(i));
			s.setType(Constants.NORMALSCORE);
			s.setAthlete(a);
			s.setDistance(distance);
			s.setP(plan);
			s.setUser(user);
			s.save();
		}
		if (isConnected) {
			if (loadingDialog == null) {
				loadingDialog = LoadingDialog.createDialog(this);
				loadingDialog.setMessage("�����ύ...");
				loadingDialog.setCanceledOnTouchOutside(false);
			}
			loadingDialog.show();
			addScoreRequest(date);
		} else {
			Intent intent = new Intent(MatchScoreActivity.this,
					ShowScoreActivity.class);
			startActivity(intent);
			finish();
		}

	}

	/**
	 * �Y�����ּ�ʱ����ʾ�ܵļ�ʱ���
	 * 
	 * @param v
	 */
	public void finishTiming(View v) {

		String date = (String) app.getMap().get(Constants.TEST_DATE);
		String actv = acTextView.getText().toString().trim();
		int crrentDistance = 0;
		if (!TextUtils.isEmpty(actv)) {
			crrentDistance = Integer.parseInt(acTextView.getText().toString()
					.trim());
		}
		int nowCurrent = (Integer) app.getMap()
				.get(Constants.CURRENT_SWIM_TIME);
		app.getMap().put(Constants.DRAG_NAME_LIST, null);
		int scoresNumber = adapter.getCount();
		int athleteNumber = dragAdapter.getCount();

		if (nowCurrent == 1) {
			if (crrentDistance == 0 && TextUtils.isEmpty(actv)) {
				CommonUtils.showToast(this, mToast, "����д��¼��ǰ�ɼ��ľ��룡");
				return;
			} else if (scoresNumber != athleteNumber) {
				CommonUtils.showToast(this, mToast, "�ɼ���Ŀ���˶�Ա��Ŀ����ȣ�");
				return;
			} else {
				// ������ǵ�һ�˲��ҳɼ���Ŀ���˶�Ա��Ŀ��ȣ���ֱ�ӱ��浽���ݿ�
				matchSuccess(date, nowCurrent, crrentDistance);
			}
		} else {
			Intent i = new Intent(this, EachTimeScoreActivity.class);
			// ������ǵ�һ�˲��ҳɼ���Ŀ���˶�Ա��Ŀ�����,���ȱ��浽sp�У�ͳ����������
			String scoresString = JsonTools.creatJsonString(scores);
			String athleteJson = JsonTools.creatJsonString(dragDatas);
			CommonUtils.saveCurrentScoreAndAthlete(this, nowCurrent,
					crrentDistance, scoresString, athleteJson);
			startActivity(i);
			finish();
		}

	}

	/**
	 * �˳���ǰ�����¼�
	 * 
	 * @param v
	 */
	public void matchBack(View v) {
		app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
	}

	/**
	 * �����ɼ���Ŀ���˶�Ա��Ŀ��ͬ��ʾ�Ի���
	 * 
	 * @param context
	 * @param i
	 * @param crrentDistance
	 * @param scoreString
	 * @param athleteString
	 */
	private void createDialog(final Context context, final int i,
			final int crrentDistance, final String scoreString,
			final String athleteString) {
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		build.setTitle("ϵͳ��ʾ").setMessage(
				"�Ƿ�ʼ��һ�˼�ʱ�� \nѡ�񡾷��򷵻ص����ɼ������˶�Ա��Ŀ,���߽������ּ�ʱ\nѡ���ǡ���ֱ�ӿ�ʼ��һ�˼�ʱ");
		build.setNegativeButton("��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		build.setPositiveButton("��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// ��ʱ���浽SharePreferences
				CommonUtils.saveCurrentScoreAndAthlete(context, i,
						crrentDistance, scoreString, athleteString);
				Intent intent = new Intent(MatchScoreActivity.this,
						TimerActivity.class);
				startActivity(intent);
				finish();
			}
		}).show();

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// �����ʱ����ȴ�����гɼ�ƥ���ֱ�ӷ���,Ҫ����ǰ�ڼ��μ�ʱ��0
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
			finish();
			overridePendingTransition(R.anim.slide_bottom_in,
					R.anim.slide_top_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void addScoreRequest(String date) {
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
		User user = mDbManager.getUser(userId);
		Map<String, Object> scoreMap = new HashMap<String, Object>();
		scoreMap.put("score", smallScores);
		scoreMap.put("plan", sp);
		scoreMap.put("uid", user.getUid());
		scoreMap.put("athlete_id", aidList);
		scoreMap.put("type", 1);
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
							int planId = (Integer) obj.get("plan_id");
							if (resCode == 1) {
								CommonUtils.showToast(MatchScoreActivity.this,
										mToast, "�ɹ�ͬ����������!");
								ContentValues values = new ContentValues();
								values.put("pid", planId);
								Plan.updateAll(Plan.class, values,
										String.valueOf(plan.getId()));
							} else {
								CommonUtils.showToast(MatchScoreActivity.this,
										mToast, "ͬ��ʧ����");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent = new Intent(MatchScoreActivity.this,
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

}
