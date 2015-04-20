package com.scnu.swimmingtrainingsystem.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 屏幕尺寸转换类
 * 
 */
public class ScreenUtils {

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static float getScreenDensity(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}

	/**
	 * px转dp
	 * 
	 * @param id
	 * @param context
	 * @return
	 */
	public static int px2dp(int id, Context context) {
		try {
			return (int) (id * getScreenDensity(context));
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
