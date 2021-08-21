package com.tao.common.core.common.excel.model;


import com.tao.common.core.utils.StringUtil;

/**
 * 单元格样式
 * 
 * @author hangw
 *
 */
public class CellStyle {
	private String format;
	private CellHorizontalAlignment horizontalAlignment = CellHorizontalAlignment.GENERAL;
	private CellVerticalAlignment verticalAlignment = CellVerticalAlignment.BOTTOM;
	private boolean wrapText;

	public CellStyle() {
		this(null, CellVerticalAlignment.BOTTOM, CellHorizontalAlignment.GENERAL, false);
	}

	public CellStyle(String format) {
		this(format, CellVerticalAlignment.BOTTOM, CellHorizontalAlignment.GENERAL, false);
	}

	public CellStyle(String format, CellVerticalAlignment verticalAlignment,
			CellHorizontalAlignment horizontalAlignment, boolean wrapText) {
		this.format = format;
		this.verticalAlignment = verticalAlignment;
		this.horizontalAlignment = horizontalAlignment;
		this.wrapText = wrapText;
	}

	/**
	 * 格式化字符串，数字和日期需要此属性
	 * 
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 水平对齐方式
	 * 
	 * @return
	 */
	public CellHorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(CellHorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	/**
	 * 垂直对齐方式
	 * 
	 * @return
	 */
	public CellVerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(CellVerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}

	/**
	 * 是否需要换行
	 * 
	 * @return
	 */
	public boolean isWrapText() {
		return wrapText;
	}

	public void setWrapText(boolean wrapText) {
		this.wrapText = wrapText;
	}

	public String genKey() {
		return StringUtil.format("{0}-{1}-{2}-{2}", format, horizontalAlignment, verticalAlignment, wrapText);
	}

}
