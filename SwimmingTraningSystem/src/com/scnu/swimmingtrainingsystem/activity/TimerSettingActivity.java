package com.scnu.swimmingtrainingsystem.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.adapter.ChooseAthleteAdapter;
import com.scnu.swimmingtrainingsystem.adapter.ShowChosenAthleteAdapter;
import com.scnu.swimmingtrainingsystem.db.DBManager;
import com.scnu.swimmingtrainingsystem.effect.Effectstype;
import com.scnu.swimmingtrainingsystem.effect.NiftyDialogBuilder;
import com.scnu.swimmingtrainingsystem.model.Athlete;
import com.scnu.swimmingtrainingsystem.model.PlanHolder;
import com.scnu.swimmingtrainingsystem.util.Constants;
import com.scnu.swimmingtrainingsystem.util.XUtils;

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

	/**
	 * 展示全部运动员的listview
	 */
	private ListView athleteListView;
	/**
	 * 展示全部运动员的adapter
	 */
	private ChooseAthleteAdapter allAthleteAdapter;
	/**
	 * 数据中全部运动员
	 */
	private List<Athlete> athletes;
	/**
	 * 显示在activity上的被选中要计时的运动员listview
	 */
	private ListView chosenListView;
	/**
	 * 显示在activity上的被选中要计时的运动员数据适配器
	 */
	private ShowChosenAthleteAdapter showChosenAthleteAdapter;

	/**
	 * 已选中的运动员
	 */
	private List<Athlete> chosenAthletes = new ArrayList<Athlete>();
	private Spinner poolSpinner;

	private Toast toast;
	private HashMap<Long, Boolean> map = new HashMap<Long, Boolean>();

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
		chosenListView = (ListView) findViewById(R.id.list_choosed);
		long userid = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		athletes = dbManager.getAthletes(userid);
		// 初始化map数据，即将全部运动员设为不选中状态
		for (int i = 0; i < athletes.size(); i++) {
			map.put(athletes.get(i).getId(), false);
		}
		List<String> poolLength = new ArrayList<String>();
		poolLength.add("25米池");
		poolLength.add("50米池");
		poolSpinner = (Spinner) findViewById(R.id.pool_length);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, poolLength);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		poolSpinner.setAdapter(adapter1);
		poolSpinner.setSelection(1);
		showChosenAthleteAdapter = new ShowChosenAthleteAdapter(
				TimerSettingActivity.this, chosenAthletes, map);
	}

	/**
	 * 选择计划
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
		allAthleteAdapter = new ChooseAthleteAdapter(this, athletes, map);
		athleteListView.setAdapter(allAthleteAdapter);
		athleteListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				PlanHolder holder = (PlanHolder) arg1.getTag();
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
				map.put(athletes.get(arg2).getId(), holder.cb.isChecked());
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
								TimerSettingActivity.this, chosenAthletes, map);
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
		if (showChosenAthleteAdapter.getCount() != 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new Date());
			// 保存计时日期
			app.getMap().put(Constants.TEST_DATE, date);
			
			List<String> athleteNames = new ArrayList<String>();
			for (Athlete ath : chosenAthletes) {
				athleteNames.add(ath.getName());
			}
			//报存显示在成绩运动员匹配页面的运动员名字
			app.getMap().put(Constants.DRAG_NAME_LIST, athleteNames);
			
			Intent i = new Intent(this, TimerActivity.class);
			i.putExtra("ATHLETE_NUMBER", chosenAthletes.size());
			startActivity(i);
			finish();
		} else {
			XUtils.showToast(this, toast, "请添加运动员后再开始计时！");
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
