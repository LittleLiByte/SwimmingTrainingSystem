package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.Score;
import com.scnu.swimmingtrainingsystem.model.Temp;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

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
	// private List<Temp> mScoreSum = new ArrayList<Temp>();
	// private List<List<Score>> list = new ArrayList<List<Score>>();
	private TextView mPlanName;
	private LoadingDialog mLoadingDialog;
	private ShowScoreListAdapter adapter;
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
			e.printStackTrace();
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
		date = (String) mApplication.getMap().get(Constants.TEST_DATE);
		Long planid = (Long) mApplication.getMap().get(Constants.PLAN_ID);
		Plan plan = DataSupport.find(Plan.class, planid);
		mPlanName.setText(plan.getPool() + " 总距离：" + plan.getDistance() + " 共"
				+ (times - 1) + "趟");

		boolean isComplete = getIntent().getBooleanExtra("isComplete", true);
		if (times == 2 && isComplete) {
			TempScoreList tempScoreList = generateScoreList(date);
			ShowScoreListAdapter adapter = new ShowScoreListAdapter(this,
					tempScoreList.getScoresList(), tempScoreList.getTemps(), 2);
			mExpandableListView.setAdapter(adapter);
		} else if (times == 2 && !isComplete) {

		} else {
			// 启动查询异步任务
			new QueryScoreTask().execute(date);
		}
		// 默认展开
		for (int i = 0; i < times; i++) {
			mExpandableListView.expandGroup(i);
		}
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
	class QueryScoreTask extends AsyncTask<String, Void, TempScoreList> {

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
		protected TempScoreList doInBackground(String... params) {
			// TODO Auto-generated method stub
			return generateScoreList(params);
		}

		@Override
		protected void onPostExecute(TempScoreList result) {
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

	private TempScoreList generateScoreList(String... params) {
		TempScoreList tempScore = new TempScoreList();
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

	class TempScoreList {
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

	class ShowScoreListAdapter extends BaseExpandableListAdapter {
		private Context mContext;
		private List<List<Score>> mLists = new ArrayList<List<Score>>();
		private List<Temp> mTemps = new ArrayList<Temp>();
		private int mSwimTime = 0;

		public ShowScoreListAdapter(Context mContext, List<List<Score>> mLists,
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
			EditText eachDistanceEditText = (EditText) convertView
					.findViewById(R.id.et_each_distance);
			TextView tipView = (TextView) convertView.findViewById(R.id.tv_1);
			if (groupPosition < getGroupCount() - 1) {
				tv1.setText("第" + (groupPosition + 1) + "趟");
				eachDistanceEditText.setText(mLists.get(0).get(0).getDistance()
						+ "");
			} else {
				tv1.setText("成绩总计");
				tipView.setVisibility(View.GONE);
				eachDistanceEditText.setVisibility(View.GONE);
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

	public void saveScores(View v) {

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mApplication.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		mApplication.getMap().put(Constants.PLAN_ID, 0);
	}

}
