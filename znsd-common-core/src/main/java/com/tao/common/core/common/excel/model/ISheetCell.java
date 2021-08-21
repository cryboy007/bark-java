package com.tao.common.core.common.excel.model;

/**
 * Excel单元格模型
 * 
 * @author hangw
 *
 */
public interface ISheetCell {

	/**
	 * 列坐标
	 * 
	 * @return
	 */
	int getColumnIndex();

	/**
	 * 行坐标
	 * 
	 * @return
	 */
	int getRowIndex();

	/**
	 * 所属sheet的名称
	 * 
	 * @return
	 */
	String getSheetName();

	/**
	 * 值
	 * 
	 * @return
	 */
	Object getValue();

	/**
	 * 值类型
	 * 
	 * @return
	 */
	CellValueType getValueType();

	/**
	 * 获取样式
	 * 
	 * @return
	 */
	CellStyle getCellStyle();

}
