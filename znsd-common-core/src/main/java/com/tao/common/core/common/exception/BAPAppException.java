package com.tao.common.core.common.exception;

/**
 * 封装JAVA异常
 * 
 * @author hangw
 *
 */
public class BAPAppException extends BAPException {

	private static final long serialVersionUID = -7966514647359915189L;

	public BAPAppException(String code, String message, Throwable cause) {
		super(code, message, cause);
	}

	// public BAPAppException(String code, String message) {
	// super(code, message);
	// }

	public BAPAppException(Throwable cause) {
		super(cause.getClass().getName(), cause.getMessage(), cause);
	}

	@Override
	public String toString() {
		return this.getMessage();
	}
}
