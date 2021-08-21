package com.tao.common.retry.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author william
 */
@Data
public class DigestDTO implements Serializable {
    private Class aClass;
    private String methodName;
    private Class[] argsClasses;
    private Object[] data;

    public DigestDTO(RetryArgsDTO retryArgsDTO) {
        this.aClass = retryArgsDTO.getClazz();
        this.methodName = retryArgsDTO.getMethodName();
        this.argsClasses = retryArgsDTO.getArgsClazzes();
        this.data = retryArgsDTO.getArgs().getData();
    }
}
