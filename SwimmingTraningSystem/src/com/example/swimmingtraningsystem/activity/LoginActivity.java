package com.example.swimmingtraningsystem.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.effect.Effectstype;
import com.example.swimmingtraningsystem.effect.NiftyDialogBuilder;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.User;
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.util.XUtils;
import com.example.swimmingtraningsystem.view.LoadingDialog;

/**
 * @author LittleByte
 * 
 */
public class LoginActivity extends Activity {

	private MyApplication app;
	private DBManager dbManager;
	private EditText etLogin;
	private EditText etPassword;
	// private TextView forget;
	private TextView sethost;
	private Toast toast;
	private RequestQueue mQueue;
	private LoadingDialog loadingDialog;
	private Effectstype effect;
	/**
	 * 默认用户帐号
	 */
	private static final String DEFAULT_USERNAME = "defaultUser";
	/**
	 * 默认用户的密码
	 */
	private static final String DEFAULT_PASSWORD = "123456asdjkl";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		intiView();
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		app = (MyApplication) getApplication();
		app.addActivity(this);
		dbManager = DBManager.getInstance();
		// 检查是否有保存的用户名和密码，如果有就回显
		SharedPreferences sp = getSharedPreferences(Constants.LOGININFO,
				Context.MODE_PRIVATE);
		String username = sp.getString("username", DEFAULT_USERNAME);
		etLogin.setText(username);
		String passwrod = sp.getString("password", DEFAULT_PASSWORD);
		etPassword.setText(passwrod);
		mQueue = Volley.newRequestQueue(this);
		boolean isFirst = sp.getBoolean("isFirst", true);
		if (isFirst) {
			User defaulrUser = new User();
			defaulrUser.setId(1L);
			defaulrUser.setUsername(DEFAULT_USERNAME);
			defaulrUser.setPassword(DEFAULT_PASSWORD);
			defaulrUser.save();
			showSettingDialog(LoginActivity.this);
			XUtils.SaveLoginInfo(LoginActivity.this, false);
		}

		// 从SharedPreferences读取服务器地址信息
		SharedPreferences hostSp = getSharedPreferences(Constants.LOGININFO,
				Context.MODE_PRIVATE);
		XUtils.HOSTURL = hostSp
				.getString("hostInfo",
						"http://192.168.1.230:8080/SWIMYES/httpPost.action?action_flag=");
	}

	/**
	 * 初始化视图
	 */
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
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	/**
	 * 登录响应
	 * 
	 * @param v
	 */
	public void onLogin(View v) {
		if (XUtils.isFastDoubleClick()) {
			return;
		} else {
			String loginString = etLogin.getText().toString().trim();
			String passwordString = etPassword.getText().toString().trim();
			if (TextUtils.isEmpty(loginString)
					|| TextUtils.isEmpty(passwordString)) {
				XUtils.showToast(this, toast, "用户名或密码不能为空");
			} else {
				// 保存登录信息
				XUtils.SaveLoginInfo(this, loginString, passwordString);
				boolean tryConnect = (Boolean) app.getMap().get(
						Constants.IS_CONNECT_SERVICE);
				if (tryConnect) {
					if (loadingDialog == null) {
						loadingDialog = LoadingDialog.createDialog(this);
						loadingDialog.setMessage("正在登录...");
						loadingDialog.setCanceledOnTouchOutside(false);
					}
					loadingDialog.show();
					// 尝试连接服务器，如果连接成功则直接登录
					loginRequest(loginString, passwordString);
				}
			}
		}

	}

	/**
	 * 提交登录请求
	 * 
	 * @param s1
	 *            用户名
	 * @param s2
	 *            密码
	 */
	public void loginRequest(final String s1, final String s2) {

		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "login", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i(Constants.TAG, response);
						loadingDialog.dismiss();
						try {
							JSONObject obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								XUtils.showToast(LoginActivity.this, toast,
										"登录成功");
								String userJson = obj.get("user").toString();
								User user = JsonTools.getObject(userJson,
										User.class);

								if (dbManager.getUserByName(user.getUsername()) == null) {
									// 如果数据库中不存在该用户，则直接将该用户保存至数据库
									user.save();
									app.getMap().put(Constants.CURRENT_USER_ID,
											user.getId());
								} else {
									// 如果该用户信息已存在本地数据库，则取出当前id作为全局变量
									long currentId = dbManager.getUserByName(
											user.getUsername()).getId();
									app.getMap().put(Constants.CURRENT_USER_ID,
											currentId);
								}
							} else if (resCode == 2) {
								XUtils.showToast(LoginActivity.this, toast,
										"用户名不存在！");
							} else if (resCode == 3) {
								XUtils.showToast(LoginActivity.this, toast,
										"密码错误！");
							} else {
								XUtils.showToast(LoginActivity.this, toast,
										"服务器错误！");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						XUtils.showToast(LoginActivity.this, toast, "登陆成功");
						Intent i = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(i);
//						finish();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// Log.e(TAG, error.getMessage());
						loadingDialog.dismiss();
						app.getMap().put(Constants.IS_CONNECT_SERVICE, false);
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

		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
				.withEffect(effect).withButton1Text(Constants.CANCLE_STRING)
				.withButton2Text("完成")
				.setCustomView(R.layout.dialog_setting_host, context);
		SharedPreferences hostSp = getSharedPreferences(Constants.LOGININFO,
				Context.MODE_PRIVATE);
		String ip = hostSp.getString("ip", "192.168.1.161");
		String port = hostSp.getString("port", "8080");
		Window window = settingDialog.getWindow();
		final TextView tv_ip = (TextView) window.findViewById(R.id.tv_ip);
		final TextView tv_port = (TextView) window.findViewById(R.id.tv_port);
		tv_ip.setText(ip);
		tv_port.setText(port);

		settingDialog.setButton1Click(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				settingDialog.dismiss();
			}
		}).setButton2Click(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String hostIp = tv_ip.getText().toString().trim();
				String hostPort = tv_port.getText().toString().trim();
				if (TextUtils.isEmpty(hostIp) || TextUtils.isEmpty(hostPort)) {
					XUtils.showToast(LoginActivity.this, toast, "ip与端口地址均不可为空！");
				} else {

					String hostUrl = "http://" + hostIp + ":" + hostPort
							+ "/SWIMYES/httpPost.action?action_flag=";
					// 保存服务器ip和端口地址到sp
					XUtils.HOSTURL = hostUrl;
					XUtils.SaveLoginInfo(LoginActivity.this, hostUrl, hostIp,
							hostPort);
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
		userDialog
				.withTitle("无法连接服务器！")
				.withMessage(
						"如果要继续使用，未注册请选择系统默认帐号登录，已注册请用注册帐号登录\n"
								+ "注意：默认账号只能试用，数据无法上传至服务器！")
				.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(false).withDuration(700)
				// def
				.withEffect(effect).withButton1Text("默认帐号登录")
				// def gone
				.hideOneButton()
				// def gone
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						etLogin.setText("defaultUser");
						etLogin.setEnabled(false);
						etPassword.setText("123456asdjkl");
						etPassword.setEnabled(false);
						userDialog.dismiss();
						offlineLogin();
					}
				}).show();

	}

	/**
	 * 离线登录
	 */
	private void offlineLogin() {
		// 连接服务器失败，则会使用离线功能登录，可以保存数据但暂时无法上传,只是功能试用
		XUtils.showToast(LoginActivity.this, toast, "登陆成功,立即跳转");
		// 将当前用户id保存为全局变量
		User user = dbManager.getUserByName("defaultUser");
		app.getMap().put(Constants.CURRENT_USER_ID, user.getId());
		Handler handler = new Handler();
		Runnable updateThread = new Runnable() {
			public void run() {
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				LoginActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_left_out);
//				 finish();
			}
		};
		handler.postDelayed(updateThread, 800);
	}

}
