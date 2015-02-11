package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
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
	private List<String> listTag = new ArrayList<String>();
	private List<Temp> mScoreSum = new ArrayList<Temp>();
	private List<List<Score>> list = new ArrayList<List<Score>>();
	private int mAthleteCount = 0;
	private TextView mPlanName;
	private LoadingDialog mLoadingDialog;
	private MyAdapter adapter;
	private int times;
	private String date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showscore);
		init();
	}

	/**
	 * 初始化界面
	 */
	private void init() {
		mApplication = (MyApplication) getApplication();
		mDbManager = DBManager.getInstance();
		mExpandableListView = (ExpandableListView) findViewById(R.id.show_list);
		mPlanName = (TextView) findViewById(R.id.show_the_plan);
		times = (Integer) mApplication.getMap().get(Constants.SWIM_TIME);
		date = getIntent().getStringExtra(Constants.TEST_DATE);
		String planString = getIntent().getStringExtra("Plan");
		mPlanName.setText(planString);
		adapter = new MyAdapter();
		mExpandableListView.setAdapter(adapter);
		//启动查询异步任务
		new MyTask().execute();
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
	class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (mLoadingDialog == null) {
				mLoadingDialog = LoadingDialog
						.createDialog(ShowScoreActivity.this);
				mLoadingDialog.setMessage("正在生成本次计时结果...");
				mLoadingDialog.setCanceledOnTouchOutside(false);
			}
			mLoadingDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<Score> athScores = mDbManager
					.getAthleteNumberInScoreByDate(date);
			mAthleteCount = athScores.size();
			List<Long> athIds = new ArrayList<Long>();
			for (Score s : athScores) {
				athIds.add(s.getAthlete().getId());
			}
			mScoreSum = mDbManager.getAthleteIdInScoreByDate(date, athIds);
			for (int i = 1; i <= times + 1; i++) {
				if (i <= times) {
					listTag.add("第" + i + "趟");
					List<Score> ls = mDbManager.getScoreByDateAndTimes(date, i);
					list.add(ls);
				} else {
					listTag.add("合计");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
			// 默认展开
			for (int i = 0; i < adapter.getGroupCount(); i++) {
				mExpandableListView.expandGroup(i);
			}
			mLoadingDialog.dismiss();

		}
	}

	class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return list.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = View.inflate(ShowScoreActivity.this,
					R.layout.show_score_list_item_sub, null);
			TextView tv1 = (TextView) convertView.findViewById(R.id.show_rank);
			TextView tv2 = (TextView) convertView.findViewById(R.id.show_score);
			TextView tv3 = (TextView) convertView.findViewById(R.id.show_name);
			tv1.setText("第" + (childPosition + 1) + "名");
			if (groupPosition < getGroupCount() - 1) {
				Score s = list.get(groupPosition).get(childPosition);
				tv2.setText(s.getScore());
				tv3.setText(mDbManager.getAthleteNameByScoreID(s.getId()));
			} else {
				tv2.setText(mScoreSum.get(childPosition).getScore());
				tv3.setText(mScoreSum.get(childPosition).getAthleteName());
			}

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mAthleteCount;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return listTag.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return listTag.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = View.inflate(ShowScoreActivity.this,
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
		mApplication.getMap().put(Constants.SWIM_TIME, 0);
		mApplication.getMap().put(Constants.PLAN_ID, 0);
	}

}
