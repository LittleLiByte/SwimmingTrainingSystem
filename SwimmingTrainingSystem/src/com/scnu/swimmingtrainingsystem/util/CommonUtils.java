package com.scnu.swimmingtrainingsystem.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.scnu.swimmingtrainingsystem.R;

/**
 * 其他工具类
 * 
 * @author Littleyte
 * 
 */
@SuppressLint("DefaultLocale")
public class CommonUtils {

	public static String HOSTURL = "";

	/**
	 * 是否第一次启动
	 * 
	 * @param context
	 * @param isFirst
	 */
	@SuppressWarnings("static-access")
	public static void SaveLoginInfo(Context context, boolean isFirst) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	/**
	 * 保存登录信息到SharedPreferences
	 * 
	 * @param context
	 * @param username
	 * @param password
	 */
	public static void SaveLoginInfo(Context context, String username,
			String password) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.commit();
	}

	public static void SaveLoginInfo(Context context, String host, String ip,
			String port) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("hostInfo", host);
		editor.putString("ip", ip);
		editor.putString("port", port);
		editor.commit();
	}

	public static void saveIsThisUserFirstLogin(Context context, boolean first) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(Constants.IS_THIS_USER_FIRST_LOGIN, first);
		editor.commit();
	}

	/**
	 * 记录选择的泳池大小
	 * 
	 * @param context
	 * @param position
	 */
	public static void saveSelectedPool(Context context, int position) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(Constants.SELECTED_POOL, position);
		editor.commit();
	}

	/**
	 * 记录预计的游泳总距离
	 * 
	 * @param context
	 * @param distance
	 */
	public static void saveDistance(Context context, String distance,
			String interval) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(Constants.SWIM_DISTANCE, distance);
		editor.putString(Constants.INTERVAL, interval);
		editor.commit();
	}

	/**
	 * 保存当前成绩状态，留到统计时进行调整
	 * 
	 * @param context
	 * @param i
	 *            第几趟
	 * @param crrentDistance
	 * @param scoreString
	 * @param athleteString
	 */
	public static void saveCurrentScoreAndAthlete(Context context, int i,
			int crrentDistance, String scoreString, String athleteString) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(Constants.CURRENT_DISTANCE + i, crrentDistance);
		editor.putString(Constants.SCORESJSON + i, scoreString);
		editor.putString(Constants.ATHLETEJSON + i, athleteString);
		editor.commit();
	}

	public static void saveSelectedAthlete(Context context, String mapJson) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("mapConfig", mapJson);
		editor.commit();
	}

	public static void saveSpinnerSelection(Context context,
			String sparseIntArray) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("spinnerSelection", sparseIntArray);
		editor.commit();
	}

	public static void saveAthleteGesture(Context context, String s) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("athleteGesture", s);
		editor.commit();
	}

	/**
	 * 记录是否第一次打开应用的运动员Activity
	 * 
	 * @param context
	 * @param isFirst
	 */
	public static void initAthletes(Context context, boolean isFirst) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(Constants.FISRTOPENATHLETE, isFirst);
		editor.commit();
	}

	/**
	 * 记录是否第一次打开应用的运动员Activity
	 * 
	 * @param context
	 * @param isFirst
	 */
	public static void initPlans(Context context, boolean isFirst) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.LOGININFO, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(Constants.FISRTOPENPLAN, isFirst);
		editor.commit();
	}

	/**
	 * 将一个运动员的多次成绩综合统计
	 * 
	 * @param list
	 * @return
	 */
	public static String scoreSum(List<String> list) {
		int hour = 0;
		int minute = 0;
		int second = 0;
		int millisecond = 0;
		for (String s : list) {
			int msc = Integer.parseInt(s.substring(9)) * 10;
			millisecond += msc;

			int sec = Integer.parseInt(s.substring(5, 7));
			second += sec;

			int min = Integer.parseInt(s.substring(2, 4));
			minute += min;

			int h = Integer.parseInt(s.substring(0, 1));
			hour += h;
		}
		second += millisecond / 1000;
		millisecond = millisecond % 1000 / 10;
		minute += second / 60;
		second = second % 60;
		hour += minute / 60;
		minute = minute % 60;
		return String.format("%1$01d:%2$02d'%3$02d''%4$02d", hour, minute,
				second, millisecond);
	}

	public static String getScoreSubtraction(String s1, String s2) {
		int Subtraction = timeString2TimeInt(s1) - timeString2TimeInt(s2);
		return timeInt2TimeString(Subtraction);

	}

	/**
	 * 将时间字符串转化成毫秒数
	 * 
	 * @param timeString
	 * @return
	 */
	public static int timeString2TimeInt(String timeString) {
		int msc = Integer.parseInt(timeString.substring(9)) * 10;
		int sec = Integer.parseInt(timeString.substring(5, 7));
		int min = Integer.parseInt(timeString.substring(2, 4));
		int hour = Integer.parseInt(timeString.substring(0, 1));
		int totalMsec = msc + sec * 1000 + min * 60000 + hour * 3600000;
		return totalMsec;

	}

	@SuppressLint("DefaultLocale")
	public static String timeInt2TimeString(int totalMsec) {
		// 秒数
		long time_count_s = totalMsec / 1000;
		// 小时数
		long hour = time_count_s / 3600;
		// 分
		long min = time_count_s / 60 - hour * 60;
		// 秒
		long sec = time_count_s - hour * 3600 - min * 60;
		// 毫秒
		long msec = totalMsec % 1000 / 10;

		return String.format("%1$01d:%2$02d'%3$02d''%4$02d", hour, min, sec,
				msec);
		// %1$01d:%2$02d'%3$ 03d''%4$ 03d
	}

	/**
	 * 自定义显示Toast
	 * 
	 * @param context
	 * @param mToast
	 * @param text
	 */
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

	private static long lastClickTime;

	/**
	 * 防止快速的重复点击出现
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
}
