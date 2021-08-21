package com.tao.common.core.common.excel.model;

public class SheetColumn implements ISheetColumn {

	private String title;
	private CellValueType type;
	private CellStyle cellStyle;
	private String[] explicitValues;
	private String comments;

	public SheetColumn(String title, CellValueType type) {
		this(title, type, new CellStyle());
	}

	public SheetColumn(String title, CellValueType type, CellStyle cellStyle) {
		this(title, type, cellStyle, null);
	}

	public SheetColumn(String title, CellValueType type, CellStyle cellStyle, String[] explicitValues) {
		this.title = title;
		this.type = type;
		this.cellStyle = cellStyle;
		this.explicitValues = explicitValues;
	}

	public SheetColumn(String title, String[] explicitValues) {
		this(title, CellValueType.String, new CellStyle(), explicitValues);
	}

	public SheetColumn(String title, CellValueType type, String format) {
		this(title, type, new CellStyle(format));
	}

	@Override
	public CellStyle getCellStyle() {
		return cellStyle;
	}

	@Override
	public String getComments() {
		return comments;
	}

	@Override
	public String[] getExplicitValues() {
		return explicitValues;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public CellValueType getType() {
		return type;
	}

	@Override
	public CellValueType getValueType() {
		return type;
	}

	public void setCellStyle(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setExplicitValues(String[] values) {
		this.explicitValues = values;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(CellValueType type) {
		this.type = type;
	}

}
