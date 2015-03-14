package com.example.swimmingtraningsystem.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.activity.LoginActivity;
import com.example.swimmingtraningsystem.activity.MyApplication;
import com.example.swimmingtraningsystem.adapter.AddPlanListAdapter;
import com.example.swimmingtraningsystem.adapter.ChoseAthleteAdapter;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.effect.Effectstype;
import com.example.swimmingtraningsystem.effect.NiftyDialogBuilder;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.model.PlanHolder;
import com.example.swimmingtraningsystem.model.User;
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.util.XUtils;

/**
 * 添加计划Fragment
 * 
 * @author LittleByte
 * 
 */
public class AddPlanFragment extends Fragment implements OnClickListener {
	private MyApplication app;
	private Activity activity;
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
		try {
			initView();
			initData();
		} catch (Exception e) {
			e.printStackTrace();
			startActivity(new Intent(getActivity(), LoginActivity.class));
		}
	}

	private void initView() {
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

		long userID = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
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

	/**
	 * 初始化是否选中Map的数据
	 */
	private void initData() {
		for (int i = 0; i < athletes.size(); i++) {
			map.put(athletes.get(i).getId(), false);
		}
	}

	/**
	 * 选择运动员加入到新建的计划当中
	 */
	public void selectAthletes() {
		final NiftyDialogBuilder selectDialog = NiftyDialogBuilder
				.getInstance(activity);
		Effectstype effect = Effectstype.Fall;
		selectDialog.setCustomView(R.layout.dialog_choose_athlete, activity);

		Window window = selectDialog.getWindow();
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
		selectDialog.withTitle("选择运动员").withMessage(null)
				.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(true).withDuration(500)
				.withEffect(effect).withButton1Text("返回")
				.withButton2Text(Constants.OK_STRING)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						selectDialog.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						planAdapter = new AddPlanListAdapter(activity,
								planList, map);
						planlv.setAdapter(planAdapter);
						selectDialog.dismiss();
					}

				}).show();

		adapter.notifyDataSetChanged();
	}

	/**
	 * 保存新建的计划到数据库
	 */
	public void savePlan() {
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

			List<Integer> idList = new ArrayList<Integer>();
			for (int i = 0; i < planList.size(); i++) {
				idList.add(planList.get(i).getAid());
			}

			if ((Boolean) app.getMap().get(Constants.IS_CONNECT_SERVICE)) {
				// 如果可以连接服务器，则提交请求
				Map<String, Object> jsonMap = new HashMap<String, Object>();
				jsonMap.put("name", pl_name);
				jsonMap.put("pool", poolScale);
				jsonMap.put("time", distance);
				jsonMap.put("user", us.getUid());
				jsonMap.put("athlete", idList);
				jsonStr = JsonTools.creatJsonString(jsonMap);
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
			selectAthletes();
			break;
		case R.id.save_plan:
			savePlan();
			break;
		default:
			break;
		}
	}

	/**
	 * 将新增计划请求发送至服务器
	 */
	public void addPlanRequest() {
		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "addPlan", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								XUtils.showToast(activity, toast,
										Constants.ADD_SUCCESS_STRING);
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

		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}
}
