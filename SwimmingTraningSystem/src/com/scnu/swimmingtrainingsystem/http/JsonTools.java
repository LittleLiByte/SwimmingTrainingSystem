package com.scnu.swimmingtrainingsystem.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Gson��ع�����
 * 
 * @author LittleByte
 * 
 */
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

	/**
	 * ��Json�ַ���ת��Ϊһ������
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
	 * ��Json�ַ���ת��Ϊ��������list
	 * 
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getObjects(String jsonString, @SuppressWarnings("rawtypes") Class cls) {
		List<T> list = new ArrayList<T>();
		try {
			Gson gson = new Gson();
			// ��json�ַ����еĶ���ȡ������װ��list������ָ����Ӧ������
			list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
			}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * ��Json�ַ���ת��Ϊ���Map��list
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
