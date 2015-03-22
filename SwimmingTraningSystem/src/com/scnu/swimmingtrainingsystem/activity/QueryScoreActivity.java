package com.scnu.swimmingtrainingsystem.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.adapter.QueryDatesAdapter;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.Score;
import com.scnu.swimmingtrainingsystem.model.Temp;
import com.scnu.swimmingtrainingsystem.model.TempScore;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

/**
 * ��ѯ�ɼ�չʾ����
 * 
 * @author LittleByte
 * 
 */
public class QueryScoreActivity extends Activity implements OnScrollListener {
	// Volley�������
	private RequestQueue mQueue;
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
	private QueryDatesAdapter adapter;
	private ListView dateListView;
	private PopupWindow mPopWin;
	private ProgressBar progressBar;
	private Button loadmoreButton;
	private View moreView;
	private Toast mToast;
	private User mUser;
	private int currentPage = 0;
	private boolean isConnect;
	// ����������������
	private int maxDateNum = 0;
	// ���ɼ���Ŀ������
	private int lastVisibleIndex;
	// ��ǰ���ڿɼ�������
	private int visibleItemCount = 5;
	private final static String NO_SUCH_RECORDS_STRING = "û�й��ڸò�ѯ�����ļ�¼��";
	private final static String GENERATING_RESULT_STRING = "�������ɲ�ѯ���...";
	private static final String NOT_CORRECT_STRING = "���μ�ʱ�ɼ����������޷���ȷչʾ��";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_queryscore);
		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	/**
	 * ��ʼ��Activity
	 */
	private void init() {
		MyApplication mApplication = (MyApplication) getApplication();
		dbManager = DBManager.getInstance();
		mQueue = Volley.newRequestQueue(this);
		userid = (Long) mApplication.getMap().get(Constants.CURRENT_USER_ID);
		mUser = dbManager.getUser(userid);
		totalDates = (LinearLayout) findViewById(R.id.total_category);
		containLayout = (LinearLayout) findViewById(R.id.ll_query_score);
		details = (TextView) findViewById(R.id.show_details);
		dateTextView = (TextView) findViewById(R.id.text_category);

		mExpandableListView = (ExpandableListView) findViewById(R.id.query_score_list);
		isConnect = (Boolean) mApplication.getMap().get(
				Constants.IS_CONNECT_SERVICE);
		totalDates.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
			}
		});
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
	 * ����popupWindow��ʾ�ɲ鿴�������б�
	 * 
	 * @param wrapContent
	 * @param wrapContent2
	 */
	@SuppressWarnings("deprecation")
	private void showPopupWindow(int wrapContent, int wrapContent2) {
		// TODO Auto-generated method stub
		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				QueryScoreActivity.this)
				.inflate(R.layout.query_date_list, null);
		dateListView = (ListView) layout.findViewById(R.id.date_list);

		// ʵ�����ײ�����
		moreView = getLayoutInflater().inflate(R.layout.listview_footer, null);
		loadmoreButton = (Button) moreView.findViewById(R.id.more);
		progressBar = (ProgressBar) moreView.findViewById(R.id.loading);
		adapter = new QueryDatesAdapter(this, dateList);
		// ���ϵײ�View��ע��Ҫ����setAdapter����ǰ
		dateListView.addFooterView(moreView);
		dateListView.setAdapter(adapter);
		dateListView.setOnScrollListener(this);

		mPopWin = new PopupWindow(layout, totalDates.getWidth(),
				containLayout.getHeight() / 3, true);
		// �����Ϊ�˷�ֹ�����˵���ȡ����֮�󣬵��activity���������û����Ӧ
		mPopWin.setBackgroundDrawable(new BitmapDrawable());
		mPopWin.showAsDropDown(totalDates, 0, 1);
		mPopWin.update();
		loadmoreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// ���������ɼ�
				progressBar.setVisibility(View.VISIBLE);
				// ��ť���ɼ�
				loadmoreButton.setVisibility(View.GONE);
				loadMoreData();
			}
		});
		dateListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				details.setVisibility(View.GONE);
				if (position <= dateList.size()) {
					details.setVisibility(View.GONE);
					dateTextView.setText(dateList.get(position));
					// ��������ͨ���������
					if (isConnect) {
						getScoresRequest(dateList.get(position));
					} else {
						// ���ز�ѯ�������˺���ʹ�õĹ��ܣ�
						new QueryScoreTask().execute(dateList.get(position));
					}
				}
				mPopWin.dismiss();
			}
		});
	}

	protected void getScoresRequest(final String string) {
		// TODO Auto-generated method stub

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("up_time", string);
		jsonMap.put("uid", mUser.getUid());
		final String dateJson = JsonTools.creatJsonString(jsonMap);

		StringRequest getScoreDateList = new StringRequest(Method.POST,
				XUtils.HOSTURL + "getScores", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println("response>>>>>>>>>>>>>>>>>"
								+ response);
						JSONObject obj;
						try {
							obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								TempScore[] tempScores = JsonTools.getObject(
										obj.get("dataList").toString(),
										TempScore[].class);

								int pid = tempScores[0].getPlan_id();
								Plan planResult = dbManager.getPlanByPid(pid);

								details.setVisibility(View.VISIBLE);
//								details.setText("�ƻ�����" + planResult.getName()
//										+ "--" + planResult.getPool() + " "
//										+ planResult.getTime() + "��");
								for (TempScore tempScore : tempScores) {

									int aid = tempScore.getAthlete_id();
									Athlete ath = dbManager
											.getAthletesByAid(aid);
									Score newScore = new Score();
									newScore.setP(planResult);
									newScore.setDate(string);
									newScore.setAthlete(ath);
									newScore.setTimes(tempScore.getTimes());
									newScore.setScore(tempScore.getScore());
									newScore.save();
								}
//								time = planResult.getTime();

								List<List<Score>> listscores = new ArrayList<List<Score>>();
								// ����ʱ���ѯ�ɼ�
								for (int t = 1; t <= time; t++) {
									List<Score> sco = dbManager
											.getScoreByDateAndTimes(string, t);
									if (t == 1) {
										List<Long> athIds = new ArrayList<Long>();
										for (Score s : sco) {
											athIds.add(s.getAthlete().getId());
										}
										sumList = dbManager
												.getAthleteIdInScoreByDate(
														string, athIds);
									}
									// ��ѯ����Ҫȷ�����ֳɼ��Ǵ��ڵ�
									if (sco.size() != 0) {
										listscores.add(sco);
									}

								}
								NameScoreListAdapter scoreListAdapter = new NameScoreListAdapter(
										QueryScoreActivity.this, listscores,
										sumList, time + 1);
								mExpandableListView
										.setAdapter(scoreListAdapter);
								// Ĭ��չ��
								for (int i = 0; i <= time; i++) {
									mExpandableListView.expandGroup(i);
								}
							} else {

							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}) {

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				String str = null;
				try {
					str = new String(response.data, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return Response.success(str,
						HttpHeaderParser.parseCacheHeaders(response));
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// �����������
				Map<String, String> map = new HashMap<String, String>();
				map.put("date", dateJson);
				return map;
			}
		};
		getScoreDateList.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(getScoreDateList);

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
				mLoadingDialog.setMessage(GENERATING_RESULT_STRING);
				mLoadingDialog.setCanceledOnTouchOutside(false);
			}
			mLoadingDialog.show();
		}

		@Override
		protected List<List<Score>> doInBackground(String... params) {
			// TODO Auto-generated method stub
			plan = dbManager.getPlanInScoreByDate(params[0]);
			// �����������ѯȷ���ܲ�ѯ����Ӧ�ĳɼ�
			if (plan != null) {
//				time = plan.getTime();
				List<List<Score>> listss = new ArrayList<List<Score>>();
				// ����ʱ���ѯ�ɼ�
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
					// ��ѯ����Ҫȷ�����ֳɼ��Ǵ��ڵ�
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
			// �ɼ��Ƿ���������;�˳���ʱ�ᵼ�¼�ʱ�ɼ�������
			boolean isComplete = true;
			if (result.size() != time) {
				isComplete = false;
			}
			if (result != null && isComplete) {
				details.setVisibility(View.VISIBLE);
//				details.setText("�ƻ�����" + plan.getName() + "--" + plan.getPool()
//						+ " " + time + "��");
				NameScoreListAdapter scoreListAdapter = new NameScoreListAdapter(
						QueryScoreActivity.this, result, sumList, time + 1);
				mExpandableListView.setAdapter(scoreListAdapter);
				// Ĭ��չ��
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

	/**
	 * ��ȡ���ص��������ݼ����첽��������״̬ʹ��
	 * 
	 * @author LiitleByte
	 * 
	 */
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
			maxDateNum = adapter.getCount();
			adapter.setDatas(result);
			adapter.notifyDataSetChanged();
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

	public void loadMoreData() {
		// TODO Auto-generated method stub
		if (isConnect) {
			// ������������ͨ
			currentPage++;
			getScoreDateListReqeust(currentPage);
		} else {
			// ��ǰΪ����״̬
			new QueryDatesTask().execute();
		}
	}

	/**
	 * ��ȡָ��ҳ�����������ݼ�
	 * 
	 * @param curPage
	 *            ��ǰҳ
	 */
	protected void getScoreDateListReqeust(final int curPage) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("curPage", curPage);
		jsonMap.put("uid", mUser.getUid());
		final String jsonString = JsonTools.creatJsonString(jsonMap);

		StringRequest getScoreDateList = new StringRequest(Method.POST,
				XUtils.HOSTURL + "getScoreDateList", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println("dateresponse>>>>" + response);
						JSONObject obj;
						try {
							obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								maxDateNum = obj.getInt("totalRecords");

								List<String> dateResult = new ArrayList<String>();
								JSONArray dates = new JSONArray(obj.get(
										"dataList").toString());
								int length = dates.length();
								for (int i = 0; i < length; i++) {
									JSONObject jsonObject = new JSONObject(
											dates.get(i).toString());
									dateResult.add(jsonObject
											.getString("up_time"));
								}
								dateList.addAll(dateResult);
								System.out.println("dateList>>>" + dateList);
								// ���������ɼ�
								progressBar.setVisibility(View.GONE);
								// ��ť�ɼ�
								loadmoreButton.setVisibility(View.VISIBLE);
								dateListView.setAdapter(new QueryDatesAdapter(
										QueryScoreActivity.this, dateList));
							} else {
								// ���������ɼ�
								progressBar.setVisibility(View.GONE);
								// ��ť�ɼ�
								loadmoreButton.setVisibility(View.GONE);
								XUtils.showToast(QueryScoreActivity.this,
										mToast, "������ȫ��������ɣ�");
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				}) {

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				String str = null;
				try {
					str = new String(response.data, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return Response.success(str,
						HttpHeaderParser.parseCacheHeaders(response));
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// �����������
				Map<String, String> map = new HashMap<String, String>();
				map.put("getScoreDate", jsonString);
				return map;
			}
		};
		getScoreDateList.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(getScoreDateList);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.visibleItemCount = visibleItemCount;
		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int itemsLastIndex = adapter.getCount() - 1; // ���ݼ����һ�������
		int lastIndex = itemsLastIndex + 1;
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == lastIndex) {
			// ������Զ�����,��������������첽�������ݵĴ���
			// loadMoreData();
		}
	}
}
