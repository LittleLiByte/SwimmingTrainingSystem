package com.scnu.swimmingtrainingsystem.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scnu.swimmingtrainingsystem.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;

/**
 * 开始计时前设定Activity，即选择计划并开始计时
 * 
 * @author LittleByte
 * 
 */
public class TimerSettingActivity extends Activity {

	private MyApplication app;
	private List<String> plans;
	private ListView dialog_listview;
	private AlertDialog alertDialog;
	private ArrayAdapter<String> adapter;
	private TextView size, count;
	private Button choose;
	private ListView athleteListView;
	private ArrayAdapter<String> adapter2;
	private List<String> athleteName;
	private List<Plan> ps;
	private RelativeLayout clcokset_rl;
	private List<Athlete> athletes;
	private int swimTime = 0;
	private long plan_id;
	private DBManager dbManager;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_clockset);
		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}

	}

	private void init() {
		// TODO Auto-generated method stub
		app = (MyApplication) getApplication();
		dbManager = DBManager.getInstance();
		size = (TextView) findViewById(R.id.choose_plan_pool_length);
		count = (TextView) findViewById(R.id.choose_plan_times);
		choose = (Button) findViewById(R.id.bt_choose_plan);
		athleteListView = (ListView) findViewById(R.id.list_choosed);

		clcokset_rl = (RelativeLayout) findViewById(R.id.clcokset_rl);
		plans = new ArrayList<String>();
		athleteName = new ArrayList<String>();
		long userid = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		ps = dbManager.getUserPlans(userid);
		for (int i = 0; i < ps.size(); i++) {
			plans.add(ps.get(i).getName());
		}

	}

	/**
	 * 选择计划
	 * 
	 * @param v
	 */
	public void choosePlan(View v) {
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setView(View.inflate(this, R.layout.dialog_choose_plan,
				null));
		alertDialog.show();
		Window window = alertDialog.getWindow();
		dialog_listview = (ListView) window.findViewById(R.id.choose_plan_list);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, plans);
		dialog_listview.setAdapter(adapter);
		dialog_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (athleteName.size() != 0) {
					athleteName.clear();
				}
				Plan p = ps.get(arg2);
				plan_id = p.getId();
				choose.setText(p.getName());
				athletes = dbManager.getAthInPlan(plan_id);
				for (Athlete a : athletes) {
					athleteName.add(a.getName());
				}
				size.setText(p.getPool());

				swimTime = p.getTime();
				// 将游泳趟数保存
				app.getMap().put(Constants.SWIM_TIME, swimTime);
				// 将选中计划的运动员的人数保存
				app.getMap().put(Constants.ATHLETE_NUMBER, athletes.size());

				List<Long> athIDList = new ArrayList<Long>();
				for (Athlete a : athletes) {
					athIDList.add(a.getId());
				}
				// 保存选中计划中的运动员ID列
				app.getMap().put(Constants.ATHLTE_ID_LIST, athIDList);
				app.getMap().put(Constants.PLAN_ID, plan_id);

				count.setText(swimTime + "趟");

				adapter2 = new ArrayAdapter<String>(TimerSettingActivity.this,
						android.R.layout.simple_list_item_1, athleteName);
				athleteListView.setAdapter(adapter2);
				clcokset_rl.setVisibility(View.VISIBLE);

				alertDialog.dismiss();
			}
		});
		Button cancle = (Button) window.findViewById(R.id.choose_plan_back);
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});

	}

	/**
	 * 开始计时,有分泳道计时和手动匹配计时
	 * 
	 * @param v
	 */
	public void startTiming(View v) {
		switch (v.getId()) {
		case R.id.start1:
			if (athleteName.size() != 0) {
				if (athleteName.size() > 8) {
					XUtils.showToast(this, toast, "为了更好的展示和计时，请不要选择人数大于8的计划");
					return;
				}
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd  HH:mm:ss");
				String date = sdf.format(new Date());
				app.getMap().put(Constants.TEST_DATE, date);
				Intent i = new Intent(this, SeparateTimingActivity.class);
				startActivity(i);
				finish();
			} else {
				XUtils.showToast(this, toast, "尚未选择计划");
			}
			break;
		case R.id.start2:
			if (athleteName.size() != 0) {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd  HH:mm:ss");
				String date = sdf.format(new Date());
				app.getMap().put(Constants.TEST_DATE, date);
				Intent i = new Intent(this, TimerActivity.class);
				startActivity(i);
				finish();
			} else {
				XUtils.showToast(this, toast, "尚未选择计划！");
			}
			break;

		default:
			break;
		}

	}

	public void clcokset_back(View v) {
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
