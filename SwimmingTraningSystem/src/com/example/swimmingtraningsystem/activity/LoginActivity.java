package com.example.swimmingtraningsystem.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.effect.Effectstype;
import com.example.swimmingtraningsystem.effect.NiftyDialogBuilder;
import com.example.swimmingtraningsystem.model.User;
import com.example.swimmingtraningsystem.util.XUtils;
import com.example.swimmingtraningsystem.view.LoadingDialog;

public class LoginActivity extends Activity {

	private MyApplication app;
	private DBManager dbManager;
	private EditText etLogin;
	private EditText etPassword;
	// private TextView forget;
	private TextView sethost;
	private Toast toast;
	private boolean isConnect = false;
	private String TAG = "swimmingtraningsystem";
	private RequestQueue mQueue;
	private Dialog loadingDialog;
	private Effectstype effect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		intiView();

		initData();
	}

	private void initData() {
		app = (MyApplication) getApplication();
		dbManager = DBManager.getInstance();
		// 检查是否有保存的用户名和密码，如果有就回显
		SharedPreferences sp = getSharedPreferences("loginInfo",
				Context.MODE_PRIVATE);
		String username = sp.getString("username", "");
		etLogin.setText(username);
		String passwrod = sp.getString("password", "");
		etPassword.setText(passwrod);

		mQueue = Volley.newRequestQueue(this);

		boolean isFirst = sp.getBoolean("isFirst", true);
		if (isFirst) {
			User defaulrUser = new User();
			defaulrUser.setId(1L);
			defaulrUser.setUsername("defaultUser");
			defaulrUser.setPassword("123456");
			defaulrUser.save();
			showSettingDialog(LoginActivity.this);
			XUtils.SaveLoginInfo(LoginActivity.this, false);
		}

		// 从SharedPreferences读取服务器地址信息
		SharedPreferences hostSp = getSharedPreferences("loginInfo",
				Context.MODE_PRIVATE);
		XUtils.HOSTURL = hostSp
				.getString("hostInfo",
						"http://192.168.1.230:8080/JsonProject1/servlet/JsonAction?action_flag=");
	}

	private void intiView() {
		// TODO Auto-generated method stub

		etLogin = (EditText) findViewById(R.id.tv_user);
		etPassword = (EditText) findViewById(R.id.tv_password);
		sethost = (TextView) findViewById(R.id.setting_host);
		sethost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSettingDialog(LoginActivity.this);
			}
		});
	}

	/**
	 * 跳转注册页面
	 * 
	 * @param v
	 */
	public void onRegister(View v) {
		Intent i = new Intent(this, RegistAcyivity.class);
		startActivity(i);
	}

	/**
	 * 登录
	 * 
	 * @param v
	 */
	public void onLogin(View v) {
		String loginString = etLogin.getText().toString().trim();
		String passwordString = etPassword.getText().toString().trim();

		if (TextUtils.isEmpty(loginString) || TextUtils.isEmpty(passwordString)) {
			XUtils.showToast(this, toast, "用户名或密码不能为空");
		} else {
			// 保存密码
			XUtils.SaveLoginInfo(this, loginString, passwordString);
			if (!isConnect) {
				if (loadingDialog == null) {
					loadingDialog = LoadingDialog.createDialog(this);
					loadingDialog.setCanceledOnTouchOutside(false);
				}
				loadingDialog.show();
				// 尝试连接服务器，如果连接成功则直接登录
				createNewRequest(loginString, passwordString);
			} else {// 如果连接服务器失败，则会使用离线功能登录，可以保存数据但无法上传

				String result = DBManager.getInstance()
						.getPassword(loginString);
				if ("".equals(result)) {
					XUtils.showToast(this, toast, "帐号或密码错误！");
					return;
				}
				// 可以使用默认帐号和已经注册的帐号
				if (loginString.equals("defaultUser")
						|| passwordString.equals(result)) {

					XUtils.showToast(this, toast, "登陆成功");
					// 将当前用户id保存为全局变量
					User user = dbManager.getUserByName(loginString);
					app.getMap().put("CurrentUser", user.getId());
					Handler handler = new Handler();
					Runnable updateThread = new Runnable() {
						public void run() {
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this,
									MainActivity.class);
							LoginActivity.this.startActivity(intent);
							finish();
						}
					};
					handler.postDelayed(updateThread, 100);
				} else {
					XUtils.showToast(this, toast, "密码错误！");
				}
			}
		}

	}

	// 提交登录请求
	public void createNewRequest(final String s1, final String s2) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "login", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i(TAG, response);
						loadingDialog.dismiss();
						if (response.equals("1")) {
							// 将当前用户id保存为全局变量
							User user = dbManager.getUserByName(s1);
							if (user == null) {
								User loginUser = new User();
								loginUser.setUsername(s1);
								loginUser.setPassword(s2);
								loginUser.save();
								app.getMap().put("CurrentUser",
										loginUser.getId());
							} else {
								app.getMap().put("CurrentUser", user.getId());
							}

							XUtils.showToast(LoginActivity.this, toast, "登陆成功");
							Intent i = new Intent(LoginActivity.this,
									MainActivity.class);
							startActivity(i);
							finish();
						} else {
							XUtils.showToast(LoginActivity.this, toast, "密码错误");
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.e(TAG, error.getMessage());
						loadingDialog.dismiss();
						isConnect = true;
						showUserSelectDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("userName", s1);
				map.put("password", s2);
				return map;
			}

			@Override
			public RetryPolicy getRetryPolicy() {
				// TODO Auto-generated method stub
				// 超时设置
				RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
						DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
				return retryPolicy;
			}
		};

		mQueue.add(stringRequest);
	}

	/**
	 * 找回密码
	 * 
	 * @param v
	 */
	public void onForget(View v) {

	}

	/**
	 * 设置服务器IP地址和端口地址对话框
	 * 
	 * @param context
	 */
	protected void showSettingDialog(Context context) {
		final NiftyDialogBuilder settingDialog = NiftyDialogBuilder
				.getInstance(this);
		effect = Effectstype.Slit;
		settingDialog.withTitle("服务器IP与端口设置").withMessage(null)
				.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(true).withDuration(700)
				// def
				.withEffect(effect)
				// def Effectstype.Slidetop
				.withButton1Text("取消")
				// def gone
				.withButton2Text("完成")
				// def gone
				.setCustomView(R.layout.dialog_setting_host, context)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						settingDialog.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Window window = settingDialog.getWindow();
						final TextView ip = (TextView) window
								.findViewById(R.id.tv_ip);
						final TextView port = (TextView) window
								.findViewById(R.id.tv_port);
						String hostIp = ip.getText().toString().trim();
						String hostPort = port.getText().toString().trim();
						if (TextUtils.isEmpty(hostIp)
								|| TextUtils.isEmpty(hostPort)) {
							XUtils.showToast(LoginActivity.this, toast,
									"ip与端口地址均不可为空！");
						} else {
							String hostUrl = "http://"
									+ hostIp
									+ ":"
									+ hostPort
									+ "/JsonProject1/servlet/JsonAction?action_flag=";
							// 保存服务器ip和端口地址到sp
							XUtils.HOSTURL = hostUrl;
							XUtils.SaveLoginInfo(LoginActivity.this, hostUrl);

							XUtils.showToast(LoginActivity.this, toast, "设置成功!");
							settingDialog.dismiss();
						}
					}
				}).show();

	}

	/**
	 * 用户无法连接服务器时弹出该对话框，选择默认帐号或已注册的帐号进行登录使用
	 */
	private void showUserSelectDialog() {
		final NiftyDialogBuilder userDialog = NiftyDialogBuilder
				.getInstance(this);
		effect = Effectstype.SlideBottom;
		userDialog.withTitle("无法连接服务器！")
				.withMessage("如果要继续使用，未注册请选择系统默认帐号登录，已注册请用注册帐号登录")
				.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(true).withDuration(700)
				// def
				.withEffect(effect).withButton1Text("默认帐号登录")
				// def gone
				.withButton2Text("已注册帐号登录")
				// def gone
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						etLogin.setText("defaultUser");
						etLogin.setEnabled(false);
						etPassword.setText("123456");
						etPassword.setEnabled(false);
						userDialog.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						etLogin.setText("");
						etPassword.setText("");
						userDialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
