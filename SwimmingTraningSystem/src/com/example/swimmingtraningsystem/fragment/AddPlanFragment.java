package com.example.swimmingtraningsystem.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.activity.MyApplication;
import com.example.swimmingtraningsystem.adapter.AddPlanListAdapter;
import com.example.swimmingtraningsystem.adapter.ChoseAthleteAdapter;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.model.PlanHolder;
import com.example.swimmingtraningsystem.model.User;
import com.example.swimmingtraningsystem.util.XUtils;

public class AddPlanFragment extends Fragment implements OnClickListener {
	private MyApplication app;
	private Activity activity;
	private AlertDialog alertDialog;
	private ListView listView;
	private List<Athlete> athletes;
	private ChoseAthleteAdapter adapter;
	private ListView planlv;
	private AddPlanListAdapter planAdapter;
	private EditText planName;
	private User us;
	private DBManager dbManager;
	/**
	 * 要显示在plan_activity的数据集
	 */
	private List<Athlete> planList = new ArrayList<Athlete>();
	private HashMap<Long, Boolean> map;
	private Spinner distanceSpinner;
	private Spinner poolSpinner;
	private long newId;
	private RequestQueue mQueue;
	private String jsonStr = null;
	private Toast toast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_add_plan, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setupView();
		initData();

	}

	private void setupView() {
		activity = getActivity();
		app = (MyApplication) activity.getApplication();
		dbManager = DBManager.getInstance();
		List<String> poolLength = new ArrayList<String>();
		List<String> ditances = new ArrayList<String>();
		poolLength.add("25米池");
		poolLength.add("50米池");
		ditances.add("1趟");
		ditances.add("2趟");
		ditances.add("3趟");
		ditances.add("4趟");
		ditances.add("5趟");
		ditances.add("6趟");
		ditances.add("7趟");
		ditances.add("8趟");
		poolSpinner = (Spinner) activity.findViewById(R.id.pool_length);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_item, poolLength);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		poolSpinner.setAdapter(adapter1);
		distanceSpinner = (Spinner) activity.findViewById(R.id.times);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_item, ditances);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		distanceSpinner.setAdapter(adapter2);

		long userID = (Long) app.getMap().get("CurrentUser");
		us = dbManager.getUser(userID);
		planlv = (ListView) activity.findViewById(R.id.plan_lsit);
		athletes = dbManager.getAthletes(userID);
		map = new HashMap<Long, Boolean>();
		planName = (EditText) activity.findViewById(R.id.plan_name);
		newId = dbManager.getLatestPlanId() + 1;
		planName.setText("计划_" + newId);
		activity.findViewById(R.id.select_ath).setOnClickListener(this);
		activity.findViewById(R.id.save_plan).setOnClickListener(this);
		mQueue = Volley.newRequestQueue(activity);
	}

	// 初始化Map的数据
	private void initData() {
		for (int i = 0; i < athletes.size(); i++) {
			map.put(athletes.get(i).getId(), false);
		}
	}

	private void createDialog() {
		alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.setView(View.inflate(activity,
				R.layout.dialog_choose_athlete, null));
		alertDialog.show();
	}

	public void select() {
		createDialog();
		Window window = alertDialog.getWindow();
		listView = (ListView) window.findViewById(R.id.choose_list);
		adapter = new ChoseAthleteAdapter(activity, athletes, map);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				PlanHolder holder = (PlanHolder) arg1.getTag();
				// 改变CheckBox的状态
				holder.cb.toggle();

				if (holder.cb.isChecked()) {
					if (!planList
							.contains(adapter.getChooseAthlete().get(arg2)))
						// 如果checkbox已选并且planList中无该项
						planList.add(adapter.getChooseAthlete().get(arg2));
				} else {
					// 如果checkbox不选择并且planList中有该项
					if (planList.contains(adapter.getChooseAthlete().get(arg2)))
						planList.remove(adapter.getChooseAthlete().get(arg2));
				}

				// 将CheckBox的选中状况记录下来
				map.put(athletes.get(arg2).getId(), holder.cb.isChecked());
			}
		});

		Button back = (Button) window.findViewById(R.id.choose_back);
		Button add_ok = (Button) window.findViewById(R.id.choose);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});

		add_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				planAdapter = new AddPlanListAdapter(activity, planList, map);
				planlv.setAdapter(planAdapter);
				alertDialog.dismiss();
			}
		});

		adapter.notifyDataSetChanged();
	}

	public void save() {
		String pl_name = planName.getText().toString().trim();
		String poolScale = (String) poolSpinner.getSelectedItem();
		int distance = distanceSpinner.getSelectedItemPosition() + 1;

		if (TextUtils.isEmpty(pl_name)) {
			XUtils.showToast(activity, toast, "计划名字不能为空！");
			return;
		} else if (dbManager.isNameExsit(us.getId(), pl_name)) {
			XUtils.showToast(activity, toast, "计划名字已存在！");
			return;
		} else if (planList.size() == 0) {
			XUtils.showToast(activity, toast, "该计划没有添加任何运动员！");
			return;
		} else {
			Plan p = new Plan();
			p.setName(pl_name);
			p.setPool(poolScale);
			p.setTime(distance);
			p.setUser(us);
			p.setAthlete(planList);
			// 在存入数据库之前就生存Json字符串，否则会报错！
			jsonStr = JsonTools.creatJsonString(p);
			if ((Boolean) app.getMap().get("isConnect")) {
				// 如果可以连接服务器，则提交请求
				addPlanRequest();
			} else {
				// 否则将数据保存本地使用
				p.save();
				XUtils.showToast(activity, toast, "计划添加成功！");
			}
			activity.finish();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.select_ath:
			select();
			break;
		case R.id.save_plan:
			save();
			break;
		default:
			break;
		}
	}

	public void addPlanRequest() {
		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "addPlan", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								XUtils.showToast(activity, toast, "添加成功");
								String userJson = obj.get("plan").toString();
								Plan pl = JsonTools.getObject(userJson,
										Plan.class);
								pl.save();
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
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("planJson", jsonStr);
				return map;
			}

			@Override
			public RetryPolicy getRetryPolicy() {
				// TODO Auto-generated method stub
				// 超时设置
				RetryPolicy retryPolicy = new DefaultRetryPolicy(
						XUtils.SOCKET_TIMEOUT,
						DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
				return retryPolicy;
			}
		};

		mQueue.add(stringRequest);
	}
}
