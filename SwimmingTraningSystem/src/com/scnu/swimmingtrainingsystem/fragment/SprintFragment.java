package com.scnu.swimmingtrainingsystem.fragment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.activity.LoginActivity;
import com.scnu.swimmingtrainingsystem.activity.MyApplication;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Upid;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;

/**
 * 测试冲刺成绩的Fragment
 * 
 * @author LittleByte
 * 
 */
public class SprintFragment extends Fragment {
	private static final String UNKNOW_ERROR = "服务器错误";
	private static final String SYNCHRONOUS_SUCCESS = "同步成功！";
	private MyApplication app;
	private Activity activity;
	private DBManager dbManager;
	private ListView listView;
	private RequestQueue mQueue;
	private LoadingDialog loadingDialog;
	private Boolean isConnect;
	private Toast mToast;
	private Long mUserId;
	private User mUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_sprint, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		try {
			initFragment();
		} catch (Exception e) {
			e.printStackTrace();
			startActivity(new Intent(getActivity(), LoginActivity.class));
		}

	}

	private void initFragment() {
		activity = getActivity();
		app = (MyApplication) activity.getApplication();
		dbManager = DBManager.getInstance();
		mUserId = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		mUser = dbManager.getUser(mUserId);

		listView = (ListView) activity.findViewById(R.id.view_plan_list);
		mQueue = Volley.newRequestQueue(activity);
		// 如果处在联网状态，则发送至服务器
		isConnect = (Boolean) app.getMap().get(Constants.IS_CONNECT_SERVICE);

	}

	private void addDashScoreRequest(List<Upid> upids) {
		// 数据查询出该计划的uid和pid;
		final String jsonString = JsonTools.creatJsonString(upids);

		System.out.println("jsonString--->" + jsonString);
		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "deletePlans", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("ViewPlan", response);
						if (response.equals("ok")) {

						} else {
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.e("ViewPlan", error.getMessage());
					}
				}) {

			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				String str = null;
				try {
					str = new String(response.data, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return Response.success(str,
						HttpHeaderParser.parseCacheHeaders(response));
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("deletePlansJson", jsonString);
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
