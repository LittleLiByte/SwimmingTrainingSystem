package com.scnu.swimmingtrainingsystem.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.scnu.swimmingtrainingsystem.adapter.AthleteListAdapter;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.effect.Effectstype;
import com.scnu.swimmingtrainingsystem.effect.NiftyDialogBuilder;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.view.LoadingDialog;
import com.scnu.swimmingtrainingsystem.view.Switch;

/**
 * 运动员管理Activity
 * 
 * @author LittleByte
 * 
 */
public class AthleteActivity extends Activity {
	private static final String UNKNOW_ERROR = "服务器错误";
	private static final String ADD_ATHLETE_TITLE_STRING = "添加运动员";
	private static final String NAME_CANNOT_BE_EMPTY_STRING = "运动员名字不能为空";
	private static final String NAME_CANNOT_BE_REPEATE_STRING = "存在运动员名字重复，请更改";
	private static final String ADDATHLETE = "addAthlete";
	private static final String GETATHLETES = "getAthletes";

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
	// 是否能连接服务器
	private Boolean isConnect;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.setTheme(R.style.AppThemeLight);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_athlete);
		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
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
		SharedPreferences sp = getSharedPreferences(Constants.LOGININFO,
				Context.MODE_PRIVATE);
		boolean isFirst = sp.getBoolean(Constants.FISRTOPENATHLETE, true);
		boolean userFirstLogin = sp.getBoolean(
				Constants.IS_THIS_USER_FIRST_LOGIN, true);

		// 如果第一次打开应用并且可以连接服务器，就会尝试从服务器获取运动员信息
		if (isConnect && isFirst && userFirstLogin) {
			CommonUtils.initAthletes(this, false);
			CommonUtils.saveIsThisUserFirstLogin(this, false);
			if (loadingDialog == null) {
				loadingDialog = LoadingDialog.createDialog(this);
				loadingDialog.setMessage("正在同步...");
				loadingDialog.setCanceledOnTouchOutside(false);
			}
			loadingDialog.show();
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
				.withDuration(500)
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

						String gender = "男";
						if (!isCheck) {
							gender = "女";
						}
						boolean isExit = mDbManager.isAthleteNameExsit(mUserId,
								name);
						if (TextUtils.isEmpty(name)) {
							CommonUtils.showToast(AthleteActivity.this, mToast,
									NAME_CANNOT_BE_EMPTY_STRING);
						} else if (isExit) {
							CommonUtils.showToast(AthleteActivity.this, mToast,
									NAME_CANNOT_BE_REPEATE_STRING);
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

		if (isConnect) {
			addAthleteequest(a);
		} else {
			a.setUser(mUser);
			a.save();
			CommonUtils.showToast(AthleteActivity.this, mToast,
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
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("athlete", a);
		jsonMap.put("uid", mUser.getUid());
		final String athleteJson = JsonTools.creatJsonString(jsonMap);
		StringRequest request = new StringRequest(Method.POST, CommonUtils.HOSTURL
				+ ADDATHLETE, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Log.i(Constants.TAG, response);
				try {
					JSONObject obj = new JSONObject(response);
					int resCode = (Integer) obj.get("resCode");
					if (resCode == 1) {
						CommonUtils.showToast(AthleteActivity.this, mToast,
								Constants.ADD_SUCCESS_STRING);
						int aid = (Integer) obj.get("athlete_id");
						a.setAid(aid);
						a.setUser(mUser);
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
	 * 获取服务器上的运动员信息
	 */
	private void getAthleteRequest() {
		StringRequest getrequest = new StringRequest(Method.POST,
				CommonUtils.HOSTURL + GETATHLETES, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						loadingDialog.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(response);
							int resCode = (Integer) jsonObject.get("resCode");
							if (resCode == 1) {

								JSONArray athleteArray = jsonObject
										.getJSONArray("athleteList");
								int athletesNumber = athleteArray.length();
								for (int i = 0; i < athletesNumber; i++) {
									Athlete athlete = new Athlete();
									TempAthlete tempAthlete = JsonTools
											.getObject(athleteArray.get(i)
													.toString(),
													TempAthlete.class);
									athlete.setAid(tempAthlete.getAid());
									athlete.setName(tempAthlete.getName());
									athlete.setAge(tempAthlete.getAge());
									athlete.setGender(tempAthlete.getGender());
									athlete.setPhone(tempAthlete.getPhone());
									athlete.setExtras(tempAthlete.getExtras());
									athlete.setUser(mUser);
									athlete.save();
								}
								mAthletes = mDbManager.getAthletes(mUserId);
								mAthleteListAdapter.setDatas(mAthletes);
								mAthleteListAdapter.notifyDataSetChanged();
								CommonUtils.showToast(AthleteActivity.this, mToast,
										"同步成功！");
							} else {
								CommonUtils.showToast(AthleteActivity.this, mToast,
										UNKNOW_ERROR);
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
						// Log.e(Constants.TAG, error.getMessage());
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
				map.put("getAthleteFirst", mUser.getUid() + "");
				return map;
			}
		};
		getrequest.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(getrequest);
	}

	class TempAthlete {
		/**
		 * 运动员id
		 */
		private long id;

		private int aid;
		/**
		 * 运动员名字
		 */
		private String name;
		/**
		 * 运动员年龄
		 */
		private int age;
		/**
		 * 运动员性别
		 */
		private String gender;
		/**
		 * 运动员电话
		 */
		private String phone;
		/**
		 * 运动员备注
		 */
		private String extras;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public int getAid() {
			return aid;
		}

		public void setAid(int aid) {
			this.aid = aid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getExtras() {
			return extras;
		}

		public void setExtras(String extras) {
			this.extras = extras;
		}

		@Override
		public String toString() {
			return "TempAthlete [id=" + id + ", aid=" + aid + ", name=" + name
					+ ", age=" + age + ", gender=" + gender + ", phone="
					+ phone + ", extras=" + extras + "]";
		}

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
