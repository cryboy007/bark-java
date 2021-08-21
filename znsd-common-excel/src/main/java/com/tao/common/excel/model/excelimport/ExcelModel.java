package com.tao.common.excel.model.excelimport;

public class ExcelModel implements Comparable<ExcelModel> {
	private String values;
	private int index;

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int compareTo(ExcelModel o) {
		int i = this.getIndex() - o.getIndex();
		return i;
	}

}
