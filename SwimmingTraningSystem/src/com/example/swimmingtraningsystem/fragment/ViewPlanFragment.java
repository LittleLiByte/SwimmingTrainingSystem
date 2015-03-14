package com.example.swimmingtraningsystem.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.activity.LoginActivity;
import com.example.swimmingtraningsystem.activity.MyApplication;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.effect.Effectstype;
import com.example.swimmingtraningsystem.effect.NiftyDialogBuilder;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.model.Upid;
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.util.XUtils;
import com.example.swimmingtraningsystem.view.LoadingDialog;

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
	private List<Plan> plans;
	private List<Plan> selectid;
	private Button calcle, delete;
	private TextView tips;
	private boolean isMulChoice = false; // 是否多选
	private RequestQueue mQueue;
	private LoadingDialog loadingDialog;
	private Boolean isConnect;
	private Toast mToast;

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
		long userID = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		dbManager = DBManager.getInstance();
		listView = (ListView) activity.findViewById(R.id.view_plan_list);
		relative = (RelativeLayout) activity.findViewById(R.id.relative);
		calcle = (Button) activity.findViewById(R.id.view_cancle);
		delete = (Button) activity.findViewById(R.id.view_delete);
		tips = (TextView) activity.findViewById(R.id.txtcount);
		calcle.setOnClickListener(this);
		delete.setOnClickListener(this);
		selectid = new ArrayList<Plan>();
		plans = dbManager.getUserPlans(userID);
		adapter = new ViewPlanAdapter(activity, tips);
		listView.setAdapter(adapter);
		mQueue = Volley.newRequestQueue(activity);
		// 如果处在联网状态，则发送至服务器
		isConnect = (Boolean) app.getMap().get(Constants.IS_CONNECT_SERVICE);

		SharedPreferences sp = activity.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		boolean isFirst = sp.getBoolean(Constants.FISRTOPENPLAN, true);
		if (isFirst) {
			XUtils.initAthletes(activity, false);
		}
		if (isConnect && isFirst) {
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
		public SparseIntArray visiblecheck;// 用来记录是否显示checkBox
		public SparseBooleanArray ischeck;
		private TextView tips;
		private List<String> athleteName = new ArrayList<String>();

		public ViewPlanAdapter(Context context, TextView tips) {
			this.context = context;
			this.tips = tips;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			visiblecheck = new SparseIntArray();
			ischeck = new SparseBooleanArray();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return plans.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return plans.get(position);
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

			holder.txt.setText(plans.get(position).getName());

			holder.checkBox.setChecked(ischeck.get(position));
			holder.checkBox.setVisibility(visiblecheck.get(position));

			convertView.setOnLongClickListener(new Onlongclick());
			convertView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					ViewPlanHolder planHolder = (ViewPlanHolder) v.getTag();
					if (isMulChoice) {
						if (planHolder.checkBox.isChecked()) {
							planHolder.checkBox.setChecked(false);
							selectid.remove(plans.get(position));
						} else {
							planHolder.checkBox.setChecked(true);
							selectid.add(plans.get(position));
						}
						tips.setText("共选择了" + selectid.size() + "项");

					} else {
						createDialog(position);
					}
				}
			});

			return convertView;
		}

		private void createDialog(int position) {
			final NiftyDialogBuilder selectDialog = NiftyDialogBuilder
					.getInstance(activity);
			Effectstype effect = Effectstype.Fall;
			selectDialog.setCustomView(R.layout.dialog_view_plan, activity);
			Window window = selectDialog.getWindow();
			TextView length = (TextView) window
					.findViewById(R.id.view_plan_pool_length);
			TextView times = (TextView) window
					.findViewById(R.id.view_plan_times);
			length.setText(plans.get(position).getPool());
			times.setText(plans.get(position).getTime() + "");
			ListView viewList = (ListView) window
					.findViewById(R.id.planlist_view);
			long plan_id = plans.get(position).getId();
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

		}

		class Onlongclick implements OnLongClickListener {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				isMulChoice = true;
				selectid.clear();
				relative.setVisibility(View.VISIBLE);
				for (int i = 0; i < plans.size(); i++) {
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
						Log.i("getPlans", response);
						loadingDialog.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(response);
							int resCode = (Integer) jsonObject.get("resCode");
							if (resCode == 1) {
								String jsonString = jsonObject.get("plan")
										.toString();
								List<Plan> plans = JsonTools.getObjects(
										jsonString, Athlete.class);
								// 将从服务器获取的运动员信息保存到本地数据库
								for (Plan plan : plans) {
									plan.save();
								}
							} else if (resCode == 2) {
								XUtils.showToast(activity, mToast,
										SYNCHRONOUS_SUCCESS);
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

			// @Override
			// protected Map<String, String> getParams() throws AuthFailureError
			// {
			// // 设置请求参数
			// Map<String, String> map = new HashMap<String, String>();
			// map.put("deletePlansJson", jsonString);
			// return map;
			// }

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

}
