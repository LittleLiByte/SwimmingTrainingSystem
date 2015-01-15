package com.example.swimmingtraningsystem.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

	public static <T> List<T> getObjects(String jsonString, Class cls) {
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
