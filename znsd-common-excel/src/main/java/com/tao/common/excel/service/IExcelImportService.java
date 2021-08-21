package com.tao.common.excel.service;

import java.util.List;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.tao.common.core.common.TowTuple;

/**
 * Excel导入服务接口
 * 
 * @author jun.chen
 *
 * @param <T>
 */
public interface IExcelImportService<T> {

	/**
	 * 自定义导入模板名称
	 * 
	 * @return
	 */
	String getExcelName();
	
	/**
	 * Excel类型 默认xlsx
	 * @return
	 */
	default ExcelTypeEnum getExcelType() {
		return ExcelTypeEnum.XLSX;
	}

	/**
	 * Excel导入行记录处理
	 * 
	 * @param data
	 * @return
	 */
	default TowTuple<Boolean, String> checkRowData(T data) {
		return new TowTuple<>(true, "");
	}

	/**
	 * Excel数据结果集处理
	 * 
	 * @param datas 从Excel中读取的泛型对象
	 */
	void handelData(List<T> datas);
}
