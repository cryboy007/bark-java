package com.tao.common.core.common.exception;


import com.tao.common.core.utils.ResourceUtils;
import com.tao.common.core.utils.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ExceptionWapper {

	public static BAPException createBapException(InvocationTargetException ex) {
		return createBapException(ex.getTargetException());
	}

	public static BAPException createBapException(String code, Throwable ex, Object... paras) {
		if (ex instanceof BAPException) {
			return (BAPException) ex;
		}

		if (ex instanceof InvocationTargetException) {
			return createBapException(((InvocationTargetException) ex).getTargetException());
		}

		if (!StringUtil.isEmptyOrNull(code)) {
			String message = getMessage(code);
			if (!StringUtil.isEmptyOrNull(message)) {
				message = StringUtil.format(message, paras);
				return new BAPException(code, message, ex);
			}
		}

		return new BAPException(code, ex.getMessage(), ex);
	}

	public static BAPException createBapException(Throwable ex, Object... paras) {
		return createBapException(ex.getClass().getName(), ex, paras);
	}

	public static BAPRuntimeException createBapRunTimeException(String code, Object... paras) {
		return createBapRunTimeException(null, code, paras);
	}

	public static BAPRuntimeException createBapRunTimeException(Throwable cause, String code, Object... paras) {
		String message = getMessage(code);
		if (!StringUtil.isEmptyOrNull(message)) {
			message = StringUtil.format(message, paras);
		} else if (paras.length > 0 && paras[0] instanceof String) {
			message = String.valueOf(paras[0]);

			paras = Arrays.copyOfRange(paras, 1, paras.length, Object[].class);
			message = StringUtil.format(message, paras);
		}

		if (!StringUtil.isEmptyOrNull(message)) {
			return new BAPRuntimeException(code, message, cause);
		}

		return new BAPRuntimeException(code, getMessage("common.UnregisteredException") + "[BAPRuntimeException]"
				+ code, cause);
	}

	private static String getMessage(String code) {
		return ResourceUtils.getValue(code, false);
	}

	private ExceptionWapper() {

	}

}
