package com.tao.common.excel.utils;

@Deprecated
public class TableModel {

	private String[] tableHeaders;

	private String sheetName;

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	private Value[][] data;

	private int startRow;
	private int startColumn;

	public String[] getTableHeaders() {
		return tableHeaders;
	}

	public void setTableHeaders(String[] tableHeaders) {
		this.tableHeaders = tableHeaders;
	}

	public Value[][] getData() {
		return data;
	}

	public void setData(Value[][] data) {
		this.data = data;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}

}
