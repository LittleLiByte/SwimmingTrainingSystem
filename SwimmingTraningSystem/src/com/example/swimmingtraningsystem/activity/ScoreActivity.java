package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
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
import com.example.swimmingtraningsystem.util.XUtils;

public class ScoreActivity extends Activity {

	private MyApplication app;
	private EditText queryKey;
	private Spinner spinner;
	private ExpandableListView lv;
	private List<String> dateList;
	private DBManager dbManager;
	private List<List<Score>> scoreList;

	private NameScoreListAdapter scoreListAdapter2;
	private long userid;
	private Long[] ids;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		app = (MyApplication) getApplication();
		userid = (Long) app.getMap().get("CurrentUser");

		queryKey = (EditText) findViewById(R.id.query_key);
		spinner = (Spinner) findViewById(R.id.query_category);
		lv = (ExpandableListView) findViewById(R.id.query_score_list);
		dbManager = DBManager.getInstance();
		dateList = new ArrayList<String>();
		scoreList = new ArrayList<List<Score>>();
		List<String> category = new ArrayList<String>();

		category.add("按姓名查询");
		category.add("按日期查询");
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
		dateList.clear();
		String name = queryKey.getText().toString().trim();
		if (!TextUtils.isEmpty(name)) {
			// 根据运动名字获得运动员全部信息
			Athlete a = dbManager.getAthleteByName(userid, name);
			if (a == null) {
				XUtils.showToast(this, toast, "该运动员不存在！");
			} else {
				// 根据运动员id获取其所有相关的成绩
				List<Score> scores = dbManager.getScoreByAth(a.getId());
				if (scores.size() == 0) {
					XUtils.showToast(this, toast, "该运动员尚无成绩记录！");
				} else {
					for (Score s : scores) {
						String date = s.getDate();
						if (!dateList.contains(date)) {
							dateList.add(date);
						}
					}
					// 获取计划
					List<Long> pls = dbManager.getPlanInScoreByDate(dateList,
							a.getId());
					ids = pls.toArray(new Long[pls.size()]);

					// 获取不同测试日期的成绩列表
					scoreList = dbManager.getScoreByDate(dateList, a.getId());
					scoreListAdapter2.notifyDataSetChanged();
				}

			}

			// 展开子项
			for (int i = 0; i < dateList.size(); i++)
				lv.expandGroup(i);
		} else {
			XUtils.showToast(this, toast, "请输入搜索关键字");
		}

	}

	class DateScoreListAdapter extends BaseExpandableListAdapter {

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			return null;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return dateList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
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
				groupHolder.plan = (TextView) convertView
						.findViewById(R.id.test_plan);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}
			groupHolder.date.setText(dateList.get(groupPosition));

			Plan plan = DataSupport.find(Plan.class, ids[groupPosition]);
			String str = plan.getName() + "--" + plan.getPool();
			groupHolder.plan.setText(str);
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

		final class GroupHolder {
			private TextView date;
			private TextView plan;
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
			// TODO Auto-generated method stub
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
						R.layout.query_score_list_item_sub, null);
				childHolder.rank = (TextView) convertView
						.findViewById(R.id.test_times);

				childHolder.score = (TextView) convertView
						.findViewById(R.id.test_score);
				convertView.setTag(childHolder);
			} else {
				childHolder = (ChildHolder) convertView.getTag();
			}
			childHolder.rank.setText("第" + (childPosition + 1) + "趟");
			childHolder.score.setText(scoreList.get(groupPosition)
					.get(childPosition).getScore());
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return scoreList.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return dateList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return dateList.size();
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
				groupHolder.plan = (TextView) convertView
						.findViewById(R.id.test_plan);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}
			groupHolder.date.setText(dateList.get(groupPosition));

			Plan plan = DataSupport.find(Plan.class, ids[groupPosition]);
			String str = plan.getName() + "--" + plan.getPool();
			groupHolder.plan.setText(str);
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
			private TextView plan;
		}

		final class ChildHolder {
			private TextView rank;
			private TextView score;
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
