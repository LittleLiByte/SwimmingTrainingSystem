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
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
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
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;
import com.scnu.swimmingtrainingsystem.view.SlideCutListView;
import com.scnu.swimmingtrainingsystem.view.SlideCutListView.RemoveDirection;
import com.scnu.swimmingtrainingsystem.view.SlideCutListView.RemoveListener;

public class MatchScoreActivity extends Activity implements RemoveListener {

	private MyApplication app;
	private SlideCutListView scoreListView;
	private DragSortListView nameListView;
	private ScoreListAdapter adapter;
	private ArrayList<String> scores = new ArrayList<String>();
	private ArrayAdapter<String> dragAdapter;
	private RequestQueue mQueue;
	private List<ListView> viewList;
	private List<String> dragDatas;
	private AutoCompleteTextView acTextView;
	private Toast toast;
	private DBManager mDbManager;

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
			dragAdapter.remove(dragAdapter.getItem(which));
		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) adapter.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
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
		mQueue = Volley.newRequestQueue(getApplicationContext());
		@SuppressWarnings("unused")
		Long userID = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		Intent result = getIntent();
		scores = result.getStringArrayListExtra("SCORES");
		scoreListView = (SlideCutListView) findViewById(R.id.matchscore_list);
		nameListView = (DragSortListView) findViewById(R.id.matchName_list);
		nameListView.setDropListener(onDrop);
		nameListView.setRemoveListener(onRemove);
		nameListView.setDragScrollProfile(ssProfile);

		// ��������Դ
		String[] autoStrings = new String[] { "25", "55", "75", "100", "125",
				"150", "175", "200", "225", "250", "275", "300" };
		acTextView = (AutoCompleteTextView) findViewById(R.id.act_current_distance);
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
		scoreListView.setRemoveListener(this);
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
		System.out.println("scores--->" + scores.toString());
		System.out.println("dragDatas--->" + dragDatas.toString());
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
		List<Athlete> athletes = mDbManager.getAthleteByNames(dragDatas);
		Plan p = DataSupport.find(Plan.class,
				(Long) app.getMap().get(Constants.PLAN_ID));
		for (int i = 0; i < scores.size(); i++) {
			Athlete a = athletes.get(i);
			Score s = new Score();
			s.setDate(date);
			s.setTimes(nowCurrent);
			s.setScore(scores.get(i));
			s.setAthlete(a);
			s.setDistance(distance);
			s.setP(p);
			s.save();

		}
	}

	/**
	 * �Y�����ּ�ʱ����ʾ�ܵļ�ʱ���
	 * 
	 * @param v
	 */
	public void finishTiming(View v) {
		Intent i = new Intent(this, ShowScoreActivity.class);
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

		if (nowCurrent == 1 && scoresNumber == athleteNumber) {
			// ������ǵ�һ�˲��ҳɼ���Ŀ���˶�Ա��Ŀ��ȣ���ֱ�ӱ��浽���ݿ�
			matchSuccess(date, nowCurrent, crrentDistance);
		} else if (nowCurrent == 1 && scoresNumber != athleteNumber) {
			// ������ǵ�һ�˲��ҳɼ���Ŀ���˶�Ա��Ŀ�����,���ȱ��浽sp�У�ͳ����������
			String scoresString = JsonTools.creatJsonString(scores);
			String athleteJson = JsonTools.creatJsonString(dragDatas);
			XUtils.saveCurrentScoreAndAthlete(this, 1, crrentDistance,
					scoresString, athleteJson);
			i.putExtra("isComplete", false);
		} else {

		}
		startActivity(i);
		finish();
	}

	/**
	 * �˳���ǰ�����¼�
	 * 
	 * @param v
	 */
	public void matchBack(View v) {
		app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		finish();
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
		build.setTitle("�ɼ���Ŀ���˶�Ա��Ŀ��ͬ!").setMessage(
				"�Ƿ�ʼ��һ�˼�ʱ�� \nѡ�񡾷��򷵻ص����ɼ������˶�Ա��Ŀ\nѡ���ǡ���ֱ�ӿ�ʼ��һ�˼�ʱ��ͳ��ҳ����������");
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
				XUtils.saveCurrentScoreAndAthlete(context, i, crrentDistance,
						scoreString, athleteString);
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

	/**
	 * �������汾�ּ�ʱ�ɼ�������
	 * 
	 * @param jsonString
	 *            �������гɼ���json�ַ���
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
										toast, "����ɹ���");
							} else {
								XUtils.showToast(MatchScoreActivity.this,
										toast, "���������������¼�ʱ��");
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

	@Override
	public void removeItem(RemoveDirection direction, int position) {
		// TODO Auto-generated method stub
		// ����Ҫ����һ���ɼ������򱾴μ�ʱ������
		if (adapter.getCount() > 1) {
			scores.remove(position);
			adapter.notifyDataSetChanged();
		} else {
			XUtils.showToast(this, toast, "����Ҫ����һ���ɼ�");
		}

	}

}
