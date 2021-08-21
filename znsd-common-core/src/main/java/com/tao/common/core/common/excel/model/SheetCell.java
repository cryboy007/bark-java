package com.tao.common.core.common.excel.model;

public class SheetCell implements ISheetCell {

	private String sheetName;
	private int rowIndex;
	private int columnIndex;
	private CellValueType valueType;
	private Object value;
	private CellStyle cellStyle;

	public SheetCell(String sheetName, int rowIndex, int columnIndex, CellValueType valueType, Object value,
			CellStyle cellStyle) {
		super();
		this.sheetName = sheetName;
		this.rowIndex = rowIndex;
		this.columnIndex = columnIndex;
		this.valueType = valueType;
		this.value = value;
		this.cellStyle = cellStyle;

		if (this.cellStyle == null) {
			this.cellStyle = new CellStyle();
		}
	}

	public SheetCell(String sheetName, int rowIndex, int columnIndex, CellValueType valueType, Object value) {
		this(sheetName, rowIndex, columnIndex, valueType, value, null);
	}

	@Override
	public int getColumnIndex() {
		return columnIndex;
	}

	@Override
	public int getRowIndex() {
		return rowIndex;
	}

	@Override
	public String getSheetName() {
		return sheetName;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public CellValueType getValueType() {
		return valueType;
	}

	@Override
	public CellStyle getCellStyle() {
		return this.cellStyle;
	}

	public void setCellStyle(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}

}
