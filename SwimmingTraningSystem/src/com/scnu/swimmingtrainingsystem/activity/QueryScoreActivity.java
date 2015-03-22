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
 * 查询成绩展示界面
 * 
 * @author LittleByte
 * 
 */
public class QueryScoreActivity extends Activity implements OnScrollListener {
	// Volley请求队列
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
	// 设置最大的数据条数
	private int maxDateNum = 0;
	// 最后可见条目的索引
	private int lastVisibleIndex;
	// 当前窗口可见项总数
	private int visibleItemCount = 5;
	private final static String NO_SUCH_RECORDS_STRING = "没有关于该查询条件的记录！";
	private final static String GENERATING_RESULT_STRING = "正在生成查询结果...";
	private static final String NOT_CORRECT_STRING = "本次计时成绩不完整，无法正确展示！";

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
	 * 初始化Activity
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
	@SuppressWarnings("deprecation")
	private void showPopupWindow(int wrapContent, int wrapContent2) {
		// TODO Auto-generated method stub
		LinearLayout layout = (LinearLayout) LayoutInflater.from(
				QueryScoreActivity.this)
				.inflate(R.layout.query_date_list, null);
		dateListView = (ListView) layout.findViewById(R.id.date_list);

		// 实例化底部布局
		moreView = getLayoutInflater().inflate(R.layout.listview_footer, null);
		loadmoreButton = (Button) moreView.findViewById(R.id.more);
		progressBar = (ProgressBar) moreView.findViewById(R.id.loading);
		adapter = new QueryDatesAdapter(this, dateList);
		// 加上底部View，注意要放在setAdapter方法前
		dateListView.addFooterView(moreView);
		dateListView.setAdapter(adapter);
		dateListView.setOnScrollListener(this);

		mPopWin = new PopupWindow(layout, totalDates.getWidth(),
				containLayout.getHeight() / 3, true);
		// 这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
		mPopWin.setBackgroundDrawable(new BitmapDrawable());
		mPopWin.showAsDropDown(totalDates, 0, 1);
		mPopWin.update();
		loadmoreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 将进度条可见
				progressBar.setVisibility(View.VISIBLE);
				// 按钮不可见
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
					// 服务器联通则进行请求
					if (isConnect) {
						getScoresRequest(dateList.get(position));
					} else {
						// 本地查询（试用账号所使用的功能）
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
//								details.setText("计划名：" + planResult.getName()
//										+ "--" + planResult.getPool() + " "
//										+ planResult.getTime() + "趟");
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
								// 根据时间查询成绩
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
									// 查询出来要确保该轮成绩是存在的
									if (sco.size() != 0) {
										listscores.add(sco);
									}

								}
								NameScoreListAdapter scoreListAdapter = new NameScoreListAdapter(
										QueryScoreActivity.this, listscores,
										sumList, time + 1);
								mExpandableListView
										.setAdapter(scoreListAdapter);
								// 默认展开
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
				// 设置请求参数
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
	 * 退出当前Activity
	 * 
	 * @param v
	 */
	public void back(View v) {
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
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
//				time = plan.getTime();
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
			if (result.size() != time) {
				isComplete = false;
			}
			if (result != null && isComplete) {
				details.setVisibility(View.VISIBLE);
//				details.setText("计划名：" + plan.getName() + "--" + plan.getPool()
//						+ " " + time + "趟");
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

	/**
	 * 获取本地的日期数据集的异步任务，离线状态使用
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
			// 如果与服务器联通
			currentPage++;
			getScoreDateListReqeust(currentPage);
		} else {
			// 当前为离线状态
			new QueryDatesTask().execute();
		}
	}

	/**
	 * 获取指定页数的日期数据集
	 * 
	 * @param curPage
	 *            当前页
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
								// 进度条不可见
								progressBar.setVisibility(View.GONE);
								// 按钮可见
								loadmoreButton.setVisibility(View.VISIBLE);
								dateListView.setAdapter(new QueryDatesAdapter(
										QueryScoreActivity.this, dateList));
							} else {
								// 进度条不可见
								progressBar.setVisibility(View.GONE);
								// 按钮可见
								loadmoreButton.setVisibility(View.GONE);
								XUtils.showToast(QueryScoreActivity.this,
										mToast, "数据已全部加载完成！");
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
				// 设置请求参数
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
		int itemsLastIndex = adapter.getCount() - 1; // 数据集最后一项的索引
		int lastIndex = itemsLastIndex + 1;
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == lastIndex) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码
			// loadMoreData();
		}
	}
}
