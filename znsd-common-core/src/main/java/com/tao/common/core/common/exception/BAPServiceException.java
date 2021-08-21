package com.tao.common.core.common.exception;


import com.tao.common.core.utils.StringUtil;

public class BAPServiceException extends BAPException {

	private static final long serialVersionUID = 8610954810143860591L;

	private boolean isRuntimeEx = false;
	private String stackTraces = null;

	public BAPServiceException(String code, String message, boolean isRuntimeEx) {
		this(code, message, isRuntimeEx, null);
	}

	public BAPServiceException(String code, String message, boolean isRuntimeEx, String stackTraces) {
		super(code, message);
		this.isRuntimeEx = isRuntimeEx;
		this.setStackTraces(stackTraces);
	}

	public String getStackTraces() {
		return stackTraces;
	}

	public boolean isRuntimeEx() {
		return isRuntimeEx;
	}

	public void setRuntimeEx(boolean isRuntimeEx) {
		this.isRuntimeEx = isRuntimeEx;
	}

	public void setStackTraces(String stackTraces) {
		this.stackTraces = stackTraces;
	}

	@Override
	public String toString() {
		String code = this.getCode();
		if (!StringUtil.isEmptyOrNull(code)) {
			code += ",";
		}

		return (this.isRuntimeEx ? "服务端运行时异常:\n" : "服务端异常:\n") + code + this.getMessage();
	}
}
