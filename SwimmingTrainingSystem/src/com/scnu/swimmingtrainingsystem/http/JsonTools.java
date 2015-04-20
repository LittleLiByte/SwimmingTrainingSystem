package com.scnu.swimmingtrainingsystem.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Gson相关工具类
 * 
 * @author LittleByte
 * 
 */
public class JsonTools {

	/**
	 * 将对象转换成json字符串
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String creatJsonString(Object value) {
		Gson gs = new Gson();
		return gs.toJson(value);
	}

	/**
	 * 将Json字符串转化为一个对象
	 * 
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> T getObject(String jsonString, Class<T> cls) {
		T t = null;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 将Json字符串转化为多个对象的list
	 * 
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getObjects(String jsonString, @SuppressWarnings("rawtypes") Class cls) {
		List<T> list = new ArrayList<T>();
		try {
			Gson gson = new Gson();
			// 将json字符串中的对象取出来封装到list集合中指定对应的类型
			list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 将Json字符串转化为多个Map的list
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Map<String, Object>> getObjectMaps(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Gson gson = new Gson();
			list = gson.fromJson(jsonString,
					new TypeToken<List<Map<String, Object>>>() {
					}.getType());

		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
}
