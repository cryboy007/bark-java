package com.tao.common.retry.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author william
 */
@Data
public class RetryCache implements Serializable {
    private int times = 1;
    private String id;
    private String key;
}
