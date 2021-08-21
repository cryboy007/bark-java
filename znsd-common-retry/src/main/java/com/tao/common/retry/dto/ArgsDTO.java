package com.tao.common.retry.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArgsDTO implements Serializable {
    private Object[] data;
    private Class[] clazzes;
}
