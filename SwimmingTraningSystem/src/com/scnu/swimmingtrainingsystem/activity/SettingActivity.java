package com.scnu.swimmingtrainingsystem.activity;

import com.scnu.swimmingtrainingsystem.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.scnu.swimmingtrainingsystem.util.Constants;

/**
 * 设置Activity
 * 
 * @author LittleByte
 * 
 */
public class SettingActivity extends Activity {
	MyApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		try {
			app = (MyApplication) getApplication();
			//这里是为了应对可能出现的application里面的全局变量被系统回收导致的错误
			Long mUserId = (Long) app.getMap().get(Constants.CURRENT_USER_ID);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
		
	}

	public void createDialog() {
		AlertDialog.Builder build = new AlertDialog.Builder(this);
		build.setTitle("系统提示").setMessage("确定退出？");
		build.setPositiveButton(Constants.OK_STRING,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent();
						i.setClass(SettingActivity.this, LoginActivity.class);
						startActivity(i);
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

	public void setting_back(View v) {
		finish();
		overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_top_out);
	}

	/**
	 * 响应设置选项
	 * 
	 * @param v
	 */
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
