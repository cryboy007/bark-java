package com.tao.common.excel.excelimport;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;

public class ReadExcelXlsxUtil {
	private static Logger log = LoggerFactory.getLogger(ReadExcelXlsxUtil.class);
	private static String STRCELL;

	/*
	 * 需要存入的对象为String类型
	 */
	public static String getStringCellvalue(Cell cell) {
		STRCELL = null;
		if (cell == null) {
			return null;
		} else {
			switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_STRING:
				STRCELL = cell.getStringCellValue();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				STRCELL = String.valueOf(cell.getNumericCellValue());
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				STRCELL = String.valueOf(cell.getBooleanCellValue());
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				STRCELL = null;
				break;
			default:
				STRCELL = null;
				break;
			}
			if (StringUtils.isEmpty(STRCELL)) {
				return null;
			}
			return STRCELL;
		}
	}

	/*
	 * 需要存入的对象为Date类型
	 */
	public static Date getDateCellValue(Cell cell) {
		Date date = null;
		int cellType;
		if (cell == null) {
			return null;
		} else {
			try {
				cellType = cell.getCellType();
				if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
					date = new Date(cell.getDateCellValue().getTime());
				} else if (cellType == HSSFCell.CELL_TYPE_STRING) {
					date = Date.valueOf(getStringCellvalue(cell));
				} else if (cellType == HSSFCell.CELL_TYPE_BLANK) {
					date = null;
				}
			} catch (Exception e) {
				log.error("单元格输入的内容不正确！");
			}
			return date;
		}
	}

	/*
	 * 需要存入的类型为Integer类型
	 */
	public static Integer getIntegerCellValue(Cell cell) {
		Integer value = null;
		if (cell == null) {
			value = null;
		} else {
			try {
				int cellType = cell.getCellType();
				if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
					value = null;
				} else if (cellType == HSSFCell.CELL_TYPE_STRING) {
					value = Integer.parseInt(getStringCellvalue(cell));
				} else if (cellType == HSSFCell.CELL_TYPE_BLANK) {
					value = null;
				}
				return value;
			} catch (Exception e) {
				log.error("单元格输入的内容不正确！");
			}
		}
		return value;
	}

	/*
	 * 需要存入的对象为String类型
	 */
	public static String getStringCellvalue(XSSFCell cell) {
		String strCell = "";
		if (cell == null) {
			return "";
		} else {
			switch (cell.getCellType()) {
			case XSSFCell.CELL_TYPE_STRING:
				strCell = cell.getStringCellValue();
				break;
			case XSSFCell.CELL_TYPE_NUMERIC:
				strCell = String.valueOf(cell.getNumericCellValue());
				break;
			case XSSFCell.CELL_TYPE_BOOLEAN:
				strCell = String.valueOf(cell.getBooleanCellValue());
				break;
			case XSSFCell.CELL_TYPE_BLANK:
				strCell = "";
				break;
			default:
				strCell = "";
				break;
			}
			if (StringUtils.isEmpty(strCell)) {
				return "";
			}
			return strCell;
		}
	}

	/*
	 * 需要存入的对象为Date类型
	 */
	public static Date getDateCellValue(XSSFCell cell) {
		Date date = null;
		if (cell == null) {
			return null;
		} else {
			try {

				int cellType = cell.getCellType();
				if (cellType == XSSFCell.CELL_TYPE_NUMERIC) {
					date = new Date(cell.getDateCellValue().getTime());
				} else if (cellType == XSSFCell.CELL_TYPE_STRING) {
					date = Date.valueOf(getStringCellvalue(cell));
				} else if (cellType == XSSFCell.CELL_TYPE_BLANK) {
					date = null;
				}
			} catch (Exception e) {
				log.error("单元格输入的内容不正确！");
			}
			return date;
		}
	}

	/*
	 * 需要存入的类型为Integer类型
	 */
	public static Integer getIntegerCellValue(XSSFCell cell) {
		Integer value = null;
		if (cell == null) {
			value = null;
		} else {
			try {
				int cellType = cell.getCellType();
				if (cellType == XSSFCell.CELL_TYPE_NUMERIC) {
					value = null;
				} else if (cellType == XSSFCell.CELL_TYPE_STRING) {
					value = Integer.parseInt(getStringCellvalue(cell));
				} else if (cellType == XSSFCell.CELL_TYPE_BLANK) {
					value = null;
				}
				return value;
			} catch (Exception e) {
				log.error("单元格输入的内容不正确！");
			}
		}
		return value;
	}

}
