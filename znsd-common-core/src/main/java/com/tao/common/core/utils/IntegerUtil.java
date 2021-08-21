package com.tao.common.core.utils;

import java.util.regex.Pattern;

public class IntegerUtil {
	public static Integer parse(Object val) {
		try {
			return tryParse(val);
		} catch (Exception e) {
			return 0;
		}
	}

	public static Integer tryParse(Object val) {
		if (val == null || val.equals("")) {
			return null;
		}

		if (val instanceof Number) {
			Double v = DoubleUtil.parse(val);
			return v.intValue();
		}

		String v = val.toString();
		return Integer.parseInt(v);
	}

	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
}
