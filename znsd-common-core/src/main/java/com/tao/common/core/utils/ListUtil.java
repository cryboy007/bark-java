package com.tao.common.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListUtil {

	/**
	 * 从list中获取指定索引的对象,数据越界返回null
	 * 
	 * @param list
	 * @param index
	 * @return
	 */
	public static <T> T getByIndex(List<T> list, Integer index) {
		return getByIndex(list, index, false);
	}

	/**
	 * 从list中获取指定索引的对象
	 * 
	 * @param list
	 * @param index
	 * @param throwIndexOutOfBounds
	 *            是否抛出数据越界异常，如果false,则返回null
	 * @return
	 */
	public static <T> T getByIndex(List<T> list, Integer index, boolean throwIndexOutOfBounds) {
		if (list == null || index == null) {
			return null;
		}

		try {
			return list.get(index);
		} catch (IndexOutOfBoundsException e) {
			if (throwIndexOutOfBounds) {
				throw e;
			}

			return null;
		}
	}

	/**
	 * 将list按splitSize分割成N段
	 * 
	 * @param list
	 * @param splitSize
	 * @return
	 */
	public static <T> List<List<T>> split(List<T> list, int splitSize) {
		List<List<T>> result = new ArrayList<List<T>>();

		while (list.size() > splitSize) {
			List<T> subList = list.subList(0, splitSize);
			result.add(subList);

			list = list.subList(splitSize, list.size());
		}

		result.add(list);

		return result;
	}

	/**
	 * 将字符串以','分割并转成list,如[string1,string2,string3]或者string1,string2,string3
	 * 
	 * @param strings
	 * @return
	 */
	public static List<String> convertToList(String strings) {
		return convertToList(strings, ",");
	}

	/**
	 * 将字符串以分割并转成list
	 * 
	 * @param strings
	 * @return
	 */
	public static List<String> convertToList(String strings, String splitRegex) {
		List<String> list = new ArrayList<String>();

		if (strings != null) {
			strings = strings.trim();

			int length = strings.length();
			if (length > 0) {
				// 去掉头尾[]
				if (strings.indexOf("[") > -1) {
					strings = strings.substring(1, length - 1);
				}

				for (String val : strings.split(splitRegex)) {
					String valTrim = val.trim();
					if (!StringUtil.isEmptyOrNull(valTrim)) {
						list.add(valTrim);
					}
				}
			}
		}

		return list;
	}

	/**
	 * 将[[string1,string2,string3],[[string4,string5,string6]]]转成二维list
	 * 
	 * @param strings
	 * @return
	 */
	public static List<List<String>> convertTo2DList(String stringss) {
		List<List<String>> tdlist = new ArrayList<List<String>>();
		if (stringss != null) {
			stringss = stringss.trim();
			if (stringss.length() > 1) {
				stringss = stringss.substring(1, stringss.length() - 1);

				Pattern pattern = Pattern.compile("\\[.*?\\]");
				Matcher matcher = pattern.matcher(stringss);

				while (matcher.find()) {
					String ids = matcher.group();
					List<String> list = convertToList(ids);
					tdlist.add(list);
				}

			}
		}
		return tdlist;
	}
}
