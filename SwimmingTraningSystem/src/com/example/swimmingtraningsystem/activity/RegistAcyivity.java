package com.example.swimmingtraningsystem.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.User;
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.util.XUtils;
import com.example.swimmingtraningsystem.view.LoadingDialog;

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
	}

	public void getback(View v) {
		finish();
	}

	/**
	 * 注册响应
	 * 
	 * @param v
	 */
	public void quick_regist(View v) {
		if (XUtils.isFastDoubleClick()) {
			return;
		} else {
			final String user = username.getText().toString().trim();
			final String pass = password.getText().toString().trim();
			final String pass1 = password1.getText().toString().trim();
			final String Email = email.getText().toString().trim();
			final String cellphone = phone.getText().toString().trim();
			if (TextUtils.isEmpty(user)) {
				XUtils.showToast(this, toast, "用户名不能为空");
			} else if (TextUtils.isEmpty(pass)) {
				XUtils.showToast(this, toast, "密码不能为空");
			} else if (TextUtils.isEmpty(pass1) || !pass.equals(pass1)) {
				XUtils.showToast(this, toast, "两次输入密码不一致");
			} else {
				// 如果处在联网状态，则发送至服务器
				boolean isConnect = (Boolean) app.getMap().get(
						Constants.IS_CONNECT_SERVICE);
				if (isConnect) {
					User newUser = new User();
					newUser.setUsername(user);
					newUser.setPassword(pass);
					newUser.setEmail(Email);
					newUser.setPhone(cellphone);
					String jsonInfo = JsonTools.creatJsonString(newUser);
					// 发送至服务器
					registRequest(jsonInfo);
				}
			}
		}

	}

	/**
	 * 注册请求
	 * 
	 * @param jsonString
	 */
	private void registRequest(final String jsonString) {
		if (loadingDialog == null) {
			loadingDialog = LoadingDialog.createDialog(this);
		}
		loadingDialog.show();
		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "regist", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i(TAG, response);
						loadingDialog.dismiss();
						try {
							JSONObject obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								XUtils.showToast(RegistAcyivity.this, toast,
										"注册成功");
								String userJson = obj.get("user").toString();
								User user = JsonTools.getObject(userJson,
										User.class);
								user.save();
								overridePendingTransition(R.anim.slide_up_in,
										R.anim.slide_down_out);
								finish();
							} else if (resCode == 2) {
								XUtils.showToast(RegistAcyivity.this, toast,
										"用户名已经存在！");
							} else {
								XUtils.showToast(RegistAcyivity.this, toast,
										"服务器错误！");
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
						XUtils.showToast(RegistAcyivity.this, toast,
								"无法连接服务器！请使用默认账号试用");

					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("registJson", jsonString);
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
