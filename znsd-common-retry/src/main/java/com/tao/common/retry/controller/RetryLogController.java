package com.tao.common.retry.controller;

import com.github.pagehelper.PageInfo;
import com.tao.common.core.common.base.BasePage;
import com.tao.common.core.common.message.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.tao.common.retry.dto.RetryLog;
import com.tao.common.retry.service.RetryLogService;

@RestController
@RequestMapping("portal/retryLog")
public class RetryLogController {

    @Autowired
    private RetryLogService retryLogService;

    @GetMapping("get")
    public Result<RetryLog> get(String id) {
        return Result.success(retryLogService.getById(id));
    }

    @PostMapping("query")
    public Result query(@RequestBody RetryLog retryLog, BasePage basePage) {
        PageInfo<RetryLog> page = retryLogService.query(retryLog, basePage);
        return Result.success(page);
    }

    @PostMapping("com/tao/common/retry")
    public Result retry(String id) {
        retryLogService.retry(id);
        return Result.success();
    }

    @PostMapping("updateAsPassed")
    public Result updateAsPassed(String id) {
        retryLogService.updateAsPassed(id);
        return Result.success();
    }
}
