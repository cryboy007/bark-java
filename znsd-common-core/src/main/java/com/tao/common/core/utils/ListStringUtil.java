package com.tao.common.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 方法转移到了{@link ListUtil}和{@link StringUtil}
 * 
 * @author hangw
 *
 */
@Deprecated
public class ListStringUtil {

	/**
	 * 推荐使用{@link ListUtil#convertToList(String)}
	 * 
	 * @param strings
	 * @return
	 */
	@Deprecated
	public static List<String> convertToList(String strings) {
		return convertToList(strings, ",");
	}

/**
	 * 推荐使用{@link ListUtil#convertToList(String, String)
	 * 
	 * @param strings
	 * @return
	 */
	@Deprecated
	public static List<String> convertToList(String strings, String splitRegex) {
		List<String> list = new ArrayList<String>();
		if (strings != null) {
			strings = strings.trim();
			if (strings.length() > 0) {
				if (strings.indexOf("[") > -1) {
					strings = strings.substring(1, strings.length() - 1);
				}
				for (String id : strings.split(splitRegex)) {
					String _id = id.trim();
					if (!StringUtil.isEmptyOrNull(_id)) {
						list.add(_id);
					}
				}
			}
		}
		return list;
	}

/**
	 * 推荐使用{@link ListUtil#convertTo2DList(String)
	 * 
	 * @param strings
	 * @return
	 */
	@Deprecated
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

/**
	 * 推荐使用{@link StringUtil#toString(List)
	 * 
	 * @param strings
	 * @return
	 */
	@Deprecated
	public static String toString(List<?> list) {
		String v = list.toString();
		return v.substring(1, v.length() - 1);
	}

/**
	 * 推荐使用{@link StringUtil#toString(Object[])
	 * 
	 * @param strings
	 * @return
	 */
	@Deprecated
	public static String toString(Object[] list) {
		return toString(Arrays.asList(list));
	}

}
