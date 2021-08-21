package com.tao.common.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据分页工具类
 * <p>
 * 集中和分页相关的一些常规API
 * 
 * @author hangwen
 * 
 */
public class DataPageUtil {

	/**
	 * 计算总页数
	 * 
	 * @param totalCount
	 *            记录行数
	 * @param pageSize
	 *            每页大小
	 * @return
	 */
	public static int getTotalPageCount(int totalCount, int pageSize) {
		if (totalCount == 0) {
			return 1;
		}
		if (pageSize > 0) {
			int pageCount = totalCount / pageSize;
			int mod = totalCount % pageSize;

			if (mod > 0) {
				return pageCount + 1;
			} else {
				return pageCount;
			}
		}

		return 1;
	}

	/**
	 * 计算指定页的记录索引范围
	 * 
	 * @param totalCount
	 *            记录行数
	 * @param pageSize
	 *            每页大小
	 * @param pageIndex
	 *            指定页索引 从0开始
	 * @return 数组int[2]{start,end}
	 */
	public static int[] getIndexScope(int totalCount, int pageSize, int pageIndex) {
		if (pageSize <= 0) {
			return new int[] { 0, totalCount };
		}

		int lastPageIndex = getTotalPageCount(totalCount, pageSize) - 1;

		pageIndex = Math.min(lastPageIndex, pageIndex);
		pageIndex = Math.max(0, pageIndex);

		int start = pageSize * pageIndex;
		int end = start + pageSize;
		end = Math.min(end, totalCount);

		return new int[] { start, end };
	}

	/**
	 * 计算dataIndex在第几页
	 * 
	 * @param totalCount
	 * @param pageSize
	 * @param dataIndex
	 * @return
	 */
	public static int getPageIndex(int totalCount, int pageSize, int dataIndex) {
		return getTotalPageCount(dataIndex + 1, pageSize) - 1;
	}

	/**
	 * 获取分页后的每页数据
	 * 
	 * @param pageSize
	 * @param allDatas
	 * @return
	 */
	public static <T> List<T[]> getAllPageDatas(int pageSize, T[] allDatas) {
		List<T[]> pageDatas = new ArrayList<T[]>();
		if (allDatas == null || allDatas.length == 0) {
			return pageDatas;
		}

		int totalCount = allDatas.length;
		int totalPages = getTotalPageCount(totalCount, pageSize);
		int from = 0, to = 0;

		for (int i = 0; i < totalPages; i++) {
			from = pageSize * i;
			to = pageSize * (i + 1);
			if (i == totalPages - 1) {
				to = totalCount;
			}
			T[] currentPageDatas = Arrays.copyOfRange(allDatas, from, to);
			pageDatas.add(currentPageDatas);
		}
		return pageDatas;
	}
}
