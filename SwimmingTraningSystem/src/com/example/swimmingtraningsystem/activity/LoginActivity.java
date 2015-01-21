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
	private String TAG = "swimmingtraningsystem";
	private RequestQueue mQueue;
	private LoadingDialog loadingDialog;
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
		// ����Ƿ��б�����û��������룬����оͻ���
		SharedPreferences sp = getSharedPreferences("loginInfo",
				Context.MODE_PRIVATE);
		String username = sp.getString("username", "defaultUser");
		etLogin.setText(username);
		String passwrod = sp.getString("password", "123456asdjkl");
		etPassword.setText(passwrod);
		mQueue = Volley.newRequestQueue(this);
		boolean isFirst = sp.getBoolean("isFirst", true);
		if (isFirst) {
			User defaulrUser = new User();
			defaulrUser.setId(1L);
			defaulrUser.setUsername("defaultUser");
			defaulrUser.setPassword("123456asdjkl");
			defaulrUser.save();
			showSettingDialog(LoginActivity.this);
			XUtils.SaveLoginInfo(LoginActivity.this, false);
		}

		// ��SharedPreferences��ȡ��������ַ��Ϣ
		SharedPreferences hostSp = getSharedPreferences("loginInfo",
				Context.MODE_PRIVATE);
		XUtils.HOSTURL = hostSp
				.getString("hostInfo",
						"http://192.168.1.230:8080/SWIMYES/httpPost.action?action_flag=");
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
	 * ��תע��ҳ��
	 * 
	 * @param v
	 */
	public void onRegister(View v) {
		Intent i = new Intent(this, RegistAcyivity.class);
		startActivity(i);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	/**
	 * ��¼��Ӧ
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
				XUtils.showToast(this, toast, "�û��������벻��Ϊ��");
			} else {
				// ��������
				XUtils.SaveLoginInfo(this, loginString, passwordString);
				boolean tryConnect = (Boolean) app.getMap().get("isConnect");
				if (tryConnect) {
					if (loadingDialog == null) {
						loadingDialog = LoadingDialog.createDialog(this);
						loadingDialog.setMessage("���ڵ�¼...");
						loadingDialog.setCanceledOnTouchOutside(false);
					}
					loadingDialog.show();
					// �������ӷ�������������ӳɹ���ֱ�ӵ�¼
					loginRequest(loginString, passwordString);
				} else {// ������ӷ�����ʧ�ܣ����ʹ�����߹��ܵ�¼�����Ա������ݵ���ʱ�޷��ϴ�,ֻ�ǹ�������

					// ͨ���û�����ѯ���룬��ƥ������ʾ
					String result = dbManager.getPassword(loginString);
					if ("".equals(result)) {
						XUtils.showToast(this, toast, "�û���������");
						return;
					}
					// ����ʹ��Ĭ���ʺź��Ѿ�ע����ʺ�
					if (passwordString.equals(result)) {
						XUtils.showToast(this, toast, "��½�ɹ�");
						// ����ǰ�û�id����Ϊȫ�ֱ���
						User user = dbManager.getUserByName(loginString);
						app.getMap().put("CurrentUser", user.getId());
						Handler handler = new Handler();
						Runnable updateThread = new Runnable() {
							public void run() {
								Intent intent = new Intent(LoginActivity.this,
										MainActivity.class);
								LoginActivity.this.startActivity(intent);
								overridePendingTransition(R.anim.push_right_in,
										R.anim.push_left_out);
								finish();
							}
						};
						handler.postDelayed(updateThread, 100);
					} else {
						XUtils.showToast(this, toast, "�������");
					}
				}
			}
		}

	}

	// �ύ��¼����
	public void loginRequest(final String s1, final String s2) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "login", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i(TAG, response);
						loadingDialog.dismiss();
						try {
							JSONObject obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								XUtils.showToast(LoginActivity.this, toast,
										"��¼�ɹ�");
								String userJson = obj.get("user").toString();
								User user = JsonTools.getObject(userJson,
										User.class);

								if (dbManager.getUserByName(user.getUsername()) == null) {
									// ������ݿ��в����ڸ��û�����ֱ�ӽ����û����������ݿ�
									user.save();
									app.getMap().put("CurrentUser",
											user.getId());
								} else {
									// ������û���Ϣ�Ѵ��ڱ������ݿ⣬��ȡ����ǰid��Ϊȫ�ֱ���
									long currentId = dbManager.getUserByName(
											user.getUsername()).getId();
									app.getMap().put("CurrentUser", currentId);
								}
							} else if (resCode == 2) {
								XUtils.showToast(LoginActivity.this, toast,
										"�û��������ڣ�");
							} else if (resCode == 3) {
								XUtils.showToast(LoginActivity.this, toast,
										"�������");
							} else {
								XUtils.showToast(LoginActivity.this, toast,
										"����������");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						XUtils.showToast(LoginActivity.this, toast, "��½�ɹ�");
						Intent i = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(i);
						finish();
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// Log.e(TAG, error.getMessage());
						loadingDialog.dismiss();
						app.getMap().put("isConnect", false);
						showUserSelectDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// �����������
				Map<String, String> map = new HashMap<String, String>();
				map.put("userName", s1);
				map.put("password", s2);
				return map;
			}

		};
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(1500,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest);
	}

	/**
	 * �һ�����
	 * 
	 * @param v
	 */
	public void onForget(View v) {

	}

	/**
	 * ���÷�����IP��ַ�Ͷ˿ڵ�ַ�Ի���
	 * 
	 * @param context
	 */
	protected void showSettingDialog(Context context) {

		final NiftyDialogBuilder settingDialog = NiftyDialogBuilder
				.getInstance(this);
		effect = Effectstype.Slit;
		settingDialog.withTitle("������IP��˿�����").withMessage(null)
				.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(true).withDuration(700)
				.withEffect(effect).withButton1Text("ȡ��").withButton2Text("���")
				.setCustomView(R.layout.dialog_setting_host, context);
		SharedPreferences hostSp = getSharedPreferences("loginInfo",
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
					XUtils.showToast(LoginActivity.this, toast, "ip��˿ڵ�ַ������Ϊ�գ�");
				} else {

					String hostUrl = "http://" + hostIp + ":" + hostPort
							+ "/SWIMYES/httpPost.action?action_flag=";
					// ���������ip�Ͷ˿ڵ�ַ��sp
					XUtils.HOSTURL = hostUrl;
					XUtils.SaveLoginInfo(LoginActivity.this, hostUrl, hostIp,
							hostPort);
					XUtils.showToast(LoginActivity.this, toast, "���óɹ�!");
					settingDialog.dismiss();
				}
			}
		}).show();

	}

	/**
	 * �û��޷����ӷ�����ʱ�����öԻ���ѡ��Ĭ���ʺŻ���ע����ʺŽ��е�¼ʹ��
	 */
	private void showUserSelectDialog() {
		final NiftyDialogBuilder userDialog = NiftyDialogBuilder
				.getInstance(this);
		effect = Effectstype.SlideBottom;
		userDialog
				.withTitle("�޷����ӷ�������")
				.withMessage(
						"���Ҫ����ʹ�ã�δע����ѡ��ϵͳĬ���ʺŵ�¼����ע������ע���ʺŵ�¼\n"
								+ "ע�⣺Ĭ���˺�ֻ�����ã������޷��ϴ�����������")
				.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(false).withDuration(700)
				// def
				.withEffect(effect).withButton1Text("Ĭ���ʺŵ�¼")
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
					}
				}).show();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
