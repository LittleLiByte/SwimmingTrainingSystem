package com.example.swimmingtraningsystem.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.example.swimmingtraningsystem.R;

public class SettingActivity extends Activity {
	MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		app = (MyApplication) getApplication();
	}

	public void createDialog() {
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		build.setTitle("系统提示").setMessage("确定退出？");
		build.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent();
				i.setClass(SettingActivity.this, LoginActivity.class);
				startActivity(i);
				finish();
				app.exit();
			}
		});
		build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	public void setting_back(View v) {
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
	}

	public void setting(View v) {

		switch (v.getId()) {
		case R.id.setting_illustration:
			Intent intent = new Intent(this, UseTipsActiviy.class);
			startActivity(intent);
			break;
		case R.id.setting_change_password:
			Intent i = new Intent();
			i.setClass(this, ModifyPassActivity.class);
			startActivity(i);
			break;
		case R.id.setting_exit:
			createDialog();
			break;
		default:
			break;
		}

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
