package com.tao.common.core.common.exception;


import com.tao.common.core.common.local.I18nUtils;

/**
 * 业务异常编码接口
 * 
 * @author tao
 * @date 2019-10-14
 *
 */
public interface ICode {
	public default String getCode() {
		return "999999";
	}

	public default String getMessage(String... args) {
		return "业务异常";
	}

	/**
	 * 获取ICode的异常信息
	 * 
	 * @param args
	 * @return
	 */
	public default String getErrorMessage(String... args) {
		String msg = "";
		String code = this.getCode();
		try {
			msg = I18nUtils.getMessage(code, args);
		} catch (Exception e) {
			msg = this.getMessage(args);
		}
		return msg;
	}
}
