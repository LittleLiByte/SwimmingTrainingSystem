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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
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
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;
import com.scnu.swimmingtrainingsystem.view.XListView;
import com.scnu.swimmingtrainingsystem.view.XListView.IXListViewListener;

/**
 * 获取查询日期页面
 * 
 * @author LittleByte
 * 
 */
public class SearchboxActivity extends Activity implements IXListViewListener {

	private MyApplication myApplication;
	private DBManager mDbManager;
	private RequestQueue mQueue;

	private RadioGroup radioGroup;
	private Spinner spinner;
	private XListView mListView;
	private LoadingDialog mLoadingDialog;
	private Toast mToast;

	private ArrayAdapter<String> mAdapter;
	private Long userid;
	private boolean isConnected;
	// 默认本地搜索
	private boolean searchType = false;
	private int scoreType = 0;
	private int resultCode = 0x11;
	private int offset = 0;
	private int offset2 = 0;
	private int offset3 = 0;
	private List<String> dateList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_searchbox);
		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}

	}

	private void init() {
		// TODO Auto-generated method stub
		myApplication = (MyApplication) getApplication();
		mDbManager = DBManager.getInstance();
		mQueue = Volley.newRequestQueue(this);
		userid = (Long) myApplication.getMap().get(Constants.CURRENT_USER_ID);

		isConnected = (Boolean) myApplication.getMap().get(
				Constants.IS_CONNECT_SERVER);

		spinner = (Spinner) findViewById(R.id.search_category);
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup1);
		mListView = (XListView) findViewById(R.id.search_date_list);
		mAdapter = new ArrayAdapter<String>(this, R.layout.xlist_item, dateList);
		mListView.setAdapter(mAdapter);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		List<String> searchWay = new ArrayList<String>();
		searchWay.add("本地搜索");
		searchWay.add("联网搜索");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, searchWay);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 1) {
					// 选择联网搜索
					searchType = true;
					if (isConnected) {
						dateList.clear();
						radioGroup.check(R.id.btn_0);
						offset = 0;
						offset2 = 0;
						offset3 = 0;
						onLoad(1, offset);
					} else {
						CommonUtils.showToast(SearchboxActivity.this, mToast,
								"没有连接服务器！");
					}
				} else {
					dateList.clear();
					radioGroup.check(R.id.btn_0);
					searchType = false;
					offset = 0;
					offset2 = 0;
					offset3 = 0;
					onLoad(1, offset);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				excuteSearching(checkedId);
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent data = new Intent();
				data.putExtra("date", dateList.get(position - 1));
				System.out.println("dateList.get(position - 1)"
						+ dateList.get(position - 1));
				if (searchType) {
					resultCode = 1;
				} else {
					resultCode = 0;
				}
				setResult(resultCode, data);
				finish();
			}
		});

	}

	/**
	 * 执行搜索
	 * 
	 * @param checkedId
	 */
	protected void excuteSearching(int checkedId) {
		// TODO Auto-generated method stub
		dateList.clear();
		mAdapter.notifyDataSetChanged();
		if (searchType) {
			// 联网搜索
			switch (checkedId) {
			case R.id.btn_0:
				scoreType = 1;
				onLoad(scoreType, offset);
				break;
			case R.id.btn_1:
				scoreType = 2;
				onLoad(scoreType, offset2);
				break;
			case R.id.btn_2:
				scoreType = 3;
				onLoad(scoreType, offset3);
				break;
			default:
				break;
			}
		} else {
			// 本地搜索
			switch (checkedId) {
			case R.id.btn_0:
				scoreType = 1;
				onLoad(scoreType, offset);
				break;
			case R.id.btn_1:
				scoreType = 2;
				onLoad(scoreType, offset2);
				break;
			case R.id.btn_2:
				scoreType = 3;
				onLoad(scoreType, offset3);
				break;
			default:
				break;
			}
		}
	}

	public void back(View v) {
		setResult(resultCode, null);
		finish();
	}

	/**
	 * 获取本地的日期数据集的异步任务
	 * 
	 * @author LiitleByte
	 * 
	 */
	class QueryDatesTask extends AsyncTask<Integer, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			List<String> list = new ArrayList<String>();
			list = mDbManager.getScoresByUserId(userid, params[0], params[1]);
			return list;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result.size() != 0) {
				dateList.addAll(result);
				mAdapter.notifyDataSetChanged();
			} else {
				mListView.stopLoadMore();
				Toast.makeText(SearchboxActivity.this, "没有可加载的数据了！",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (scoreType == 0) {
			offset += 20;
			onLoad(scoreType, offset);
		} else if (scoreType == 1) {
			offset2 += 20;
			onLoad(scoreType, offset2);
		} else {
			offset3 += 20;
			onLoad(scoreType, offset3);
		}

	}

	private void onLoad(int scoreType, int off) {
		// TODO Auto-generated method stub
		if (searchType) {
			getScoreDateListReqeust(scoreType, off);
		} else {
			new QueryDatesTask().execute(scoreType, off);
		}

	}

	/**
	 * 获取指定页数的日期数据集
	 * 
	 * @param curPage
	 *            当前页
	 */
	protected void getScoreDateListReqeust(int type, int curPage) {
		if (mLoadingDialog == null) {
			mLoadingDialog = LoadingDialog.createDialog(SearchboxActivity.this);
			mLoadingDialog.setMessage("正在努力查询...");
			mLoadingDialog.setCanceledOnTouchOutside(false);
		}
		mLoadingDialog.show();
		User user = mDbManager.getUser(userid);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("curPage", curPage);
		jsonMap.put("uid", user.getUid());
		jsonMap.put("type", type);
		final String jsonString = JsonTools.creatJsonString(jsonMap);

		StringRequest getScoreDateList = new StringRequest(Method.POST,
				CommonUtils.HOSTURL + "getScoreDateList",
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println("dateresponse>>>>" + response);
						JSONObject obj;
						try {
							obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {

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
								mAdapter.notifyDataSetChanged();
								System.out.println("dateList>>>" + dateList);
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
						mLoadingDialog.dismiss();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(resultCode, null);
			finish();
			overridePendingTransition(R.anim.slide_bottom_in,
					R.anim.slide_top_out);
			return false;
		}
		return false;
	}
}
