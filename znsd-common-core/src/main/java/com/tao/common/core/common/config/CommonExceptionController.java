package com.tao.common.core.common.config;

import com.tao.common.core.common.exception.BizCode;
import com.tao.common.core.common.exception.BizException;
import com.tao.common.core.common.exception.ProcessingException;
import com.tao.common.core.common.message.Result;
import com.tao.common.core.common.other.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * @author william
 */
@RestControllerAdvice
public class CommonExceptionController {

	private final static String PROD = "prod";
	private final static String ERROR_CODE = "999999";
	private final static String ERROR_CODE_VALID = "999998";

	private static Logger logger = LoggerFactory.getLogger(CommonExceptionController.class);

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e, HttpServletRequest httpServletRequest) {
		String error = String.format("异常请求接口{%s}异常消息{%s}", httpServletRequest.getRequestURI(), e.getMessage());
		logger.error(error, e);
		if (PROD.equalsIgnoreCase(ServiceUtils.getActiveProfile())) {
			return Result.error(ERROR_CODE, "系统异常");
		} else {
			if (null != e.getCause() && e.getCause().getClass().toString().contains("LockWaitTimeoutException")) {
				return Result.error(ERROR_CODE, "当前正在处理中...请稍后重试");
			} else {
				return Result.error(ERROR_CODE, getStackTrace(e), "系统异常：" + e.getMessage());
			}

		}
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ProcessingException.class)
	public Result<?> handleProcessingException(ProcessingException e) {
		logger.error(e.getMessage(), e);
		return Result.error(e.getCode(), "正在处理中");
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
	public Result<?> handleIllegalException(Exception e) {
		logger.error(e.getMessage(), e);
		return Result.error(ERROR_CODE, e.getMessage());
	}

	public static String getStackTrace(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return e.getMessage() + "\r\n" + sw.toString() + "\r\n";
		} catch (Exception e2) {
			return "Failed to get stack trace.";
		}
	}

    /**
     * 参数校验异常类
     *
     * @param exception
     * @return
     * @author 刘朋
     * <br/>date 2020-04-15
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handlerConstraintViolationException(ConstraintViolationException exception) {
    	logger.warn(exception.getMessage(), exception);
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
        	String errorType = constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            String message = constraintViolation.getMessage();
            strBuilder.append(formatMsg(String.valueOf(constraintViolation.getPropertyPath()), errorType, message));
        }
        return Result.error(ERROR_CODE_VALID, strBuilder.toString());
    }
    
	/**
	 * Spring校验异常<MethodArgumentNotValidException>拦截包装返回<br>
	 * 
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		logger.error(e.getMessage(), e);
		BindingResult bindingResult = e.getBindingResult();
		StringBuilder msgBuilder = formatBindingResult(bindingResult);
		return Result.error(ERROR_CODE_VALID, msgBuilder.toString());
	}

	/**
	 * Spring校验异常<BindException>拦截包装返回<br>
	 * 
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(BindException.class)
	public Result<?> handleMethodArgumentNotValidException(BindException e) {
		logger.error(e.getMessage(), e);
		StringBuilder msgBuilder = formatBindingResult(e);
		return Result.error(ERROR_CODE_VALID, msgBuilder.toString());
	}

	/**
	 * 对 BindingResult 进行校验信息格式化
	 * 
	 * @param bindingResult
	 * @return
	 */
	private StringBuilder formatBindingResult(BindingResult bindingResult) {
		List<ObjectError> allErrors = bindingResult.getAllErrors();
		StringBuilder msgBuilder = new StringBuilder();
		for (ObjectError error : allErrors) {
			if (error instanceof FieldError) {
				FieldError fieldError = (FieldError) error;
				msgBuilder.append(formatMsg(fieldError.getField(), error.getCode(), error.getDefaultMessage()));
			} else {
				msgBuilder.append(error.getDefaultMessage());
			}
		}
		return msgBuilder;
	}

	/**
	 * 自定义返回信息
	 * 
	 * @param msg
	 * @return
	 */
	private String formatMsg(String field, String errorCode, String defaultMsg) {
		String resultMsg = String.format("[%s]%s;", field, defaultMsg);
		return resultMsg;
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(BizException.class)
	public Result<?> handleBizException(BizException e) {
		// 公共幂等校验数据存在时直接返回操作成功
		if (e.getCode().equals(BizCode.IDEMPONT_PASS.getCode())) {
			return Result.success(null, BizCode.IDEMPONT_PASS.getMessage() + ",默认返回成功！");
		}
		logger.error(e.getErrMsg(), e);

		Result<Object> error = Result.error(e.getCode(), e.getErrMsg());
		// 异常数据信息赋值到结果的data中
		error.setData(e.getData());
		return error;
	}

}
