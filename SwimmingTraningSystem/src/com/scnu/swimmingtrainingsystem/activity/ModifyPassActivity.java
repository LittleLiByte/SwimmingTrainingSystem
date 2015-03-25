package com.scnu.swimmingtrainingsystem.activity;

import java.util.HashMap;
import java.util.Map;

import com.scnu.swimmingtrainingsystem.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;

public class ModifyPassActivity extends Activity {
	private MyApplication app;
	private DBManager dbManager;
	private EditText modify_oldpass;
	private EditText modify_newpass;
	private EditText modify_comfirmpass;
	private RequestQueue mQueue;
	private Toast toast;
	private Long userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify);
		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	private void init() {
		app = (MyApplication) getApplication();
		userId = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		dbManager = DBManager.getInstance();
		modify_oldpass = (EditText) findViewById(R.id.modify_oldpass);
		modify_newpass = (EditText) findViewById(R.id.modify_newpass);
		modify_comfirmpass = (EditText) findViewById(R.id.modify_comfirmpass);
		mQueue = Volley.newRequestQueue(this);
	}

	/**
	 * 响应修改密码事件
	 * 
	 * @param v
	 */
	public void modify(View v) {
		String oldPassword = modify_oldpass.getText().toString().trim();
		String newPassword = modify_newpass.getText().toString().trim();
		String comfPassword = modify_comfirmpass.getText().toString().trim();

		String name = dbManager.getUser(userId).getUsername();
		String userPass = dbManager.getUser(userId).getPassword();
		if (TextUtils.isEmpty(oldPassword)) {
			CommonUtils.showToast(this, toast, "原密码不能为空！");
		} else if (TextUtils.isEmpty(newPassword)) {
			CommonUtils.showToast(this, toast, "新密码不能为空！");
		} else if (TextUtils.isEmpty(comfPassword)) {
			CommonUtils.showToast(this, toast, "确认密码不能为空！");
		} else if (!userPass.equals(oldPassword)) {
			CommonUtils.showToast(this, toast, "原密码错误！");
		} else if (userPass.equals(newPassword)) {
			CommonUtils.showToast(this, toast, "原密码与新密码相同，无需修改！");
		} else if (name.equals("defaultUser")) {
			CommonUtils.showToast(this, toast, "当前为系统默认帐号，不能修改密码！");
		} else {
			dbManager.modifyUserPassword(userId, comfPassword);
			CommonUtils.showToast(this, toast, "修改密码成功！");
			// 如果处在联网状态，则发送至服务器
			boolean isConnect = (Boolean) app.getMap().get(
					Constants.IS_CONNECT_SERVICE);
			if (isConnect) {
				// 发送至服务器
				modifyRequest(oldPassword, newPassword, comfPassword);
			}
			finish();
		}
	}

	/**
	 * 创建修改密码请求
	 * 
	 * @param oldPassword
	 *            旧密码
	 * @param newPassword
	 *            新密码
	 * @param comfPassword
	 *            确认新密码
	 */
	public void modifyRequest(final String oldPassword,
			final String newPassword, final String comfPassword) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				CommonUtils.HOSTURL + "modifyPass", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("modifyPass", response);
						finish();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.e("modifyPass", error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("oldPass", oldPassword);
				map.put("newPass", newPassword);
				map.put("comfrim", comfPassword);
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
	 * 退出当前窗体
	 * 
	 * @param v
	 */
	public void getback(View v) {
		finish();
	}
}
