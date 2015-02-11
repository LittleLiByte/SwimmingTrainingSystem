package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.util.XUtils;
import com.example.swimmingtraningsystem.view.LoadingDialog;

/**
 * ��ѯ�ɼ�չʾ����
 * 
 * @author LittleByte
 * 
 */
public class QueryScoreActivity extends Activity {

	private MyApplication mApplication;
	private Spinner mSpinner;
	private ExpandableListView mExpandableListView;
	private TextView details;
	private DBManager dbManager;
	private List<List<Score>> scoreList = new ArrayList<List<Score>>();;
	private NameScoreListAdapter scoreListAdapter;
	private long userid;
	private Toast toast;
	private int time = 0;
	private Plan plan;
	private List<Temp> sumList = new ArrayList<Temp>();
	private LoadingDialog mLoadingDialog;
	private List<String> dateList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		init();
	}

	/**
	 * ��ʼ��Activity
	 */
	private void init() {
		mApplication = (MyApplication) getApplication();
		dbManager = DBManager.getInstance();
		userid = (Long) mApplication.getMap().get(Constants.CURRENT_USER_ID);
		mSpinner = (Spinner) findViewById(R.id.query_category);
		details = (TextView) findViewById(R.id.show_details);
		mExpandableListView = (ExpandableListView) findViewById(R.id.query_score_list);

		List<Athlete> aths = dbManager.getAthletes(userid);
		List<Long> aids = new ArrayList<Long>();
		for (Athlete a : aths) {
			aids.add(a.getId());
		}
		dateList.add("-- ��ѡ���ѯʱ��  --");
		// List<String> dates = dbManager.getScoresByAthleteId(aids);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, dateList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);

		// ��������
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
	 * �˳���ǰActivity
	 * 
	 * @param v
	 */
	public void back(View v) {
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
	}

	/**
	 * ��Ӧ��ѯ�¼�
	 * 
	 * @param v
	 */
	public void queryScore(View v) {
		details.setVisibility(View.GONE);
		String condition = (String) mSpinner.getSelectedItem();
		if (!condition.equals("-- ��ѡ���ѯʱ��  --")) {
			new QueryScoreTask().execute(condition);
		} else {
			XUtils.showToast(this, toast, "��ѡ����ȷ�Ĳ�ѯʱ�䣡");
		}

	}

	/**
	 * ��ѯ�ɼ��첽���񣬷�ֹ�������̵߳���ANR
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
				mLoadingDialog.setMessage("�������ɲ�ѯ���...");
				mLoadingDialog.setCanceledOnTouchOutside(false);
			}
			mLoadingDialog.show();
		}

		@Override
		protected List<List<Score>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			plan = dbManager.getPlanInScoreByDate(params[0]);
			time = plan.getTime();
			sumList.clear();
			scoreList.clear();
			// ����ʱ���ѯ�ɼ�
			for (int t = 1; t <= time; t++) {
				List<Score> sco = dbManager
						.getScoreByDateAndTimes(params[0], t);
				if (t == 1) {
					List<Long> athIds = new ArrayList<Long>();
					for (Score s : sco) {
						athIds.add(s.getAthlete().getId());
					}
					sumList = dbManager.getAthleteIdInScoreByDate(params[0],
							athIds);
				}
				scoreList.add(sco);
			}
			return scoreList;
		}

		@Override
		protected void onPostExecute(List<List<Score>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			details.setVisibility(View.VISIBLE);
			details.setText(mSpinner.getSelectedItem() + "----"
					+ plan.getPool() + " " + time + "��");

			scoreListAdapter = new NameScoreListAdapter(
					QueryScoreActivity.this, result, sumList, time + 1);
			mExpandableListView.setAdapter(scoreListAdapter);
			// Ĭ��չ��
			for (int i = 0; i <= time; i++) {
				mExpandableListView.expandGroup(i);
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
				childHolder.rank.setText("��" + (childPosition + 1) + "��");
				childHolder.score.setText(mLists.get(groupPosition)
						.get(childPosition).getScore());
				childHolder.name.setText(mLists.get(groupPosition)
						.get(childPosition).getAthlete().getName());
			} else {
				childHolder.rank.setText("��" + (childPosition + 1) + "��");
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
				groupHolder.date.setText("��" + (groupPosition + 1) + "��");
			} else {
				groupHolder.date.setText("�����ܼ�");
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
