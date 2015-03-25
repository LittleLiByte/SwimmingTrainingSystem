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
		// 设置数据源
		String[] autoStrings = new String[] { "25", "55", "75", "100", "125",
				"150", "175", "200", "225", "250", "275", "300" };
		acTextView = (AutoCompleteTextView) findViewById(R.id.match_act_current_distance);
		ArrayAdapter<String> tipsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoStrings);
		// 设置AutoCompleteTextView的Adapter
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
	 * 匹配完毕，可以进入下一趟计时或者进入本轮总计
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
		// 暂时保存到SharePreferences
		String scoresString = JsonTools.creatJsonString(scores);
		String athleteJson = JsonTools.creatJsonString(dragDatas);
		createDialog(this, nowCurrent, crrentDistance, scoresString,
				athleteJson);
	}

	/**
	 * 如果当前成绩数目与运动员人数相等,并且是该轮游泳只有一趟则直接保存到数据库
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
	 * 結束本轮计时并显示总的计时情况
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
				CommonUtils.showToast(this, toast, "请填写记录当前成绩的距离！");
				return;
			} else if (scoresNumber != athleteNumber) {
				CommonUtils.showToast(this, toast, "成绩数目与运动员数目不相等！");
				return;
			} else {
				i = new Intent(this, ShowScoreActivity.class);
				// 如果这是第一趟并且成绩数目与运动员数目相等，则直接保存到数据库
				matchSuccess(date, nowCurrent, crrentDistance);
			}
		} else {
			i = new Intent(this, EachTimeScoreActivity.class);
			// 如果这是第一趟并且成绩数目与运动员数目不相等,则先保存到sp中，统计再做调整
			String scoresString = JsonTools.creatJsonString(scores);
			String athleteJson = JsonTools.creatJsonString(dragDatas);
			CommonUtils.saveCurrentScoreAndAthlete(this, nowCurrent, crrentDistance,
					scoresString, athleteJson);
		}
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

	/**
	 * 创建成绩数目与运动员数目不同提示对话框
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
		build.setTitle("系统提示").setMessage(
				"是否开始下一趟计时？ \n选择【否】则返回调整成绩或者运动员数目,或者结束本轮计时\n选择【是】则直接开始下一趟计时");
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
				//暂时保存到SharePreferences
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

}
