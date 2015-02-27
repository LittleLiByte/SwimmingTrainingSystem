package com.example.swimmingtraningsystem.activity;

import java.util.HashMap;
import java.util.List;
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
import android.widget.ListView;
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
import com.example.swimmingtraningsystem.adapter.AthleteListAdapter;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.effect.Effectstype;
import com.example.swimmingtraningsystem.effect.NiftyDialogBuilder;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.User;
import com.example.swimmingtraningsystem.util.Constants;
import com.example.swimmingtraningsystem.util.XUtils;
import com.example.swimmingtraningsystem.view.Switch;

/**
 * 运动员管理Activity
 * 
 * @author LittleByte
 * 
 */
public class AthleteActivity extends Activity {
	// 该对象保存全局变量
	private MyApplication mApplication;
	// 展示所有运动员信息的列表控件
	private ListView mListView;
	private Toast mToast;
	// 运动员信息列表的数据适配器
	private AthleteListAdapter mAthleteListAdapter;
	// 运动员信息数据集
	private List<Athlete> mAthletes;
	// Volley请求队列
	private RequestQueue mQueue;
	// 数据库管理类
	private DBManager mDbManager;
	// 当前用户对象
	private User mUser;
	// 当前用户对象id
	private Long mUserId;
	// 运动员名字编辑框
	private EditText mAthleteName;
	// 运动员年龄编辑框
	private EditText mAthleteAge;
	// 运动员联系电话编辑框
	private EditText mAthleteContact;
	// 运动员备注编辑框
	private EditText mOthers;
	// 运动员性别切换按钮
	private Switch mGenderSwitch;
	private Boolean isConnect;

	private static final String ADD_ATHLETE_TITLE_STRING = "添加运动员";
	private static final String NAME_CANNOT_BE_EMPTY_STRING = "运动员名字不能为空";
	private static final String NAME_CANNOT_BE_REPEATE_STRING = "存在运动员名字重复，请更改";
	private static final String AGE_CANNOT_BE_EMPTY_STRING = "年龄不能为空";
	private static final String ADDATHLETE = "addAthlete";
	private static final String GETATHLETES = "getAthletes";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.setTheme(R.style.AppThemeLight);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_athlete);
		init();
	}

	/**
	 * 界面初始化
	 */
	private void init() {
		mApplication = (MyApplication) getApplication();
		mDbManager = DBManager.getInstance();
		mUserId = (Long) mApplication.getMap().get(Constants.CURRENT_USER_ID);
		mUser = mDbManager.getUser(mUserId);
		mListView = (ListView) findViewById(R.id.lv);
		mAthletes = mDbManager.getAthletes(mUserId);
		mAthleteListAdapter = new AthleteListAdapter(this, mApplication,
				mAthletes, mUserId);
		mListView.setAdapter(mAthleteListAdapter);
		mQueue = Volley.newRequestQueue(this);
		// 根据是否能够连接服务器来操作，如果能够连接服务器，则使用服务返回的数据，否则将数据保存到本地使用
		isConnect = (Boolean) mApplication.getMap().get(
				Constants.IS_CONNECT_SERVICE);
		
		if (isConnect) {
			getAthleteRequest();
		}

	}

	/**
	 * 弹出对话框并添加一个运动员信息
	 * 
	 * @param v
	 */
	public void addAthlete(View v) {
		final NiftyDialogBuilder addDialog = NiftyDialogBuilder
				.getInstance(this);
		Effectstype effect = Effectstype.RotateLeft;
		Window window = addDialog.getWindow();
		addDialog
				.withTitle(ADD_ATHLETE_TITLE_STRING)
				.withMessage(null)
				.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(false)
				.withDuration(700)
				.withEffect(effect)
				.withButton1Text(Constants.CANCLE_STRING)
				.withButton2Text(Constants.OK_STRING)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						addDialog.dismiss();
					}
				})
				.setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						String name = mAthleteName.getText().toString().trim();
						String ageString = mAthleteAge.getText().toString()
								.trim();
						String phone = mAthleteContact.getText().toString()
								.trim();
						String other = mOthers.getText().toString().trim();

						boolean isCheck = mGenderSwitch.isChecked();

						System.out.println("isCheck--->" + isCheck);
						String gender = "男";
						if (!isCheck) {
							gender = "女";
						}
						boolean isExit = mDbManager.isAthleteNameExsit(mUserId,
								name);
						if (TextUtils.isEmpty(name)) {
							XUtils.showToast(AthleteActivity.this, mToast,
									NAME_CANNOT_BE_EMPTY_STRING);
						} else if (isExit) {
							XUtils.showToast(AthleteActivity.this, mToast,
									NAME_CANNOT_BE_REPEATE_STRING);
						} else if (TextUtils.isEmpty(ageString)) {
							XUtils.showToast(AthleteActivity.this, mToast,
									AGE_CANNOT_BE_EMPTY_STRING);
						} else {
							int age = Integer.parseInt(ageString);
							addAthlete(name, age, gender, phone, other);
							addDialog.dismiss();
						}
					}
				}).setCustomView(R.layout.add_athlete_dialog, v.getContext())
				.show();
		mAthleteName = (EditText) window.findViewById(R.id.add_et_user);
		mAthleteAge = (EditText) window.findViewById(R.id.add_et_age);
		mAthleteContact = (EditText) window.findViewById(R.id.add_et_contact);
		mOthers = (EditText) window.findViewById(R.id.add_et_extra);
		mGenderSwitch = (Switch) window.findViewById(R.id.toggle_gender);
	}

	/**
	 * 保存一个运动员信息，如果无法联网则直接保存到数据库， 如果成功连接至服务器则将运动员信息发送至服务器
	 * 
	 * @param name
	 *            运动员姓名
	 * @param age
	 *            运动员年龄
	 * @param gender
	 *            运动员性别
	 * @param contact
	 *            运动员手机号码
	 * @param others
	 *            运动员其他信息
	 */
	public void addAthlete(String name, int age, String gender, String contact,
			String others) {

		Athlete a = new Athlete();
		a.setName(name);
		a.setAge(age);
		a.setGender(gender);
		a.setPhone(contact);
		a.setExtras(others);
		a.setUser(mUser);

		if (isConnect) {
			addAthleteequest(a);
		} else {
			a.save();
			XUtils.showToast(AthleteActivity.this, mToast,
					Constants.ADD_SUCCESS_STRING);
			mAthletes = mDbManager.getAthletes(mUserId);
			mAthleteListAdapter.setDatas(mAthletes);
			mAthleteListAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 将需要保存的对象转换成json字符串，提交到服务器
	 * 
	 * @param obj
	 */
	public void addAthleteequest(final Athlete a) {
		final String athleteJson = JsonTools.creatJsonString(a);
		StringRequest request = new StringRequest(Method.POST, XUtils.HOSTURL
				+ ADDATHLETE, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.i(Constants.TAG, response);
				try {
					JSONObject obj = new JSONObject(response);
					int resCode = (Integer) obj.get("resCode");
					if (resCode == 1) {
						XUtils.showToast(AthleteActivity.this, mToast,
								Constants.ADD_SUCCESS_STRING);
						int aid = (Integer) obj.get("athlete");
						a.setAid(aid);
						a.save();
						mAthletes = mDbManager.getAthletes(mUserId);
						mAthleteListAdapter.setDatas(mAthletes);
						mAthleteListAdapter.notifyDataSetChanged();
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
				Log.e(Constants.TAG, error.getMessage());
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("athleteJson", athleteJson);
				return map;
			}
		};
		request.setRetryPolicy(new DefaultRetryPolicy(Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(request);
	}

	/**
	 * 获取运动员信息
	 */
	private void getAthleteRequest() {
		StringRequest getrequest = new StringRequest(Method.GET, XUtils.HOSTURL
				+ GETATHLETES, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.i(Constants.TAG, response);
				if (response.equals("1")) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						int resCode = (Integer) jsonObject.get("resCode");
						if (resCode == 1) {
							String jsonString = jsonObject.get("athlete")
									.toString();
							List<Athlete> athltes = JsonTools.getObjects(
									jsonString, Athlete.class);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {

				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				if (error != null) {
					Log.e(Constants.TAG, error.getMessage());
				}
			}
		});
		getrequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(getrequest);
	}

	/**
	 * 退出当前活动窗体
	 * 
	 * @param v
	 */
	public void back(View v) {
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.slide_bottom_in,
					R.anim.slide_top_out);
			return false;
		}
		return false;
	}

}
