package com.tao.common.excel.utils;


import com.tao.common.core.utils.DateUtil;

public class Value {
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	private Object value;
	private String pattern = DateUtil.DEF_DATETIME_FMT;


}
