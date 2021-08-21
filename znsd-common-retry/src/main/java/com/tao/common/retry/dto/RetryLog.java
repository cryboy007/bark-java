package com.tao.common.retry.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("log_need_retry_method")
public class RetryLog {
    private String id;
    private String clazz;
    private String methodName;
    private String args;
    private String argsClazzes;
    private Integer maxRetryTimes;
    private String stackTrace;
    private String status;
    private String name;
    private String billNo;
    private String errorMsg;
    private Date createTime;
    private String createBy;
    private Date modifyTime;
    private String modifyBy;
}
