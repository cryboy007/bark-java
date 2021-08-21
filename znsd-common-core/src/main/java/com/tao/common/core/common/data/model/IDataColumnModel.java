package com.tao.common.core.common.data.model;


import com.tao.common.core.common.excel.model.CellValueType;

public interface IDataColumnModel {
	String getTitle();

	void setTitle(String title);

	CellValueType getValueType();
	
}
