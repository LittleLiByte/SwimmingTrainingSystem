package com.scnu.swimmingtrainingsystem.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
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
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

public class RetrievePasswordActivity extends Activity {
	private RequestQueue mQueue;
	private EditText emailEditText;
	private Toast mToast;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retrieve_password);
		mQueue = Volley.newRequestQueue(this);
		emailEditText = (EditText) findViewById(R.id.edt_email);
	}

	public void back(View v) {
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	public void sendEmail(View v) {
		String emailAdress = emailEditText.getText().toString().trim();
		if (loadingDialog == null) {
			loadingDialog = LoadingDialog.createDialog(this);
			loadingDialog.setMessage("正在发送请求...");
			loadingDialog.setCanceledOnTouchOutside(false);
		}
		loadingDialog.show();
		sendEmailRequest(emailAdress);
	}

	/**
	 * 发送找回密码请求
	 * 
	 * @param s1
	 *            邮箱地址
	 */
	public void sendEmailRequest(final String s1) {

		StringRequest sendEmailRequest = new StringRequest(Method.POST,
				CommonUtils.HOSTURL + "getPassword", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i(Constants.TAG, response);
						loadingDialog.dismiss();
						try {
							JSONObject obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								CommonUtils.showToast(
										RetrievePasswordActivity.this, mToast,
										"发送成功，请稍后查看邮箱");
							} else if (resCode == 0) {
								CommonUtils.showToast(
										RetrievePasswordActivity.this, mToast,
										"该用户还未注册");
							} else {
								CommonUtils.showToast(
										RetrievePasswordActivity.this, mToast,
										"服务器错误！");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Handler handler = new Handler();
						Runnable updateThread = new Runnable() {
							public void run() {
								finish();
								overridePendingTransition(R.anim.in_from_left,
										R.anim.out_to_right);
							}
						};
						handler.postDelayed(updateThread, 800);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// Log.e(TAG, error.getMessage());
						loadingDialog.dismiss();
						CommonUtils.showToast(RetrievePasswordActivity.this,
								mToast, "无法连接服务器！");
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("email", s1);
				return map;
			}

		};
		sendEmailRequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(sendEmailRequest);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			return false;
		}
		return false;
	}

}
