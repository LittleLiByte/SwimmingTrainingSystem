package com.scnu.swimmingtrainingsystem.fragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.scnu.swimmingtrainingsystem.activity.LoginActivity;
import com.scnu.swimmingtrainingsystem.activity.MyApplication;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.Upid;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

/**
 * 查看计划Fragment
 * 
 * @author LittleByte
 * 
 */
public class ViewPlanFragment extends Fragment implements OnClickListener {
	private static final String UNKNOW_ERROR = "服务器错误";
	private static final String SYNCHRONOUS_SUCCESS = "同步成功！";
	private MyApplication app;
	private Activity activity;
	private DBManager dbManager;
	private ListView listView;
	private ViewPlanAdapter adapter;
	private RelativeLayout relative;
	private List<Plan> plans = new ArrayList<Plan>();
	private List<Plan> selectid;
	private Button calcle, delete;
	private TextView tips;
	private boolean isMulChoice = false; // 是否多选
	private RequestQueue mQueue;
	private LoadingDialog loadingDialog;
	private Boolean isConnect;
	private Toast mToast;
	private Long mUserId;
	private User mUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_view_plan, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		try {
			initFragment();
		} catch (Exception e) {
			e.printStackTrace();
			startActivity(new Intent(getActivity(), LoginActivity.class));
		}

	}

	private void initFragment() {
		activity = getActivity();
		app = (MyApplication) activity.getApplication();
		dbManager = DBManager.getInstance();
		mUserId = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		mUser = dbManager.getUser(mUserId);

		listView = (ListView) activity.findViewById(R.id.view_plan_list);
		relative = (RelativeLayout) activity.findViewById(R.id.relative);
		calcle = (Button) activity.findViewById(R.id.view_cancle);
		delete = (Button) activity.findViewById(R.id.view_delete);
		tips = (TextView) activity.findViewById(R.id.txtcount);
		calcle.setOnClickListener(this);
		delete.setOnClickListener(this);
		selectid = new ArrayList<Plan>();
		plans = dbManager.getUserPlans(mUserId);
		adapter = new ViewPlanAdapter(activity, tips, plans);
		listView.setAdapter(adapter);
		mQueue = Volley.newRequestQueue(activity);
		// 如果处在联网状态，则发送至服务器
		isConnect = (Boolean) app.getMap().get(Constants.IS_CONNECT_SERVICE);

		SharedPreferences sp = activity.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		boolean isFirst = sp.getBoolean(Constants.FISRTOPENPLAN, true);
		boolean isAthleteOpen = sp.getBoolean(Constants.FISRTOPENATHLETE, true);
		if (isFirst && !isAthleteOpen) {
			XUtils.initPlans(activity, false);
		}
		// 当服务器可以联通且第一次打开计划页面且运动员页面不是第一次打开，才从服务器获取计划数据
		if (isConnect && isFirst && !isAthleteOpen) {
			if (loadingDialog == null) {
				loadingDialog = LoadingDialog.createDialog(activity);
				loadingDialog.setMessage("正在同步...");
				loadingDialog.setCanceledOnTouchOutside(false);
			}
			loadingDialog.show();
			// 发送至服务器
			getPlansRequest();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.view_cancle:
			isMulChoice = false;
			break;
		case R.id.view_delete:
			isMulChoice = false;
			for (int i = 0; i < selectid.size(); i++) {
				for (int j = 0; j < plans.size(); j++) {
					if (selectid.get(i).equals(plans.get(j))) {
						plans.remove(j);
					}
				}
			}
			// 在数据库中删除之前获取计划对应的uid和pid
			List<Upid> upids = dbManager.getdeletePlanId(selectid);
			dbManager.deletePlans(selectid);

			if (isConnect) {
				// 发送至服务器
				deletePlanRequest(upids);
			}

			break;
		default:
			break;
		}
		selectid.clear();
		adapter.notifyDataSetChanged();
		relative.setVisibility(View.INVISIBLE);
	}

	class ViewPlanAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater = null;
		private SparseIntArray visiblecheck;// 用来记录是否显示checkBox
		private SparseBooleanArray ischeck;
		private TextView tips;
		private List<Plan> planList = new ArrayList<Plan>();
		private List<String> athleteName = new ArrayList<String>();

		public ViewPlanAdapter(Context context, TextView tips,
				List<Plan> planList) {
			this.context = context;
			this.tips = tips;
			this.planList = planList;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			visiblecheck = new SparseIntArray();
			ischeck = new SparseBooleanArray();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return planList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return planList.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (isMulChoice) {
				for (int i = 0; i < plans.size(); i++) {
					ischeck.put(i, false);
					visiblecheck.put(i, CheckBox.VISIBLE);
				}
			} else {
				for (int i = 0; i < plans.size(); i++) {
					ischeck.put(i, false);
					visiblecheck.put(i, CheckBox.INVISIBLE);
				}
			}
			ViewPlanHolder holder = null;
			if (convertView == null) {
				holder = new ViewPlanHolder();
				convertView = inflater.inflate(R.layout.view_plan_list_item,
						null);
				holder.txt = (TextView) convertView
						.findViewById(R.id.view_plan_tv);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.check);
				convertView.setTag(holder);
			} else {
				holder = (ViewPlanHolder) convertView.getTag();
			}

//			holder.txt.setText(planList.get(position).getName());

			holder.checkBox.setChecked(ischeck.get(position));
			holder.checkBox.setVisibility(visiblecheck.get(position));

			convertView.setOnLongClickListener(new Onlongclick());
			convertView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					ViewPlanHolder planHolder = (ViewPlanHolder) v.getTag();
					if (isMulChoice) {
						if (planHolder.checkBox.isChecked()) {
							planHolder.checkBox.setChecked(false);
							selectid.remove(planList.get(position));
						} else {
							planHolder.checkBox.setChecked(true);
							selectid.add(planList.get(position));
						}
						tips.setText("共选择了" + selectid.size() + "项");

					} else {
						createDialog(position);
					}
				}
			});

			return convertView;
		}

		public void setDatas(List<Plan> pl) {
			this.planList.clear();
			this.planList.addAll(pl);
		}

		private void createDialog(int position) {/*
			final NiftyDialogBuilder selectDialog = NiftyDialogBuilder
					.getInstance(activity);
			Effectstype effect = Effectstype.Fall;
			selectDialog.setCustomView(R.layout.dialog_view_plan, activity);
			Window window = selectDialog.getWindow();
			TextView length = (TextView) window
					.findViewById(R.id.view_plan_pool_length);
			TextView times = (TextView) window
					.findViewById(R.id.view_plan_times);
			length.setText(planList.get(position).getPool());
			times.setText(planList.get(position).getTime() + "");
			ListView viewList = (ListView) window
					.findViewById(R.id.planlist_view);
			long plan_id = planList.get(position).getId();
			List<Athlete> athletes = dbManager.getAthInPlan(plan_id);
			if (athleteName.size() != 0)
				athleteName.clear();

			for (Athlete a : athletes) {
				athleteName.add(a.getName());
			}
			ArrayAdapter<String> arAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_1, athleteName);
			viewList.setAdapter(arAdapter);
			selectDialog
					.withTitle("查看计划")
					.withMessage(null)
					.withIcon(
							getResources().getDrawable(R.drawable.ic_launcher))
					.isCancelableOnTouchOutside(true).withDuration(500)
					.withEffect(effect).hideFoot().show();

		*/}

		class Onlongclick implements OnLongClickListener {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				isMulChoice = true;
				selectid.clear();
				relative.setVisibility(View.VISIBLE);
				for (int i = 0; i < planList.size(); i++) {
					adapter.visiblecheck.put(i, CheckBox.VISIBLE);
				}
				adapter.notifyDataSetChanged();
				return true;
			}
		}

		final class ViewPlanHolder {
			private TextView txt;
			private CheckBox checkBox;
		}

	}

	public void getPlansRequest() {
		StringRequest getAthRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "getPlans", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i(Constants.TAG, "response>>>>>" + response);
						loadingDialog.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(response);
							int resCode = (Integer) jsonObject.get("resCode");
							if (resCode == 1) {
								String jsonString = jsonObject.get("planList")
										.toString();
								JSONArray planArray = new JSONArray(jsonString);
								int planSize = planArray.length();
								for (int i = 0; i < planSize; i++) {
									Plan newPlan = new Plan();
									TempPlan tpPlan = JsonTools.getObject(
											planArray.get(i).toString(),
											TempPlan.class);
									newPlan.setPid(tpPlan.getPid());
//									newPlan.setName(tpPlan.getName());
									newPlan.setPool(tpPlan.getPool());
//									if (tpPlan.getTime() != null) {
//										newPlan.setTime(Integer.parseInt(tpPlan
//												.getTime()));
//									}
									Integer[] athIDs = tpPlan.getAthleteID();

									List<Athlete> athsList = dbManager
											.getAthletesByAid(athIDs, mUserId);
									newPlan.setAthlete(athsList);
									newPlan.setUser(mUser);
									newPlan.save();
								}
								adapter.setDatas(dbManager
										.getUserPlans(mUserId));
								adapter.notifyDataSetChanged();
								XUtils.showToast(activity, mToast,
										SYNCHRONOUS_SUCCESS);

							} else if (resCode == 2) {
								XUtils.showToast(activity, mToast, "同步失败");
							} else {
								XUtils.showToast(activity, mToast, UNKNOW_ERROR);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						loadingDialog.dismiss();
						Log.e("ViewPlan", error.getMessage());
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
				User mUser = dbManager.getUser(mUserId);
				map.put("getPlanFirst", mUser.getUid() + "");
				return map;
			}

		};
		getAthRequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(getAthRequest);
	}

	public void deletePlanRequest(List<Upid> upids) {
		// 数据查询出该计划的uid和pid;
		final String jsonString = JsonTools.creatJsonString(upids);

		System.out.println("jsonString--->" + jsonString);
		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "deletePlans", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("ViewPlan", response);
						if (response.equals("ok")) {

						} else {
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.e("ViewPlan", error.getMessage());
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
				map.put("deletePlansJson", jsonString);
				return map;
			}

		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	/**
	 * 
	 * @author LitleByte 方便Gson转换的临时类
	 * 
	 */
	class TempPlan {
		int pid;
		String name;
		String time;
		String pool;
		Integer[] athleteID;

		public int getPid() {
			return pid;
		}

		public void setPid(int pid) {
			this.pid = pid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getPool() {
			return pool;
		}

		public void setPool(String pool) {
			this.pool = pool;
		}

		public Integer[] getAthleteID() {
			return athleteID;
		}

		public void setAthleteID(Integer[] athleteID) {
			this.athleteID = athleteID;
		}

		@Override
		public String toString() {
			return "TempPlan [pid=" + pid + ", name=" + name + ", time=" + time
					+ ", pool=" + pool + ", athleteID="
					+ Arrays.toString(athleteID) + "]";
		}

	}

}
