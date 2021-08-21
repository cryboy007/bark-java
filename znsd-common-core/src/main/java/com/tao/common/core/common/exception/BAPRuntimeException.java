package com.tao.common.core.common.exception;

public class BAPRuntimeException extends BAPException {

	private static final long serialVersionUID = 7360194806539524593L;

	public BAPRuntimeException(String code, String message) {
		super(code, message);
	}

	public BAPRuntimeException(String code, String message, Throwable cause) {
		super(code, message, cause);
	}
}
