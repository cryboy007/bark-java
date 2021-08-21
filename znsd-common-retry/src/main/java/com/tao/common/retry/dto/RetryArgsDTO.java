package com.tao.common.retry.dto;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author william
 */
@Data
public class RetryArgsDTO implements Serializable {
    private String id;
    private Class clazz;
    private String methodName;
    private Class[] argsClazzes;
    private ArgsDTO args;
    private Type[] argsTypes;
    private int maxRetryTimes;
    private String stackTrace;
    private String name;
    private String billNo;
    private String errorMsg;
}
