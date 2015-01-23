package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.model.Score;
import com.example.swimmingtraningsystem.model.Temp;
import com.example.swimmingtraningsystem.util.XUtils;

public class ScoreActivity extends Activity {

	private MyApplication app;
	private Spinner spinner;
	private ExpandableListView lv;
	private TextView details;
	private DBManager dbManager;
	private List<List<Score>> scoreList;
	private NameScoreListAdapter scoreListAdapter2;
	private long userid;
	private Toast toast;
	private int time = 0;
	private Plan plan;
	private List<Temp> all;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		app = (MyApplication) getApplication();
		userid = (Long) app.getMap().get("CurrentUser");
		scoreList = new ArrayList<List<Score>>();
		spinner = (Spinner) findViewById(R.id.query_category);
		details = (TextView) findViewById(R.id.show_details);
		lv = (ExpandableListView) findViewById(R.id.query_score_list);
		dbManager = DBManager.getInstance();
		List<Athlete> aths = dbManager.getAthletes(userid);
		List<Long> aids = new ArrayList<Long>();
		for (Athlete a : aths) {
			aids.add(a.getId());
		}
		List<String> category = dbManager.getScoresByAthleteId(aids);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, category);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		lv.setGroupIndicator(null);
		// 屏蔽收缩
		lv.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});

	}

	public void back(View v) {
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
	}

	public void query(View v) {
		scoreListAdapter2 = new NameScoreListAdapter();
		lv.setAdapter(scoreListAdapter2);
		details.setVisibility(View.GONE);
		time = 0;
		scoreList.clear();
		String condition = (String) spinner.getSelectedItem();
		if (!condition.equals("-- 请选择查询时间 --")) {
			plan = dbManager.getPlanInScoreByDate(condition);
			time = plan.getTime();
			details.setVisibility(View.VISIBLE);
			details.setText(spinner.getSelectedItem() + "----" + plan.getPool()
					+ " " + time + "趟");
			// 根据时间查询成绩
			for (int t = 1; t <= time; t++) {
				List<Score> sco = dbManager
						.getScoreByDateAndTimes(condition, t);
				if (t == 1) {
					List<Long> athIds = new ArrayList<Long>();
					for (Score s : sco) {
						athIds.add(s.getAthlete().getId());
					}
					all = dbManager
							.getAthleteIdInScoreByDate(condition, athIds);
				}
				scoreList.add(sco);
			}
			scoreListAdapter2.notifyDataSetChanged();
			// 展开子项
			for (int i = 0; i <= time; i++)
				lv.expandGroup(i);
		} else {
			XUtils.showToast(this, toast, "请选择正确的查询时间！");
		}

	}

	class NameScoreListAdapter extends BaseExpandableListAdapter {

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return scoreList.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ChildHolder childHolder = null;
			if (convertView == null) {
				childHolder = new ChildHolder();
				convertView = View.inflate(ScoreActivity.this,
						R.layout.show_score_list_item_sub, null);
				childHolder.rank = (TextView) convertView
						.findViewById(R.id.show_rank);
				childHolder.score = (TextView) convertView
						.findViewById(R.id.show_score);
				childHolder.name = (TextView) convertView
						.findViewById(R.id.show_name);
				convertView.setTag(childHolder);
			} else {
				childHolder = (ChildHolder) convertView.getTag();
			}

			if (groupPosition < getGroupCount() - 1) {
				childHolder.rank.setText("第" + (childPosition + 1) + "名");
				childHolder.score.setText(scoreList.get(groupPosition)
						.get(childPosition).getScore());
				childHolder.name.setText(scoreList.get(groupPosition)
						.get(childPosition).getAthlete().getName());
			} else {
				childHolder.rank.setText("第" + (childPosition + 1) + "名");
				childHolder.score.setText(all.get(childPosition).getScore());
				childHolder.name.setText(all.get(childPosition)
						.getAthleteName());
			}

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return scoreList.get(0).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return (time + 1);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder groupHolder = null;
			if (convertView == null) {
				groupHolder = new GroupHolder();
				convertView = View.inflate(ScoreActivity.this,
						R.layout.query_score_list_item_head, null);
				groupHolder.date = (TextView) convertView
						.findViewById(R.id.test_date);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}

			if (groupPosition < getGroupCount() - 1) {
				groupHolder.date.setText("第" + (groupPosition + 1) + "趟");
			} else {
				groupHolder.date.setText("本轮总计");
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

		final class GroupHolder {
			private TextView date;
		}

		final class ChildHolder {
			private TextView rank;
			private TextView score;
			private TextView name;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.slide_bottom_in,
					R.anim.slide_top_out);
			return false;
		}
		return false;
	}

}
