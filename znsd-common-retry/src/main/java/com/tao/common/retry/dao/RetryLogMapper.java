package com.tao.common.retry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.tao.common.retry.dto.RetryLog;

@Mapper
public interface RetryLogMapper extends BaseMapper<RetryLog> {

}
