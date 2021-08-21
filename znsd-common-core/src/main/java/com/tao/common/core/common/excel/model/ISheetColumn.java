package com.tao.common.core.common.excel.model;


import com.tao.common.core.common.data.model.IDataColumnModel;

public interface ISheetColumn extends IDataColumnModel {

	/**
	 * 下拉框选项
	 * 
	 * @return
	 */
	String[] getExplicitValues();

	CellStyle getCellStyle();

	String getComments();
}
