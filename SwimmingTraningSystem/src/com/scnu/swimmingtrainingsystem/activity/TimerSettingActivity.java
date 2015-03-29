package com.scnu.swimmingtrainingsystem.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.adapter.ChooseAthleteAdapter;
import com.scnu.swimmingtrainingsystem.adapter.ShowChosenAthleteAdapter;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.effect.Effectstype;
import com.scnu.swimmingtrainingsystem.effect.NiftyDialogBuilder;
import com.scnu.swimmingtrainingsystem.http.JsonTools;
import com.scnu.swimmingtrainingsystem.model.AdapterHolder;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.User;
import com.scnu.swimmingtrainingsystem.util.CommonUtils;
import com.scnu.swimmingtrainingsystem.util.Constants;

/**
 * 开始计时前设定的Activity，即选择运动员以及其他添加其他信息并开始计时
 * 
 * @author LittleByte
 * 
 */
@SuppressLint("SimpleDateFormat")
public class TimerSettingActivity extends Activity {

	private MyApplication app;
	private DBManager dbManager;
	private EditText distanceEditText;
	private EditText remarksEditText;
	/**
	 * 展示全部运动员的ListView
	 */
	private ListView athleteListView;
	/**
	 * 展示全部运动员的adapter
	 */
	private ChooseAthleteAdapter allAthleteAdapter;
	/**
	 * 全部运动员
	 */
	private List<Athlete> athletes;
	private List<String> athleteNames = new ArrayList<String>();
	/**
	 * 显示在activity上的被选中要计时的运动员ListView
	 */
	private ListView chosenListView;
	/**
	 * 显示在activity上的被选中要计时的运动员数据适配器
	 */
	private ShowChosenAthleteAdapter showChosenAthleteAdapter;

	/**
	 * 已选中的运动员
	 */
	private List<String> chosenAthletes = new ArrayList<String>();
	private Spinner poolSpinner;

	private Toast toast;
	private SparseBooleanArray map = new SparseBooleanArray();
	private Long userid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_clockset);
		try {
			initView();
			initData();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}

	}

	private void initView() {
		// TODO Auto-generated method stub
		app = (MyApplication) getApplication();
		dbManager = DBManager.getInstance();
		chosenListView = (ListView) findViewById(R.id.list_choosed);
		poolSpinner = (Spinner) findViewById(R.id.pool_length);
		distanceEditText = (EditText) findViewById(R.id.tv_distance);
		remarksEditText = (EditText) findViewById(R.id.et_remarks);
	}

	private void initData() {
		SharedPreferences sp = getSharedPreferences(Constants.LOGININFO,
				Context.MODE_PRIVATE);
		int selectedPositoin = sp.getInt(Constants.SELECTED_POOL, 1);
		String swimDistance = sp.getString(Constants.SWIM_DISTANCE, "");
		String mapConfigString = sp.getString("mapConfig", "");
		SparseBooleanArray configArray = JsonTools.getObject(mapConfigString,
				SparseBooleanArray.class);
		app.getMap().put(Constants.CURRENT_SWIM_TIME, 0);
		userid = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		athletes = dbManager.getAthletes(userid);
		for (Athlete ath : athletes) {
			athleteNames.add(ath.getName());
		}
		// 初始化map数据，即将全部运动员设为不选中状态
		for (int i = 0; i < athletes.size(); i++) {
			if (configArray.size() != 0) {
				if (i < configArray.size()) {
					map.put(i, configArray.get(i));
					if (configArray.get(i)) {
						chosenAthletes.add(athleteNames.get(i));
					}
				} else {
					map.put(i, false);
				}
			} else {
				map.put(i, false);
			}

		}

		distanceEditText.setText(swimDistance);
		List<String> poolLength = new ArrayList<String>();
		poolLength.add("25米池");
		poolLength.add("50米池");
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, poolLength);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		poolSpinner.setAdapter(adapter1);
		poolSpinner.setSelection(selectedPositoin);
		showChosenAthleteAdapter = new ShowChosenAthleteAdapter(
				TimerSettingActivity.this, chosenAthletes);
		chosenListView.setAdapter(showChosenAthleteAdapter);
	}

	/**
	 * 选择运动员
	 * 
	 * @param v
	 */
	public void chooseAthlete(View v) {
		final NiftyDialogBuilder selectDialog = NiftyDialogBuilder
				.getInstance(this);
		Effectstype effect = Effectstype.Fall;
		selectDialog.setCustomView(R.layout.dialog_choose_athlete, this);
		Window window = selectDialog.getWindow();
		athleteListView = (ListView) window.findViewById(R.id.choose_list);
		allAthleteAdapter = new ChooseAthleteAdapter(this, athleteNames, map);
		athleteListView.setAdapter(allAthleteAdapter);
		athleteListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				AdapterHolder holder = (AdapterHolder) arg1.getTag();
				// 改变CheckBox的状态
				holder.cb.toggle();
				if (holder.cb.isChecked()) {
					if (!chosenAthletes.contains(allAthleteAdapter
							.getChooseAthlete().get(arg2)))
						// 如果checkbox已选并且chosenAthleteList中无该项
						chosenAthletes.add(allAthleteAdapter.getChooseAthlete()
								.get(arg2));
				} else {
					// 如果checkbox不选择并且chosenAthleteList中有该项
					if (chosenAthletes.contains(allAthleteAdapter
							.getChooseAthlete().get(arg2)))
						chosenAthletes.remove(allAthleteAdapter
								.getChooseAthlete().get(arg2));
				}
				// 将CheckBox的选中状况记录下来
				map.put(arg2, holder.cb.isChecked());
			}
		});
		selectDialog.withTitle("选择运动员").withMessage(null)
				.withIcon(getResources().getDrawable(R.drawable.ic_launcher))
				.isCancelableOnTouchOutside(false).withDuration(500)
				.withEffect(effect).withButton1Text("返回")
				.withButton2Text(Constants.OK_STRING)
				.setButton1Click(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						selectDialog.dismiss();
					}
				}).setButton2Click(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showChosenAthleteAdapter = new ShowChosenAthleteAdapter(
								TimerSettingActivity.this, chosenAthletes);
						chosenListView.setAdapter(showChosenAthleteAdapter);
						selectDialog.dismiss();
					}

				}).show();

		allAthleteAdapter.notifyDataSetChanged();
	}

	/**
	 * 开始计时,有分泳道计时和手动匹配计时
	 * 
	 * @param v
	 */
	public void startTiming(View v) {

		if (chosenAthletes.size() != 0) {
			// 保存这一次的配置到sp
			CommonUtils.saveSelectedPool(this,
					poolSpinner.getSelectedItemPosition());
			CommonUtils.saveDistance(this, distanceEditText.getText()
					.toString());
			CommonUtils.saveSelectedAthlete(this,
					JsonTools.creatJsonString(map));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new Date());
			// 保存计时日期
			app.getMap().put(Constants.TEST_DATE, date);

			List<String> athleteNames = new ArrayList<String>();
			List<Athlete> chosenPersons = dbManager
					.getAthleteByNames(chosenAthletes);
			for (Athlete ath : chosenPersons) {
				athleteNames.add(ath.getName());
			}
			// 报存显示在成绩运动员匹配页面的运动员名字
			app.getMap().put(Constants.DRAG_NAME_LIST, athleteNames);

			String poolString = (String) poolSpinner.getSelectedItem();
			String distance = distanceEditText.getText().toString().trim();
			String extra = remarksEditText.getText().toString();
			if (TextUtils.isEmpty(distance)) {
				distance = "0";
			}
			// 将配置保存到数据库计划表中
			savePlan(poolString, distance, extra, chosenPersons);
			Intent i = new Intent(this, TimerActivity.class);
			startActivity(i);
			finish();
		} else {
			CommonUtils.showToast(this, toast, "请添加运动员后再开始计时！");
		}

	}

	private void savePlan(String pool, String distance, String extra,
			List<Athlete> athlete) {
		// TODO Auto-generated method stub
		User user = dbManager.getUser(userid);
		Plan plan = new Plan();
		plan.setPool(pool);
		plan.setDistance(Integer.parseInt(distance));
		plan.setExtra(extra);
		plan.setUser(user);
		plan.setAthlete(athlete);
		plan.save();
		app.getMap().put(Constants.PLAN_ID, plan.getId());
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
