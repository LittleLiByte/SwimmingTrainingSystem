package com.example.swimmingtraningsystem.http;

import com.google.gson.Gson;

public class JsonTools {

	/**
	 * ������ת����json�ַ���
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
