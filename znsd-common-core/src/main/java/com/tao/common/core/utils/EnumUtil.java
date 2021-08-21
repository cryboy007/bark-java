package com.tao.common.core.utils;


import com.tao.common.core.common.exception.ExceptionWapper;

public class EnumUtil {

	public static <T extends Enum<T>> T parse(Class<T> enumType, String enumStringValue) {
		try {
			return Enum.valueOf(enumType, enumStringValue);
		} catch (Exception e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	public static <T extends Enum<T>> int getFlagEnumsValue(T[] enums) {
		if (enums == null) {
			return 0;
		}

		int value = 0;

		for (T e : enums) {
			int flag = 1 << e.ordinal();
			value |= flag;
		}

		return value;
	}

	public static <T extends Enum<T>> boolean isFlagEnumEnabled(int flagsValue, T flagEnum) {
		int flag = 1 << flagEnum.ordinal();
		return (flagsValue & flag) == flag;
	}
}
