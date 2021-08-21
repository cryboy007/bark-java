package com.tao.common.core.common.excel.model;

public interface IWorkbookProxy {
	static final String EXCEL_2003 = "2003";
	static final String EXCEL_2007 = "2007";

	void createTable(ISheetTable table);

	void addSheetData(ISheetTable table);

	void save(String filePath);
	
	byte[] save();

	/**
	 * 推荐使用 {@link IWorkbookProxy#setCell(ISheetCell)}
	 * 
	 * @param sheetName
	 * @param rowIndex
	 * @param columnIndex
	 * @param type
	 * @param value
	 * @param format
	 */
	@Deprecated
	void setCellValue(String sheetName, int rowIndex, int columnIndex, CellValueType type, Object value, String format);

	void setCell(ISheetCell sheetCell);

}
