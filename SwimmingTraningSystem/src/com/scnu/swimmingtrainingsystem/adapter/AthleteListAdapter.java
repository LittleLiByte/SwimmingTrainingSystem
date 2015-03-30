package com.scnu.swimmingtrainingsystem.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import com.scnu.swimmingtrainingsystem.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.scnu.swimmingtrainingsystem.activity.MyApplication;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.effect.Effectstype;
import com.scnu.swimmingtrainingsystem.effect.NiftyDialogBuilder;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.SmallPlan;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.view.Switch;

/**
 * 运动员列表数据适配器
 * 
 * @author LittleByte
 * 
 */
public class AthleteListAdapter extends BaseAdapter {

	private Context context;
	private RequestQueue mQueue;
	private List<Athlete> athletes;
	private boolean editable = false;
	private EditText athleteName;
	private EditText athleteAge;
	private Switch mGenderSwitch;
	private EditText athleteContact;
	private EditText others;
	private DBManager dbManager;
	private long userID;
	protected Toast toast;
	protected MyApplication app;

	public AthleteListAdapter(Context context, MyApplication app,
			List<Athlete> athletes, long userID) {
		this.context = context;
		this.athletes = athletes;
		this.userID = userID;
		this.app = app;
		dbManager = DBManager.getInstance();
		mQueue = Volley.newRequestQueue(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return athletes.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return athletes.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		OnClick listener = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.athlete_list_item, null);

			holder.id = (TextView) convertView.findViewById(R.id.tb_AthID);
			holder.name = (TextView) convertView.findViewById(R.id.tb_AthName);
			holder.details = (ImageButton) convertView
					.findViewById(R.id.details);
			holder.delete = (ImageButton) convertView.findViewById(R.id.delete);
			listener = new OnClick();// 在这里新建监听对象
			holder.details.setOnClickListener(listener);
			holder.delete.setOnClickListener(listener);
			convertView.setTag(holder);
			convertView.setTag(holder.details.getId(), listener);
		} else {
			holder = (ViewHolder) convertView.getTag();
			listener = (OnClick) convertView.getTag(holder.details.getId());
		}
		holder.id.setText(String.format("%1$03d", athletes.get(position)
				.getId()));
		holder.name.setText(athletes.get(position).getName());
		listener.setPosition(position);
		return convertView;
	}

	private void createDialog(final int position) {
		final NiftyDialogBuilder viewDialog = NiftyDialogBuilder
				.getInstance(context);
		Effectstype effect = Effectstype.RotateLeft;
		viewDialog
				.withTitle("查看运动员信息")
				.withMessage(null)
				.withIcon(
						context.getResources().getDrawable(
								R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(false).withDuration(500)
				.withEffect(effect).withButton1Text("修改")
				.withButton2Text(Constants.OK_STRING)
				.setCustomView(R.layout.add_athlete_dialog, context)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						enableModification();
					}

				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						updateModification(position, viewDialog);
					}

				}).show();
		Window window = viewDialog.getWindow();
		athleteName = (EditText) window.findViewById(R.id.add_et_user);
		athleteAge = (EditText) window.findViewById(R.id.add_et_age);
		athleteContact = (EditText) window.findViewById(R.id.add_et_contact);
		others = (EditText) window.findViewById(R.id.add_et_extra);
		mGenderSwitch = (Switch) window.findViewById(R.id.toggle_gender);

		athleteName.setText(athletes.get(position).getName());
		athleteAge.setText(athletes.get(position).getAge() + "");

		String gender = athletes.get(position).getGender();
		System.out.println("gender------->" + gender);
		if (gender.equals("男")) {
			mGenderSwitch.setChecked(true);
		} else {
			mGenderSwitch.setChecked(false);
		}
		athleteContact.setText(athletes.get(position).getPhone());
		others.setText(athletes.get(position).getExtras());
		// 禁用编辑框
		athleteName.setEnabled(false);
		athleteAge.setEnabled(false);
		athleteContact.setEnabled(false);
		others.setEnabled(false);
		mGenderSwitch.setFocusable(false);

	}

	/**
	 * 使得运动员信息可以修改
	 */
	private void enableModification() {
		editable = true;
		athleteName.setBackgroundResource(R.drawable.bg_edit);
		athleteAge.setBackgroundResource(R.drawable.bg_edit);
		athleteContact.setBackgroundResource(R.drawable.bg_edit);
		others.setBackgroundResource(R.drawable.bg_edit);
		athleteName.setEnabled(true);
		athleteAge.setEnabled(true);
		athleteContact.setEnabled(true);
		others.setEnabled(true);
		mGenderSwitch.setFocusable(true);
	}

	/**
	 * 提交修改，更新数据库数据，如果处于联网状态则将更新请求发送至服务器
	 * 
	 * @param position
	 * @param viewDialog
	 */
	private void updateModification(final int position,
			final NiftyDialogBuilder viewDialog) {
		if (editable) {
			editable = false;
			String ath_name = athleteName.getText().toString().trim();
			String ath_age = athleteAge.getText().toString().trim();
			String ath_phone = athleteContact.getText().toString().trim();
			String ath_extras = others.getText().toString().trim();
			String ath_gender = "男";
			if (!mGenderSwitch.isChecked()) {
				ath_gender = "女";
			}
			dbManager.updateAthlete(athletes, position, ath_name, ath_age,
					ath_gender, ath_phone, ath_extras);
			
			// 如果处在联网状态，则发送至服务器
			boolean isConnect = (Boolean) app.getMap().get(
					Constants.IS_CONNECT_SERVER);
			if (isConnect) {
				// 同步服务器
				modifyAthRequest(athletes, position, ath_name, ath_age,
						ath_gender, ath_phone, ath_extras);
			}else {
				setDatas(dbManager.getAthletes(userID));
				notifyDataSetChanged();
				CommonUtils.showToast(context, toast, "修改成功");
			}
		}
		viewDialog.dismiss();
	}

	final class ViewHolder {
		private TextView id;
		private TextView name;
		private ImageButton details;
		private ImageButton delete;
	}

	class OnClick implements OnClickListener {
		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// Log.d(TAG, list.get(position));
			switch (v.getId()) {
			case R.id.details:
				createDialog(position);
				break;
			case R.id.delete:
				deleteAthlete();
				break;
			default:
				break;
			}
		}

		/**
		 * 响应删除运动员事件
		 */
		private void deleteAthlete() {
			AlertDialog.Builder build = new AlertDialog.Builder(context);
			build.setTitle("系统提示").setMessage(
					"确定要删除[ " + athletes.get(position).getName() + " ]的信息吗？");
			build.setPositiveButton(Constants.OK_STRING,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int result = dbManager.deleteAthlete(athletes,
									position);
							if (result != 0) {
								// 如果处在联网状态，则发送至服务器
								boolean isConnect = (Boolean) app.getMap().get(
										Constants.IS_CONNECT_SERVER);
								if (isConnect) {
									// 同步服务器
									deleteAthRequest(athletes.get(position));
								} else {
									setDatas(dbManager.getAthletes(userID));
									notifyDataSetChanged();
									CommonUtils.showToast(context, toast,
											"删除成功");
								}

							}
						}
					});
			build.setNegativeButton(Constants.CANCLE_STRING,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
		}
	}

	public void setDatas(List<Athlete> athletes) {
		this.athletes.clear();
		this.athletes.addAll(athletes);
	}

	/**
	 * 修改运动员信息请求
	 * 
	 * @param obj
	 */
	public void modifyAthRequest(List<Athlete> athletes, int position,
			String ath_name, String ath_age, String ath_gender,
			String ath_phone, String ath_extras) {
		User user = dbManager.getUser(userID);
		Athlete obj = athletes.get(position);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("uid", user.getUid());
		jsonMap.put("athlete_id", obj.getAid());
		jsonMap.put("athlete_name", ath_name);
		jsonMap.put("athlete_age", ath_age);
		jsonMap.put("athlete_gender", ath_gender);
		jsonMap.put("athlete_phone", ath_phone);
		jsonMap.put("athlete_extra", ath_extras);
		final String athleteJson = JsonTools.creatJsonString(jsonMap);
		StringRequest stringRequest = new StringRequest(Method.POST,
				CommonUtils.HOSTURL + "modifyAthlete", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("ModifyAthlete", response);
						JSONObject obj;
						try {
							obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								setDatas(dbManager.getAthletes(userID));
								notifyDataSetChanged();
								CommonUtils.showToast(context, toast, "修改成功");
							} else {
								CommonUtils.showToast(context, toast, "修改失败");
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
						Log.e("ModifyAthlete", error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("modifyAthleteJson", athleteJson);
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
	 * 删除运动员信息请求
	 * 
	 * @param a
	 *            运动员对象
	 */
	public void deleteAthRequest(final Athlete a) {
		final User us = dbManager.getUser(userID);
		StringRequest stringRequest2 = new StringRequest(Method.POST,
				CommonUtils.HOSTURL + "deleteAthlete", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("AthleteListAdapter", response);
						JSONObject obj;
						try {
							obj = new JSONObject(response);
							int resCode = (Integer) obj.get("resCode");
							if (resCode == 1) {
								setDatas(dbManager.getAthletes(userID));
								notifyDataSetChanged();
								CommonUtils.showToast(context, toast, "删除成功");
							} else {
								CommonUtils.showToast(context, toast, "删除失败");
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
						Log.e("AthleteListAdapter", error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// 设置请求参数
				Map<String, String> map = new HashMap<String, String>();
				map.put("athlete_id", a.getAid() + "");
				return map;
			}
		};
		stringRequest2.setRetryPolicy(new DefaultRetryPolicy(
				Constants.SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mQueue.add(stringRequest2);
	}
}
