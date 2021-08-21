package com.tao.common.core.common.excel.model;


import com.tao.common.core.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

public class SheetTable implements ISheetTable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3664871267771020345L;
	private String sheetTitle;
	private String name;
	private List<ISheetColumn> columns;
	private Object data;
	private Object parameter;
	private int startRowIndex = 0;

	public SheetTable(String sheetTitle, List<ISheetColumn> columns) {
		this(sheetTitle, columns, null);
	}

	public SheetTable(String sheetTitle, List<ISheetColumn> columns, Object data) {
		this(sheetTitle, columns, data, null);
	}

	public SheetTable(String sheetTitle, List<ISheetColumn> columns, Object data, Object parameter) {
		this(sheetTitle, sheetTitle, columns, data, parameter);
	}

	public SheetTable(String sheetTitle, String name, List<ISheetColumn> columns) {
		this(sheetTitle, name, columns, null);
	}

	public SheetTable(String sheetTitle, String name, List<ISheetColumn> columns, Object data) {
		this(sheetTitle, name, columns, data, null);
	}

	public SheetTable(String sheetTitle, String name, List<ISheetColumn> columns, Object data, Object parameter) {
		this.sheetTitle = sheetTitle;
		this.columns = columns;
		this.data = data;
		this.parameter = parameter;
	}

	@Override
	public List<ISheetColumn> getColumns() {
		return this.columns;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public String getName() {
		if (StringUtil.isEmptyOrNull(name)) {
			return this.sheetTitle;
		}

		return name;
	}

	@Override
	public Object getParameter() {
		return this.parameter;
	}

	@Override
	public String getSheetTitle() {
		return this.sheetTitle;
	}

	@Override
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

	@Override
	public void setSheetTitle(String sheetTitle) {
		this.sheetTitle = sheetTitle;
	}

	@Override
	public int getStartRowIndex() {
		return this.startRowIndex;
	}

	@Override
	public void setStartRowIndex(int startRowIndex) {
		this.startRowIndex = startRowIndex;
	}

}
