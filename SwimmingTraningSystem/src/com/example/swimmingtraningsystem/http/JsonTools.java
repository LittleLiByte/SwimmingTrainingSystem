package com.example.swimmingtraningsystem.http;

import com.google.gson.Gson;

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
}
