package com.example.swimmingtraningsystem.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

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
		build.setTitle("ϵͳ��ʾ").setMessage("ȷ���˳���");
		build.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent();
				i.setClass(SettingActivity.this, LoginActivity.class);
				startActivity(i);
				finish();
				app.exit();
			}
		});
		build.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	public void setting_back(View v) {
		finish();
	}

	public void setting(View v) {

		switch (v.getId()) {
		case R.id.setting_illustration:
			Toast.makeText(this, "ʹ��˵��", 0).show();
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
}
