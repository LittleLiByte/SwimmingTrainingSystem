package com.example.swimmingtraningsystem.util;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.swimmingtraningsystem.R;

public class XUtils {

	public static String IpAdress = "192.168.1.235";
	public static String port = "8080";
	public final static String countries[] = new String[] { "��1��", "��2��",
			"��3��", "��4��", "��5��", "��6��", "��7��", "��8��", };
	public static String HOSTURL = "";
	public static final int SOCKET_TIMEOUT = 5000;

	/**
	 * �Ƿ��һ������
	 * 
	 * @param context
	 * @param isFirst
	 */
	public static void SaveLoginInfo(Context context, boolean isFirst) {
		SharedPreferences sp = context.getSharedPreferences("loginInfo",
				context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	/**
	 * �����¼��Ϣ��SharedPreferences
	 * 
	 * @param context
	 * @param username
	 * @param password
	 */
	public static void SaveLoginInfo(Context context, String username,
			String password) {
		SharedPreferences sp = context.getSharedPreferences("loginInfo",
				context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.commit();
	}

	public static void SaveLoginInfo(Context context, String host) {
		SharedPreferences sp = context.getSharedPreferences("loginInfo",
				context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("hostInfo", host);
		editor.commit();
	}

	/**
	 * ��һ���˶�Ա�Ķ�γɼ��ۺ�ͳ��
	 * 
	 * @param list
	 * @return
	 */
	public static String scoreSum(List<String> list) {
		int minute = 0;
		int second = 0;
		int millisecond = 0;
		for (String s : list) {
			int msc = Integer.parseInt(s.substring(6));
			millisecond += msc;

			int sec = Integer.parseInt(s.substring(3, 5));
			second += sec;

			int min = Integer.parseInt(s.substring(0, 2));
			minute += min;
		}

		second += millisecond / 1000;
		millisecond = millisecond % 1000;
		minute += second / 60;
		second = second % 60;
		if (minute > 59) {
			return "����ͳ�Ƴ������㷶Χ������";
		}

		return String.format("%1$02d��%2$02d��%3$03d", minute, second,
				millisecond);
	}

	public static void showToast(Context context, Toast mToast, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
			View view = mToast.getView();
			view.setBackgroundResource(R.drawable.bg_toast);
			mToast.setView(view);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}
}
