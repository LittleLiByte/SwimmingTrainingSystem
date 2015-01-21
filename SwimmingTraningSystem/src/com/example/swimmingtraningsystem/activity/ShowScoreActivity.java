package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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

public class ShowScoreActivity extends Activity {
	private MyApplication app;
	private DBManager dbManager;
	private ExpandableListView listView;
	private List<String> listTag = new ArrayList<String>();
	private List<Temp> all;
	private List<List<Score>> list = new ArrayList<List<Score>>();
	private int athleteCount = 0;
	private TextView planName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showscore);
		app = (MyApplication) getApplication();
		dbManager=DBManager.getInstance();
		listView = (ExpandableListView) findViewById(R.id.show_list);
		planName = (TextView) findViewById(R.id.show_the_plan);
		int times = (Integer) app.getMap().get("current");
		String date = getIntent().getStringExtra("testDate");
		String planString = getIntent().getStringExtra("Plan");
		planName.setText(planString);
		List<Score> athScores =dbManager.getAthleteNumberInScoreByDate(date);
		athleteCount = athScores.size();

		List<Long> athIds = new ArrayList<Long>();
		for (Score s : athScores) {
			athIds.add(s.getAthlete().getId());
		}
		all = dbManager.getAthleteIdInScoreByDate(date, athIds);
		for (int i = 1; i <= times + 1; i++) {
			if (i <= times) {
				listTag.add("第" + i + "趟");
				List<Score> ls = dbManager.getScoreByDateAndTimes(date, i);
				list.add(ls);
			} else {
				listTag.add("合计");
			}
		}

		MyAdapter adapter = new MyAdapter();
		listView.setAdapter(adapter);

		// 默认展开
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			listView.expandGroup(i);
		}
		// 屏蔽收缩
		listView.setOnGroupClickListener(new OnGroupClickListener() {

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
				tv3.setText(dbManager.getAthleteNameByScoreID(s.getId()));
			} else {
				tv2.setText(all.get(childPosition).getScore());
				tv3.setText(all.get(childPosition).getAthleteName());
			}

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return athleteCount;
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
		app.getMap().put("current", 0);
		app.getMap().put("planID", 0);
	}

}
