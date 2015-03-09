package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.model.Score;
import com.example.swimmingtraningsystem.model.Temp;
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.util.XUtils;
import com.example.swimmingtraningsystem.view.LoadingDialog;

/**
 * 查询成绩展示界面
 * 
 * @author LittleByte
 * 
 */
public class QueryScoreActivity extends Activity {

	private LinearLayout totalDates, containLayout;
	private ExpandableListView mExpandableListView;
	private TextView details;
	private TextView dateTextView;
	private DBManager dbManager;
	private long userid;
	private int time = 0;
	private Plan plan;
	private List<Temp> sumList = new ArrayList<Temp>();
	private LoadingDialog mLoadingDialog;
	private List<String> dateList = new ArrayList<String>();
	private DatesAdapter adapter;
	private ListView dateListView;
	private View headView;
	private PopupWindow mPopWin;
	private Toast mToast;
	private final static String NO_SUCH_RECORDS_STRING = "没有关于该查询条件的记录！";
	private final static String PLEASE_SELECT_RIGHT_TIME_STRING = "请选择正确的查询时间！";
	private final static String GENERATING_RESULT_STRING = "正在生成查询结果...";
	private static final String NOT_CORRECT_STRING = "本次计时成绩不完整，无法正确展示！";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		init();
	}

	/**
	 * 初始化Activity
	 */
	private void init() {
		MyApplication mApplication = (MyApplication) getApplication();
		dbManager = DBManager.getInstance();
		userid = (Long) mApplication.getMap().get(Constants.CURRENT_USER_ID);
		totalDates = (LinearLayout) findViewById(R.id.total_category);
		containLayout = (LinearLayout) findViewById(R.id.ll_query_score);
		details = (TextView) findViewById(R.id.show_details);
		dateTextView = (TextView) findViewById(R.id.text_category);
		mExpandableListView = (ExpandableListView) findViewById(R.id.query_score_list);

		dateTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
			}
		});
		// 屏蔽收缩
		mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});
	}

	/**
	 * 弹出popupWindow显示可查看的日期列表
	 * 
	 * @param wrapContent
	 * @param wrapContent2
	 */
	private void showPopupWindow(int wrapContent, int wrapContent2) {
		// TODO Auto-generated method stub
		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				QueryScoreActivity.this)
				.inflate(R.layout.query_date_list, null);
		dateListView = (ListView) layout.findViewById(R.id.date_list);
		headView = View.inflate(this, R.layout.query_score_header, null);
		dateListView.addHeaderView(headView);
		adapter = new DatesAdapter(this, dateList);
		dateListView.setAdapter(adapter);
		mPopWin = new PopupWindow(layout, totalDates.getWidth(),
				containLayout.getHeight() / 3, true);
		// 这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
		mPopWin.setBackgroundDrawable(new BitmapDrawable());
		mPopWin.showAsDropDown(totalDates, 0, 1);
		mPopWin.update();
		if (dateList.size() == 0) {
			new QueryDatesTask().execute();
		} else {
			dateListView.removeHeaderView(headView);
		}

		dateListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				dateTextView.setText(dateList.get(position));
				mPopWin.dismiss();
			}
		});
	}

	/**
	 * 退出当前Activity
	 * 
	 * @param v
	 */
	public void back(View v) {
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
	}

	/**
	 * 响应查询事件
	 * 
	 * @param v
	 */
	public void queryScore(View v) {
		details.setVisibility(View.GONE);
		String condition = dateTextView.getText().toString().trim();
		if (!condition.equals("请选择日期进行查询")) {
			new QueryScoreTask().execute(condition);
		} else {
			XUtils.showToast(this, mToast, PLEASE_SELECT_RIGHT_TIME_STRING);
		}

	}

	/**
	 * 查询成绩异步任务，防止阻塞主线程导致ANR
	 * 
	 * @author LittleByte
	 * 
	 */
	class QueryScoreTask extends AsyncTask<String, Void, List<List<Score>>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (mLoadingDialog == null) {
				mLoadingDialog = LoadingDialog
						.createDialog(QueryScoreActivity.this);
				mLoadingDialog.setMessage(GENERATING_RESULT_STRING);
				mLoadingDialog.setCanceledOnTouchOutside(false);
			}
			mLoadingDialog.show();
		}

		@Override
		protected List<List<Score>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			plan = dbManager.getPlanInScoreByDate(params[0]);
			// 输入的条件查询确保能查询出对应的成绩
			if (plan != null) {
				time = plan.getTime();
				List<List<Score>> listss = new ArrayList<List<Score>>();
				// 根据时间查询成绩
				for (int t = 1; t <= time; t++) {
					List<Score> sco = dbManager.getScoreByDateAndTimes(
							params[0], t);
					if (t == 1) {
						List<Long> athIds = new ArrayList<Long>();
						for (Score s : sco) {
							athIds.add(s.getAthlete().getId());
						}
						sumList = dbManager.getAthleteIdInScoreByDate(
								params[0], athIds);
					}
					// 查询出来要确保该轮成绩是存在的
					if (sco.size() != 0) {
						listss.add(sco);
					}

				}
				return listss;
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<List<Score>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// 成绩是否完整，中途退出计时会导致计时成绩不完整
			boolean isComplete = true;
			System.out.println("result.size()--->" + result.size());
			if (result.size() != time) {
				isComplete = false;
			}
			if (result != null && isComplete) {
				details.setVisibility(View.VISIBLE);
				details.setText(dateTextView.getText().toString() + "----"
						+ plan.getPool() + " " + time + "趟");
				NameScoreListAdapter scoreListAdapter = new NameScoreListAdapter(
						QueryScoreActivity.this, result, sumList, time + 1);
				mExpandableListView.setAdapter(scoreListAdapter);
				// 默认展开
				for (int i = 0; i <= time; i++) {
					mExpandableListView.expandGroup(i);
				}
			} else if (!isComplete) {
				XUtils.showToast(QueryScoreActivity.this, mToast,
						NOT_CORRECT_STRING);
			} else {
				XUtils.showToast(QueryScoreActivity.this, mToast,
						NO_SUCH_RECORDS_STRING);
			}
			mLoadingDialog.dismiss();
		}
	}

	class NameScoreListAdapter extends BaseExpandableListAdapter {
		private Context mContext;
		private List<List<Score>> mLists = new ArrayList<List<Score>>();
		private List<Temp> mTemps = new ArrayList<Temp>();
		private int mSwimTime = 0;

		public NameScoreListAdapter(Context mContext, List<List<Score>> mLists,
				List<Temp> mTemps, int mSwimTime) {
			this.mContext = mContext;
			this.mLists = mLists;
			this.mTemps = mTemps;
			this.mSwimTime = mSwimTime;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
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
			ChildHolder childHolder = null;
			if (convertView == null) {
				childHolder = new ChildHolder();
				convertView = View.inflate(mContext,
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

			if (groupPosition < mSwimTime - 1) {
				childHolder.rank.setText("第" + (childPosition + 1) + "名");
				childHolder.score.setText(mLists.get(groupPosition)
						.get(childPosition).getScore());
				childHolder.name.setText(mLists.get(groupPosition)
						.get(childPosition).getAthlete().getName());
			} else {
				childHolder.rank.setText("第" + (childPosition + 1) + "名");
				childHolder.score.setText(mTemps.get(childPosition).getScore());
				childHolder.name.setText(mTemps.get(childPosition)
						.getAthleteName());
			}

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			if (mLists.size() == 0) {
				return 0;
			}
			return mLists.get(0).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
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
			GroupHolder groupHolder = null;
			if (convertView == null) {
				groupHolder = new GroupHolder();
				convertView = View.inflate(mContext,
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

	class QueryDatesTask extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<Athlete> aths = dbManager.getAthletes(userid);
			List<Long> aids = new ArrayList<Long>();
			for (Athlete a : aths) {
				aids.add(a.getId());
			}
			dateList = dbManager.getScoresByAthleteId(aids);
			return dateList;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dateList = result;
			dateListView.removeHeaderView(headView);
			adapter.setDatas(result);
			adapter.notifyDataSetChanged();
		}
	}

	class DatesAdapter extends BaseAdapter {
		private Context mContext;
		private List<String> list = new ArrayList<String>();

		public DatesAdapter(Context mContext, List<String> list) {
			this.mContext = mContext;
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = View.inflate(mContext,
						android.R.layout.simple_list_item_1, null);
			}
			TextView tView = (TextView) convertView
					.findViewById(android.R.id.text1);
			tView.setText(list.get(position));
			return convertView;
		}

		public void setDatas(List<String> list) {
			this.list.clear();
			this.list.addAll(list);
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
