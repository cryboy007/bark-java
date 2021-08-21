package com.tao.common.core.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ArrayUtil {

	/**
	 * 拼接多个数组
	 * 
	 * @param arrays
	 * @return
	 */
	public static <T> T[] concat(List<T[]> arrays) {
		if (arrays == null || arrays.size() == 0) {
			return null;
		}

		int tottalLength = 0;
		for (T[] other : arrays) {
			tottalLength += other.length;
		}

		T[] first = arrays.get(0);
		T[] result = Arrays.copyOf(first, tottalLength);
		int offset = first.length;

		for (int i = 1; i < arrays.size(); i++) {
			T[] other = arrays.get(i);
			System.arraycopy(other, 0, result, offset, other.length);
			offset += other.length;
		}

		return result;
	}

	/**
	 * 拼接多个数组 返回的数组类型与第一个一致
	 * 
	 * @param arrays
	 * @return
	 */
	public static <T> T[] concat(T[]... arrays) {
		if (arrays == null || arrays.length == 0) {
			return null;
		}

		int tottalLength = 0;
		for (T[] other : arrays) {
			tottalLength += other.length;
		}

		T[] first = arrays[0];
		T[] result = Arrays.copyOf(first, tottalLength);
		int offset = first.length;

		for (int i = 1; i < arrays.length; i++) {
			T[] other = arrays[i];
			System.arraycopy(other, 0, result, offset, other.length);
			offset += other.length;
		}

		return result;
	}

	public static <T> T[] toArray(List<?> source, Class<T> clazz) {
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(clazz, source.size());
		return source.toArray(array);
	}

	/**
	 * 转换数组对象
	 * 
	 * <p>
	 * [string1,string2,string3]或者string1,string2,string3可以转换为数组；List转换为数组；如果都不是
	 * ，会返回一个数组包装对象
	 * 
	 * @param value
	 * @return
	 */
	public static Object[] tryParse(Object value) {
		if (value == null) {
			return new Object[0];
		}

		if (value.getClass().isArray()) {
			return (Object[]) value;
		}

		if (value instanceof String) {
			return ListUtil.convertToList((String) value).toArray();
		}

		if (value instanceof List<?>) {
			return ((List<?>) value).toArray();
		}

		return new Object[] { value };
	}
}
