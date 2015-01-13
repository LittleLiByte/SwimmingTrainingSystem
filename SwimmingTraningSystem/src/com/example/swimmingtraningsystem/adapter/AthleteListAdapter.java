package com.example.swimmingtraningsystem.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.litepal.crud.DataSupport;

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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.effect.Effectstype;
import com.example.swimmingtraningsystem.effect.NiftyDialogBuilder;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.util.XUtils;

public class AthleteListAdapter extends BaseAdapter {

	private Context context;
	private RequestQueue mQueue;
	private List<Athlete> athletes;
	private boolean editable = false;
	private EditText athleteName;
	private EditText athleteAge;
	private EditText athleteContact;
	private EditText others;
	private DBManager dbManager;
	private long userID;
	protected Toast toast;

	public AthleteListAdapter(Context context, List<Athlete> athletes,
			long userID) {
		this.context = context;
		this.athletes = athletes;
		this.userID = userID;
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
			listener = new OnClick();// �������½���������
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
				.withTitle("�鿴�˶�Ա��Ϣ")
				.withMessage(null)
				.withIcon(
						context.getResources().getDrawable(
								R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(true).withDuration(500)
				.withEffect(effect).withButton1Text("�޸�").withButton2Text("�ύ")
				.setCustomView(R.layout.add_athlete_dialog, context)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						editable = true;
						athleteName.setBackgroundResource(R.drawable.bg_edit);
						athleteAge.setBackgroundResource(R.drawable.bg_edit);
						athleteContact
								.setBackgroundResource(R.drawable.bg_edit);
						others.setBackgroundResource(R.drawable.bg_edit);
						athleteName.setEnabled(true);
						athleteAge.setEnabled(true);
						athleteContact.setEnabled(true);
						others.setEnabled(true);
					}
				}).setButton2Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (editable) {
							editable = false;
							String ath_name = athleteName.getText().toString()
									.trim();
							String ath_age = athleteAge.getText().toString()
									.trim();
							String ath_phone = athleteContact.getText()
									.toString().trim();
							String ath_extras = others.getText().toString()
									.trim();

							dbManager.updateAthlete(athletes, position,
									ath_name, ath_age, ath_phone, ath_extras);
							setDatas(dbManager.getAthletes(userID));
							notifyDataSetChanged();

							// ͬ��������
							createNewRequest1(athletes, position);

							XUtils.showToast(context, toast, "�޸ĳɹ�");
						}
						viewDialog.dismiss();
					}
				}).show();

		Window window = viewDialog.getWindow();
		athleteName = (EditText) window.findViewById(R.id.add_et_user);
		athleteAge = (EditText) window.findViewById(R.id.add_et_age);
		athleteContact = (EditText) window.findViewById(R.id.add_et_contact);
		others = (EditText) window.findViewById(R.id.add_et_extra);
		athleteName.setText(athletes.get(position).getName());
		athleteAge.setText(athletes.get(position).getAge() + "");
		athleteContact.setText(athletes.get(position).getPhone());
		others.setText(athletes.get(position).getExtras());
		// ���ñ༭��
		athleteName.setEnabled(false);
		athleteAge.setEnabled(false);
		athleteContact.setEnabled(false);
		others.setEnabled(false);

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
				AlertDialog.Builder build = new AlertDialog.Builder(context);
				build.setTitle("ϵͳ��ʾ").setMessage(
						"ȷ��Ҫɾ��[ " + athletes.get(position).getName()
								+ " ]����Ϣ��");
				build.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								int result = dbManager.deleteAthlete(athletes,
										position);
								if (result != 0) {
									createNewRequest2(athletes.get(position));
									setDatas(dbManager.getAthletes(userID));
									notifyDataSetChanged();
									XUtils.showToast(context, toast, "ɾ���ɹ�");
								}
							}
						});
				build.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();

				break;
			default:
				break;
			}
		}
	}

	public void setDatas(List<Athlete> athletes) {
		this.athletes.clear();
		this.athletes.addAll(athletes);
	}

	/**
	 * ������ת����json�ַ��������ύ��������
	 * 
	 * @param obj
	 */
	public void createNewRequest1(List<Athlete> athletes, int position) {

		Athlete obj = athletes.get(position);

		obj = DataSupport.find(Athlete.class, obj.getId(), true);
		System.out.println("JsonTools.creatJsonString(obj)----->"
				+ JsonTools.creatJsonString(obj));
		final String athleteJson = JsonTools.creatJsonString(obj);
		StringRequest stringRequest = new StringRequest(Method.POST,
				XUtils.HOSTURL + "modifyAthlete", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("ModifyAthlete", response);
						if (response.equals("ok")) {

						} else {
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
				// �����������
				Map<String, String> map = new HashMap<String, String>();
				map.put("modifyAthleteJson", athleteJson);
				return map;
			}

			@Override
			public RetryPolicy getRetryPolicy() {
				// TODO Auto-generated method stub
				// ��ʱ����
				RetryPolicy retryPolicy = new DefaultRetryPolicy(
						XUtils.SOCKET_TIMEOUT,
						DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
				return retryPolicy;
			}
		};

		mQueue.add(stringRequest);
	}

	public void createNewRequest2(final Athlete a) {

		StringRequest stringRequest2 = new StringRequest(Method.POST,
				XUtils.HOSTURL + "deleteAthlete", new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("AthleteListAdapter", response);
						XUtils.showToast(context, toast, "�ɹ�ͬ������������");
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
				// �����������
				Map<String, String> map = new HashMap<String, String>();
				map.put("deleteAthlete", a.getId() + "");
				return map;
			}
		};

		mQueue.add(stringRequest2);
	}
}
