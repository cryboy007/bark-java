package com.tao.common.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONUtil {
	private static Logger log = LoggerFactory.getLogger(JSONUtil.class);
	@SuppressWarnings("unchecked")
	public static <T> T getValue(JSONObject jobj, String propertyName, T defaultValue) {
		if (jobj == null || !jobj.containsKey(propertyName)) {
			return defaultValue;
		}

		Object o = jobj.get(propertyName);
		if (o == null) {
			return defaultValue;
		}
		return (T) o;

	}

	@SuppressWarnings("unchecked")
	public static <T> T get(JSONArray jarr, int index) {
		return (T) jarr.get(index);
	}

	public static void setValue(JSONObject jobj, String propertyName, Object value) {
		jobj.put(propertyName, value);
	}

	public static JSONObject createJsonObject(String JsonString) {
		return JSON.parseObject(JsonString);
	}
	
	public static JSONObject createJsonObject(String JsonString, Feature... features) {
		return JSON.parseObject(JsonString, features);
	}

	public static JSONObject createJsonObject(Map<String, Object> map) {
		return new JSONObject(map);
	}

	public static JSONArray createJsonArray() {
		return new JSONArray();
	}

	public static JSONArray createJsonArray(String JsonString) {
		return JSON.parseArray(JsonString);
	}

	public static JSONArray createJsonArray(List<Object> list) {
		return new JSONArray(list);
	}

	public static JSONObject tryParseObject(String jsonString) {
		try {
			return JSON.parseObject(jsonString);
		} catch (JSONException e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}

	public static JSONArray tryParseArray(String jsonString) {
		try {
			return JSON.parseArray(jsonString);
		} catch (JSONException e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}

	public static String toPrettyJSONString(Object json) {
		if (json instanceof JSONObject || json instanceof JSONArray) {
			return JSON.toJSONString(json, true);
		}

		try {
			if (json instanceof String) {
				try {
					return JSON.toJSONString(JSON.parseObject((String) json), true);
				} catch (JSONException e) {
					log.error(e.getMessage(),e);
					return JSON.toJSONString(JSON.parseArray((String) json), true);
				}
			}

			return ObjectUtil.serializeByObjectMapper(json, new SerializerFeature[] { SerializerFeature.PrettyFormat });
		} catch (JSONException e) {
			log.error(e.getMessage(),e);
			return json.toString();
		}

	}
}
