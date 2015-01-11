package com.example.swimmingtraningsystem.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.example.swimmingtraningsystem.activity.MyApplication;
import com.example.swimmingtraningsystem.db.DBManager;
import com.example.swimmingtraningsystem.http.JsonTools;
import com.example.swimmingtraningsystem.model.Athlete;
import com.example.swimmingtraningsystem.model.Plan;
import com.example.swimmingtraningsystem.util.XUtils;

public class ViewPlanFragment extends Fragment implements OnClickListener {
	private MyApplication app;
	private Activity activity;
	private DBManager dbManager;
	private ListView listView;
	private adapter adapter;
	private RelativeLayout relative;
	private List<Plan> plans;
	private List<Plan> selectid;
	private Button calcle, delete;
	private TextView tips;
	private boolean isMulChoice = false; // �Ƿ��ѡ
	private AlertDialog alertDialog;
	private RequestQueue mQueue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_view_plan, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
		app = (MyApplication) activity.getApplication();
		long userID = (Long) app.getMap().get("CurrentUser");
		dbManager = DBManager.getInstance();
		listView = (ListView) activity.findViewById(R.id.view_plan_list);
		relative = (RelativeLayout) activity.findViewById(R.id.relative);
		calcle = (Button) activity.findViewById(R.id.view_cancle);
		delete = (Button) activity.findViewById(R.id.view_delete);
		tips = (TextView) activity.findViewById(R.id.txtcount);
		calcle.setOnClickListener(this);
		delete.setOnClickListener(this);
		selectid = new ArrayList<Plan>();
		plans = dbManager.getUserPlans(userID);
		adapter = new adapter(activity, tips);
		listView.setAdapter(adapter);
		mQueue = Volley.newRequestQueue(activity);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.view_cancle:
			isMulChoice = false;
			break;
		case R.id.view_delete:
			isMulChoice = false;
			for (int i = 0; i < selectid.size(); i++) {
				for (int j = 0; j < plans.size(); j++) {
					if (selectid.get(i).equals(plans.get(j))) {
						plans.remove(j);
					}
				}
			}
			dbManager.deletePlans(selectid);

			List<Long> planIds = new ArrayList<Long>();
			for (Plan p : selectid) {
				planIds.add(p.getId());
			}
			createNewRequest(planIds);
			break;
		default:
			break;
		}
		selectid.clear();
		adapter.notifyDataSetChanged();
		relative.setVisibility(View.INVISIBLE);
	}

	class adapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater = null;
		public HashMap<Integer, Integer> visiblecheck;// ������¼�Ƿ���ʾcheckBox
		public HashMap<Integer, Boolean> ischeck;
		private TextView tips;
		private List<String> athleteName = new ArrayList<String>();

		public adapter(Context context, TextView tips) {
			this.context = context;
			this.tips = tips;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			visiblecheck = new HashMap<Integer, Integer>();
			ischeck = new HashMap<Integer, Boolean>();

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return plans.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return plans.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (isMulChoice) {
				for (int i = 0; i < plans.size(); i++) {
					ischeck.put(i, false);
					visiblecheck.put(i, CheckBox.VISIBLE);
				}
			} else {
				for (int i = 0; i < plans.size(); i++) {
					ischeck.put(i, false);
					visiblecheck.put(i, CheckBox.INVISIBLE);
				}
			}
			ViewPlanHolder holder = null;
			if (convertView == null) {
				holder = new ViewPlanHolder();
				convertView = inflater.inflate(R.layout.view_plan_list_item,
						null);
				holder.txt = (TextView) convertView
						.findViewById(R.id.view_plan_tv);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.check);
				convertView.setTag(holder);
			} else {
				holder = (ViewPlanHolder) convertView.getTag();
			}

			holder.txt.setText(plans.get(position).getName());

			holder.checkBox.setChecked(ischeck.get(position));
			holder.checkBox.setVisibility(visiblecheck.get(position));

			convertView.setOnLongClickListener(new Onlongclick());
			convertView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					ViewPlanHolder planHolder = (ViewPlanHolder) v.getTag();
					if (isMulChoice) {
						if (planHolder.checkBox.isChecked()) {
							planHolder.checkBox.setChecked(false);
							selectid.remove(plans.get(position));
						} else {
							planHolder.checkBox.setChecked(true);
							selectid.add(plans.get(position));
						}
						tips.setText("��ѡ����" + selectid.size() + "��");

					} else {
						createDialog(position);
					}
				}
			});

			return convertView;
		}

		private void createDialog(int position) {
			alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.setView(View.inflate(context,
					R.layout.dialog_view_plan, null));
			alertDialog.show();
			Window window = alertDialog.getWindow();
			TextView length = (TextView) window
					.findViewById(R.id.view_plan_pool_length);
			TextView times = (TextView) window
					.findViewById(R.id.view_plan_times);
			length.setText(plans.get(position).getPool());
			times.setText(plans.get(position).getTime() + "");

			window.findViewById(R.id.view_plan_back).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							alertDialog.dismiss();
						}
					});
			ListView viewList = (ListView) window
					.findViewById(R.id.planlist_view);
			long plan_id = plans.get(position).getId();
			List<Athlete> athletes = dbManager.getAthInPlan(plan_id);
			if (athleteName.size() != 0) {
				athleteName.clear();
			}
			for (Athlete a : athletes) {
				athleteName.add(a.getName());
			}
			ArrayAdapter<String> arAdapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_list_item_1, athleteName);
			viewList.setAdapter(arAdapter);
		}

		class Onlongclick implements OnLongClickListener {

			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				isMulChoice = true;
				selectid.clear();
				relative.setVisibility(View.VISIBLE);
				for (int i = 0; i < plans.size(); i++) {
					adapter.visiblecheck.put(i, CheckBox.VISIBLE);
				}
				adapter.notifyDataSetChanged();
				return true;
			}
		}

		final class ViewPlanHolder {
			private TextView txt;
			private CheckBox checkBox;
		}
	}

	public void createNewRequest(List<Long> planIds) {

		final String deletePlans = JsonTools.creatJsonString(planIds);

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
			protected Map<String, String> getParams() throws AuthFailureError {
				// �����������
				Map<String, String> map = new HashMap<String, String>();
				map.put("deletePlansJson", deletePlans);
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
}
