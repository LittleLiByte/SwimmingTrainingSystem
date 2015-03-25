package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.adapter.ScoreListAdapter;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.Score;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;

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
				return ((float) dragAdapter.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};

	private DragSortListView.RemoveListener onRemove2 = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			scores.remove(which);
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
		Intent result = getIntent();
		scores = result.getStringArrayListExtra("SCORES");
		scoreListView = (DragSortListView) findViewById(R.id.matchscore_list);
		nameListView = (DragSortListView) findViewById(R.id.matchName_list);
		nameListView.setDropListener(onDrop);
		nameListView.setRemoveListener(onRemove);
		nameListView.setDragScrollProfile(ssProfile);
		scoreListView.setRemoveListener(onRemove2);
		// ��������Դ
		String[] autoStrings = new String[] { "25", "55", "75", "100", "125",
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
		Intent i = null;
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
				CommonUtils.showToast(this, toast, "����д��¼��ǰ�ɼ��ľ��룡");
				return;
			} else if (scoresNumber != athleteNumber) {
				CommonUtils.showToast(this, toast, "�ɼ���Ŀ���˶�Ա��Ŀ����ȣ�");
				return;
			} else {
				i = new Intent(this, ShowScoreActivity.class);
				// ������ǵ�һ�˲��ҳɼ���Ŀ���˶�Ա��Ŀ��ȣ���ֱ�ӱ��浽���ݿ�
				matchSuccess(date, nowCurrent, crrentDistance);
			}
		} else {
			i = new Intent(this, EachTimeScoreActivity.class);
			// ������ǵ�һ�˲��ҳɼ���Ŀ���˶�Ա��Ŀ�����,���ȱ��浽sp�У�ͳ����������
			String scoresString = JsonTools.creatJsonString(scores);
			String athleteJson = JsonTools.creatJsonString(dragDatas);
			CommonUtils.saveCurrentScoreAndAthlete(this, nowCurrent, crrentDistance,
					scoresString, athleteJson);
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
				//��ʱ���浽SharePreferences
				CommonUtils.saveCurrentScoreAndAthlete(context, i, crrentDistance,
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
