package com.example.swimmingtraningsystem.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.swimmingtraningsystem.adapter.AthleteListAdapter;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.User;
import com.example.swimmingtraningsystem.util.XUtils;

public class AthleteActivity extends Activity {
	private MyApplication app;
	private ListView listView;
	private AlertDialog alertDialog;

	private Toast toast;
	private AthleteListAdapter adapter;
	private List<Athlete> list;
	private RequestQueue mQueue;
	private DBManager dbManager;
	protected String TAG = "com.example.swimmingtraningsystem";
	private User us;
	private long row;
	private Long userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_athlete);
		app = (MyApplication) getApplication();
		dbManager = DBManager.getInstance();
		userId = (Long) app.getMap().get("CurrentUser");
		us = dbManager.getUser(userId);
		listView = (ListView) findViewById(R.id.lv);
		list = dbManager.getAthletes(userId);
		adapter = new AthleteListAdapter(this, list, userId);
		listView.setAdapter(adapter);
		mQueue = Volley.newRequestQueue(this);

	}

	/**
	 * 添加运动员按钮
	 * 
	 * @param v
	 */
	public void add(View v) {
		createDialog();
		Window window = alertDialog.getWindow();
		Button cancel_btn = (Button) window.findViewById(R.id.add_cancle);
		Button success = (Button) window.findViewById(R.id.add_ok);
		final EditText userID = (EditText) window.findViewById(R.id.et_userID);
		final EditText athleteName = (EditText) window
				.findViewById(R.id.add_et_user);
		final EditText athleteAge = (EditText) window
				.findViewById(R.id.add_et_age);
		final EditText athleteContact = (EditText) window
				.findViewById(R.id.add_et_contact);
		final EditText others = (EditText) window
				.findViewById(R.id.add_et_extra);
		row = dbManager.getLatestAthleteId();
		userID.setText(String.format("%1$03d", row + 1));

		success.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = athleteName.getText().toString().trim();
				String ageString = athleteAge.getText().toString().trim();
				String phone = athleteContact.getText().toString().trim();
				String other = others.getText().toString().trim();
				boolean isExit = dbManager.isAthleteNameExsit(userId, name);
				if (TextUtils.isEmpty(name)) {
					XUtils.showToast(AthleteActivity.this, toast, "名字不能为空");
				} else if (isExit) {
					XUtils.showToast(AthleteActivity.this, toast,
							"存在运动员名字重复，请更改！");
				} else if (TextUtils.isEmpty(ageString)) {
					XUtils.showToast(AthleteActivity.this, toast, "年龄不能为空");
				} else {
					int age = Integer.parseInt(ageString);
					addAthlete(row, name, age, phone, other);
					list = dbManager.getAthletes(userId);
					adapter.setDatas(list);
					adapter.notifyDataSetChanged();
					XUtils.showToast(AthleteActivity.this, toast, "添加成功");
					alertDialog.dismiss();

				}

			}
		});
		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
	}

	/**
	 * 保存一个运动员信息到数据库
	 * 
	 * @param number
	 * @param name
	 * @param age
	 * @param contact
	 * @param others
	 */
	public void addAthlete(long id, String name, int age, String contact,
			String others) {

		Athlete a = new Athlete();
		a.setId(id);
		a.setName(name);
		a.setAge(age);
		a.setPhone(contact);
		a.setExtras(others);
		a.setUser(us);

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", id);
			jsonObject.put("name", name);
			jsonObject.put("age", age);
			jsonObject.put("phone", contact);
			jsonObject.put("extras", others);
			jsonObject.put("user", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		createNewRequest(jsonObject.toString());
		dbManager.addAthlete(a);
	}

	/**
	 * 将对象转换成json字符串，提交到服务器
	 * 
	 * @param obj
	 */
	public void createNewRequest(final String athleteJson) {
		StringRequest request = new StringRequest(Method.POST, XUtils.HOSTURL
				+ "addAthlete", new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.i(TAG, response);
				if (response.equals("ok")) {

				} else {

				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.e(TAG, error.getMessage());
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("athleteJson", athleteJson);
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

		mQueue.add(request);
	}

	public void back(View v) {
		finish();
	}

	private void createDialog() {
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setView(getLayoutInflater().inflate(
				R.layout.add_athlete_dialog, null));
		alertDialog.show();
	}

}
