package com.tao.common.retry.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tao.common.core.common.base.BasePage;
import com.tao.common.core.utils.StringUtil;
import com.tao.common.core.utils.UUIDUtil;
import com.tao.common.retry.dao.RetryLogMapper;
import com.tao.common.retry.dto.ArgsDTO;
import com.tao.common.retry.dto.RetryArgsDTO;
import com.tao.common.retry.dto.RetryLog;
import com.tao.common.retry.stream.RetryOutputService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import com.tao.common.retry.RetryConstants;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RetryLogService extends ServiceImpl<RetryLogMapper, RetryLog> {

    @Autowired
    private RetryOutputService retryOutputService;

    public String save(RetryArgsDTO retryArgsDTO) {
        RetryLog retryLog = convert2RetryLog(retryArgsDTO);
        retryLog.setStatus(retryLog.getMaxRetryTimes() == 0 ? RetryConstants.FAILED : RetryConstants.NEW);
        retryLog.setCreateTime(new Date());
        retryLog.setCreateBy("admin");
        retryLog.setModifyTime(new Date());
        retryLog.setModifyBy("admin");
        this.baseMapper.insert(retryLog);
        return retryLog.getId();
    }

    public int updateAsFailed(String id) {
        return updateStatus(id, RetryConstants.FAILED);
    }

    public int updateAsSuccess(String id) {
        return updateStatus(id, RetryConstants.SUCCESS);
    }

    private int updateStatus(String id, String status) {
        RetryLog retryLog = new RetryLog();
        retryLog.setStatus(status);
        retryLog.setModifyTime(new Date());
        QueryWrapper queryWrapper = new QueryWrapper<RetryLog>();
        queryWrapper.eq("id", id);
        return this.baseMapper.update(retryLog, queryWrapper);
    }

    public RetryArgsDTO getRetryArgsDTOById(String id) {
        RetryLog retryLog = this.baseMapper.selectById(id);
        Assert.notNull(retryLog, "数据不存在");
        return convert2RetryArgsDTO(retryLog);
    }

    public PageInfo<RetryLog> query(RetryLog retryLog, BasePage basePage) {
        QueryWrapper<RetryLog> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, false, "create_time");
        wrapper.eq(StringUtil.isNotEmptyOrNull(retryLog.getId()), "id", retryLog.getId());
        wrapper.ne("status", RetryConstants.SUCCESS);
        wrapper.like(StringUtil.isNotEmptyOrNull(retryLog.getArgs()), "args", retryLog.getArgs());
        wrapper.like(StringUtil.isNotEmptyOrNull(retryLog.getStackTrace()), "stack_trace", retryLog.getStackTrace());
        wrapper.like(StringUtil.isNotEmptyOrNull(retryLog.getName()), "name", retryLog.getName());
        wrapper.like(StringUtil.isNotEmptyOrNull(retryLog.getBillNo()), "bill_no", retryLog.getBillNo());
        wrapper.like(StringUtil.isNotEmptyOrNull(retryLog.getErrorMsg()), "error_msg", retryLog.getErrorMsg());
        wrapper.ge(retryLog.getCreateTime()!=null, "create_time", retryLog.getCreateTime());
        PageHelper.startPage(basePage.getPageNum(), basePage.getPageSize());
        List<RetryLog> retryLogList = baseMapper.selectList(wrapper);
        PageInfo<RetryLog> pageInfo = new PageInfo<>(retryLogList);
        return pageInfo;
    }

    public int updateAsPassed(String id) {
        return updateStatus(id, RetryConstants.PASSED);
    }

    public void retry(String id) {
        RetryLog retryLog = this.baseMapper.selectById(id);
        Assert.notNull(retryLog, "数据不存在");
        Assert.state(RetryConstants.FAILED.equals(retryLog.getStatus()) || RetryConstants.NEW.equals(retryLog.getStatus()), "只有新增和失败的请求可以重试");
        retryOutputService.sendMsg(convert2RetryArgsDTO(retryLog));
    }

    private RetryLog convert2RetryLog(RetryArgsDTO retryArgsDTO) {
        ObjectMapper mapper = new ObjectMapper();
        RetryLog retryLog = new RetryLog();
        retryLog.setId(UUIDUtil.generate());
        try {
            retryLog.setArgs(mapper.writeValueAsString(retryArgsDTO.getArgs()));
            retryLog.setArgsClazzes(mapper.writeValueAsString(retryArgsDTO.getArgsClazzes()));
            retryLog.setClazz(mapper.writeValueAsString(retryArgsDTO.getClazz()));
        } catch (JsonProcessingException e) {
            log.error("failed to write value to json.", e);
        }
        retryLog.setMaxRetryTimes(retryArgsDTO.getMaxRetryTimes());
        retryLog.setMethodName(retryArgsDTO.getMethodName());
        retryLog.setStackTrace(retryArgsDTO.getStackTrace());
        retryLog.setName(retryArgsDTO.getName());
        retryLog.setBillNo(retryArgsDTO.getBillNo());
        
        if (StringUtils.hasText(retryArgsDTO.getErrorMsg())) {
        	
            // 限制错误消息的最大长度为250.以防太长造成保存失败。
        	if(retryArgsDTO.getErrorMsg().length()>250){
        		retryLog.setErrorMsg(retryArgsDTO.getErrorMsg().substring(0, 250));
        	}else {
        		retryLog.setErrorMsg(retryArgsDTO.getErrorMsg());
        	}
            
        }
        return retryLog;
    }

    private RetryArgsDTO convert2RetryArgsDTO(RetryLog retryLog) {
        RetryArgsDTO retryArgsDTO = new RetryArgsDTO();
        ObjectMapper mapper = new ObjectMapper();
        try {
            retryArgsDTO.setClazz(mapper.readValue(retryLog.getClazz(), Class.class));
            retryArgsDTO.setArgs(mapper.readValue(retryLog.getArgs(), ArgsDTO.class));
            retryArgsDTO.setArgsClazzes(mapper.readValue(retryLog.getArgsClazzes(), Class[].class));
        } catch (IOException e) {
            log.error("failed to read value from json.", e);
        }
        retryArgsDTO.setMethodName(retryLog.getMethodName());
        // 如果重试次数小于1，改为1。保证至少重试1次。
        retryArgsDTO.setMaxRetryTimes(retryLog.getMaxRetryTimes() < 1 ? 1 : retryLog.getMaxRetryTimes());
        retryArgsDTO.setId(retryLog.getId());
        retryArgsDTO.setName(retryLog.getName());
        retryArgsDTO.setBillNo(retryLog.getBillNo());
        retryArgsDTO.setErrorMsg(retryLog.getErrorMsg());
        return retryArgsDTO;
    }
}
