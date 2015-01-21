package com.example.swimmingtraningsystem.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.model.ViewBean;
import com.example.swimmingtraningsystem.view.SexangleImageViews;
import com.example.swimmingtraningsystem.view.SexangleImageViews.OnSexangleImageClickListener;
import com.example.swimmingtraningsystem.view.SexangleViewGroup;

public class MainActivity extends Activity {
	private SexangleViewGroup sexangleViewGroup;
	private ViewBean viewBean;
	private SexangleImageViews imageViews;
	private static final int ID = 0x10000;

	OnSexangleImageClickListener listener = new OnSexangleImageClickListener() {

		@Override
		public void onClick(View view) {
			Intent i = new Intent();
			switch (view.getId()) {
			case ID:
				i.setClass(MainActivity.this, AthleteActivity.class);
				break;
			case ID + 1:
				i.setClass(MainActivity.this, PlanActivity.class);
				break;
			case ID + 2:
				i.setClass(MainActivity.this, ClockSettingActivity.class);
				break;
			case ID + 3:
				i.setClass(MainActivity.this, ScoreActivity.class);
				break;
			default:
				i.setClass(MainActivity.this, SettingActivity.class);
			}
			startActivity(i);
			overridePendingTransition(R.anim.slide_top_in,
					R.anim.slide_bottom_out);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		sexangleViewGroup = (SexangleViewGroup) findViewById(R.id.sexangleView);
		initView();
		MyApplication app = (MyApplication) getApplication();
		app.addActivity(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 5; i++) {
			viewBean = new ViewBean();
			viewBean.setHome(i);
			viewBean.setColor(i);
			viewBean.setTextsize(22);
			viewBean.setTexts(setName(i));
			imageViews = new SexangleImageViews(this, viewBean);
			imageViews.setId(ID + i);
			imageViews.setOnSexangleImageClick(listener);
			sexangleViewGroup.addView(imageViews);
		}
	}

	public String setName(int i) {
		if (i == 0) {
			return "运动员";
		} else if (i == 1) {
			return "计划";
		} else if (i == 2) {
			return "计时器";
		} else if (i == 3) {
			return "成绩查询";
		} else if (i == 4) {
			return "设置";
		}
		return "";
	}

	// 退出程序
	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "在按一次退出", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}

			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyApplication app = (MyApplication) getApplication();
		app.getMap().put("swimTime", 1);
		app.getMap().put("current", 0);
		app.getMap().put("athleteCount", 0);
		app.getMap().put("athIDList", null);
		app.getMap().put("planID", 0);
		app.getMap().put("testDate", "");
		app.getMap().put("isConnect", true);
	}
}
