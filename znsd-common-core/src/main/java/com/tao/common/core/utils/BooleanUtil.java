package com.tao.common.core.utils;

public class BooleanUtil {
	public static Boolean parse(Object val) {
		if (val == null) {
			return false;
		}
		if (val.getClass() == Boolean.class || val.getClass() == boolean.class) {
			return (Boolean) val;
		}
		if (val.getClass() == int.class || val.getClass() == Integer.class) {
			return parse((Integer) val);
		}
		if (val.getClass() == String.class) {
			return parse((String) val);
		}
		return false;
	}

	private static Boolean parse(Integer val) {
		return val == 1;
	}

	private static Boolean parse(String val) {
		val = val.trim();
		return val.equalsIgnoreCase("true") || val.equals("1");
	}
}
