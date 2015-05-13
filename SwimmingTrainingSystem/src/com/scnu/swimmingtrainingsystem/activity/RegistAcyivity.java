package com.scnu.swimmingtrainingsystem.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

/**
 * 注册Activity
 * 
 * @author LittleByte
 * 
 */
public class RegistAcyivity extends Activity {
	private MyApplication app;
	private String TAG = "swimmingtraningsystem";
	private EditText username;
	private EditText password;
	private EditText password1;
	private EditText email;
	private EditText phone;
	private RequestQueue mQueue;
	private Toast toast;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist);

		app = (MyApplication) getApplication();
		username = (EditText) findViewById(R.id.et_userID);
		password = (EditText) findViewById(R.id.et_password);
		password1 = (EditText) findViewById(R.id.et_password1);
		email = (EditText) findViewById(R.id.et_email);
		phone = (EditText) findViewById(R.id.et_phone);
		mQueue = Volley.newRequestQueue(this);

		SharedPreferences hostSp = getSharedPreferences(Constants.LOGININFO,
				Context.MODE_PRIVATE);
		// 保存服务器ip和端口地址到sp
		CommonUtils.HOSTURL = hostSp.getString("hostInfo", "");

	}

	public void getback(View v) {
		finish();
	}

	/**
	 * 注册响应
	 * 
	 * @param v
	 */
	public void quickRegist(View v) {
		final String user = username.getText().toString().trim();
		final String pass = password.getText().toString().trim();
		final String pass1 = password1.getText().toString().trim();
		final String Email = email.getText().toString().trim();
		final String cellphone = phone.getText().toString().trim();
		if (TextUtils.isEmpty(user)) {
			CommonUtils.showToast(this, toast, "用户名不能为空");
		} else if (TextUtils.isEmpty(pass)) {
			CommonUtils.showToast(this, toast, "密码不能为空");
		} else if (TextUtils.isEmpty(pass1) || !pass.equals(pass1)) {
			CommonUtils.showToast(this, toast, "两次输入密码不一致");
		} else if (!TextUtils.isEmpty(Email)
				&& !CommonUtils.isEmail(email.getText().toString().trim())) {
			CommonUtils.showToast(this, toast, "邮箱格式错误");
		} else if (!TextUtils.isEmpty(cellphone)
				&& !CommonUtils.isMobileNO(phone.getText().toString().trim())) {
			CommonUtils.showToast(this, toast, "手机号码格式错误");
		} else {
			// 如果处在联网状态，则发送至服务器
			boolean isConnect = (Boolean) app.getMap().get(
					Constants.IS_CONNECT_SERVER);
			if (isConnect) {
				User newUser = new User();
				newUser.setUsername(user);
				newUser.setPassword(pass);
				newUser.setEmail(Email);
				newUser.setPhone(cellphone);
				// 发送至服务器
				registRequest(newUser);
			} else {
				CommonUtils.showToast(RegistAcyivity.this, toast,
						"无法连接服务器！请直接使用默认账号登陆试用");
			}
		}

	}

	/**
	 * 注册请求
	 * 
	 * @param jsonString
	 */
	private void registRequest(final User user) {
		if (loadingDialog == null) {
			loadingDialog = LoadingDialog.createDialog(this);
			loadingDialog.setMessage("正在注册...");
			loadingDialog.setCanceledOnTouchOutside(false);
		}
		loadingDialog.show();

		final String jsonInfo = JsonTools.creatJsonString(user);

		StringRequest stringRequest = new StringRequest(Method.POST,
				CommonUtils.HOSTURL + "regist", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i(TAG, response);
						loadingDialog.dismiss();
						try {
							JSONObject obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								CommonUtils.showToast(RegistAcyivity.this,
										toast, "注册成功");
								String uid = obj.get("uid").toString();
								user.setUid(Integer.parseInt(uid));
								user.save();
								overridePendingTransition(R.anim.slide_up_in,
										R.anim.slide_down_out);
								finish();
							} else if (resCode == 2) {
								CommonUtils.showToast(RegistAcyivity.this,
										toast, "用户名已经存在！");
							} else {
								CommonUtils.showToast(RegistAcyivity.this,
										toast, "服务器错误！");
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
						CommonUtils.showToast(RegistAcyivity.this, toast,
								"无法连接服务器！请使用默认账号试用");
						app.getMap().put(Constants.IS_CONNECT_SERVER, false);
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("registJson", jsonInfo);
				return map;
			}

		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			return false;
		}
		return false;
	}

}
