package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.model.Score;
import com.example.swimmingtraningsystem.model.Temp;
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.view.LoadingDialog;

/**
 * 计时完毕后展示成绩的Activity
 * 
 * @author LittleByte
 * 
 */
public class ShowScoreActivity extends Activity {
	private MyApplication mApplication;
	private DBManager mDbManager;
	private ExpandableListView mExpandableListView;
	private List<Temp> mScoreSum = new ArrayList<Temp>();
	private List<List<Score>> list = new ArrayList<List<Score>>();
	private TextView mPlanName;
	private LoadingDialog mLoadingDialog;
	private ScoreListAdapter adapter;
	private String date;
	private Integer times;
	private static final String GENERATING_THE_SCORES = "正在生成本次计时结果...";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showscore);
		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		mApplication = (MyApplication) getApplication();
		mDbManager = DBManager.getInstance();
		mExpandableListView = (ExpandableListView) findViewById(R.id.show_list);
		mPlanName = (TextView) findViewById(R.id.show_the_plan);
		times = (Integer) mApplication.getMap()
				.get(Constants.CURRENT_SWIM_TIME) + 1;
		date = getIntent().getStringExtra(Constants.TEST_DATE);
		String planString = getIntent().getStringExtra("Plan");
		mPlanName.setText(planString);

		adapter = new ScoreListAdapter(this, list, mScoreSum, 0);
		mExpandableListView.setAdapter(adapter);
		// 启动查询异步任务
		new QueryScoreTask().execute(date);
		// 屏蔽收缩
		mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
	}

	public void showBack(View v) {
		finish();
	}

	/**
	 * 数据库查询异步任务，防止ANR
	 * 
	 * @author LittleByte
	 * 
	 */
	class QueryScoreTask extends AsyncTask<String, Void, TempScore> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (mLoadingDialog == null) {
				mLoadingDialog = LoadingDialog
						.createDialog(ShowScoreActivity.this);
				mLoadingDialog.setMessage(GENERATING_THE_SCORES);
				mLoadingDialog.setCanceledOnTouchOutside(false);
			}
			mLoadingDialog.show();
		}

		@Override
		protected TempScore doInBackground(String... params) {
			// TODO Auto-generated method stub
			TempScore tempScore = new TempScore();
			List<Score> athScores = mDbManager
					.getAthleteNumberInScoreByDate(params[0]);
			List<Long> athIds = new ArrayList<Long>();
			for (Score s : athScores) {
				athIds.add(s.getAthlete().getId());
			}
			List<Temp> totalScores = mDbManager.getAthleteIdInScoreByDate(date,
					athIds);
			List<List<Score>> lists = new ArrayList<List<Score>>();
			for (int i = 1; i < times; i++) {
				List<Score> ls = mDbManager.getScoreByDateAndTimes(date, i);
				lists.add(ls);
			}
			tempScore.setScoresList(lists);
			tempScore.setTemps(totalScores);
			tempScore.setSwimTime(times);
			return tempScore;
		}

		@Override
		protected void onPostExecute(TempScore result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter.setDatas(result.getScoresList(), result.getTemps(),
					result.getSwimTime());
			adapter.notifyDataSetChanged();
			// 默认展开
			for (int i = 0; i < adapter.getGroupCount(); i++) {
				mExpandableListView.expandGroup(i);
			}
			mLoadingDialog.dismiss();

		}
	}

	class TempScore {
		private List<List<Score>> scoresList = new ArrayList<List<Score>>();
		List<Temp> temps = new ArrayList<Temp>();
		private int swimTime;

		public List<List<Score>> getScoresList() {
			return scoresList;
		}

		public void setScoresList(List<List<Score>> scoresList) {
			this.scoresList = scoresList;
		}

		public List<Temp> getTemps() {
			return temps;
		}

		public void setTemps(List<Temp> temps) {
			this.temps = temps;
		}

		public int getSwimTime() {
			return swimTime;
		}

		public void setSwimTime(int swimTime) {
			this.swimTime = swimTime;
		}

	}

	class ScoreListAdapter extends BaseExpandableListAdapter {
		private Context mContext;
		private List<List<Score>> mLists = new ArrayList<List<Score>>();
		private List<Temp> mTemps = new ArrayList<Temp>();
		private int mSwimTime = 0;

		public ScoreListAdapter(Context mContext, List<List<Score>> mLists,
				List<Temp> mTemps, int mSwimTime) {
			this.mContext = mContext;
			this.mLists = mLists;
			this.mTemps = mTemps;
			this.mSwimTime = mSwimTime;
		}

		public void setDatas(List<List<Score>> mLists, List<Temp> mTemps,
				int mSwimTime) {
			this.mLists.clear();
			this.mTemps.clear();
			this.mLists = mLists;
			this.mTemps = mTemps;
			this.mSwimTime = mSwimTime;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mLists.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = View.inflate(mContext,
					R.layout.show_score_list_item_sub, null);
			TextView tv1 = (TextView) convertView.findViewById(R.id.show_rank);
			TextView tv2 = (TextView) convertView.findViewById(R.id.show_score);
			TextView tv3 = (TextView) convertView.findViewById(R.id.show_name);
			tv1.setText("第" + (childPosition + 1) + "名");
			if (groupPosition < mSwimTime - 1) {
				Score s = mLists.get(groupPosition).get(childPosition);
				tv2.setText(s.getScore());
				tv3.setText(mDbManager.getAthleteNameByScoreID(s.getId()));
			} else {
				tv2.setText(mTemps.get(childPosition).getScore());
				tv3.setText(mTemps.get(childPosition).getAthleteName());
			}

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (mLists.size() == 0) {
				return 0;
			}
			return mLists.get(0).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getGroupCount() {
			return mSwimTime;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = View.inflate(mContext,
					R.layout.show_score_list_item_head, null);
			TextView tv1 = (TextView) convertView.findViewById(R.id.show_times);
			if (groupPosition < getGroupCount() - 1) {
				tv1.setText("第" + (groupPosition + 1) + "趟");
			} else {
				tv1.setText("成绩总计");
			}
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mApplication.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		mApplication.getMap().put(Constants.SWIM_TIME, 0);
		mApplication.getMap().put(Constants.PLAN_ID, 0);
	}

}
