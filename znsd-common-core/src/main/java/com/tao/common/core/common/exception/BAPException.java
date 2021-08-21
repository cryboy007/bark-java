package com.tao.common.core.common.exception;

public class BAPException extends RuntimeException {

	private static final long serialVersionUID = 7775777449658940109L;

	private String code;

	public BAPException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	protected BAPException() {

	}

	protected BAPException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean simplified) {
		return getMessage();
	}
}