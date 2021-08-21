package com.tao.common.retry.stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tao.common.core.common.other.ServiceUtils;
import com.tao.common.retry.dto.RetryArgsDTO;
import com.tao.common.retry.dto.RetryCache;
import com.tao.common.retry.util.RetryCacheUtils;
import com.tao.common.retry.util.RetryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import com.tao.common.retry.NeedRetryException;
import com.tao.common.retry.ParameterizedTypeReference;
import com.tao.common.retry.config.RetryProperties;
import com.tao.common.retry.service.RetryLogService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Slf4j
public class RetryInputService {

    @Autowired
    private RetryLogService retryLogService;

    @Autowired
    private RetryProperties retryProperties;

    @StreamListener(RetrySource.RETRY_INPUT)
    public void receive(RetryArgsDTO retryArgsDTO) {
        try {
            RetryCache retryCache;
            if (RetryCacheUtils.checkIfExistsCache(retryArgsDTO)) {
                retryCache = RetryCacheUtils.getRetryCacheFromRedis(retryArgsDTO);
                retryCache.setTimes(retryCache.getTimes() + 1);
            } else {
                String id = retryArgsDTO.getId();
                if (id == null) {
                    id = retryLogService.save(retryArgsDTO);
                }
                if (retryArgsDTO.getMaxRetryTimes() == 0) {
                    return;
                }
                retryCache = new RetryCache();
                retryCache.setId(id);
            }
            if (retryCache.getTimes() > retryProperties.getMaxRetryTimes() || retryCache.getTimes() > retryArgsDTO.getMaxRetryTimes()) {
                RetryCacheUtils.removeCache(retryCache.getKey());
                retryLogService.updateAsFailed(retryCache.getId());
                return;
            }
            try {
                Thread.sleep(RetryUtils.nextMaxInterval(retryCache.getTimes()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
            log.info("日志ID[{}]-类[{}]方法[{}]的第[{} in {}]次重试。", retryCache.getId(), retryArgsDTO.getClazz().getSimpleName(), retryArgsDTO.getMethodName(), retryCache.getTimes(), retryArgsDTO.getMaxRetryTimes());
            RetryCacheUtils.putCache(retryArgsDTO, retryCache);
            Object bean = ServiceUtils.getService(retryArgsDTO.getClazz());
            Method method = retryArgsDTO.getClazz().getMethod(retryArgsDTO.getMethodName(), retryArgsDTO.getArgsClazzes());
            retryArgsDTO.setArgsTypes(method.getGenericParameterTypes());
            checkAndRecoverArgsType(retryArgsDTO);
            callMethod(method, bean, retryArgsDTO, retryCache);
        } catch (Exception ex) {
            log.error(String.format("类[{%s}]方法[{%s}]的重试消息消费失败[{%s}]。", retryArgsDTO.getClazz().getSimpleName(), retryArgsDTO.getMethodName(), ex.getMessage()), ex);
        }
    }

    private void callMethod(Method method, Object bean, RetryArgsDTO retryArgsDTO, RetryCache retryCache) throws Exception {
        try {
            method.invoke(bean, retryArgsDTO.getArgs().getData());
            RetryCacheUtils.removeCache(retryCache.getKey());
            retryLogService.updateAsSuccess(retryCache.getId());
            log.info("日志ID[{}]-类[{}]方法[{}]的第[{} in {}]次重试。最终成功执行。", retryCache.getId(), retryArgsDTO.getClazz().getSimpleName(), retryArgsDTO.getMethodName(), retryCache.getTimes(), retryArgsDTO.getMaxRetryTimes());
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof NeedRetryException) {
                log.error("日志ID[{}]-类[{}]方法[{}]的第[{} in {}]次重试，还是抛出重试异常。", retryCache.getId(), retryArgsDTO.getClazz().getSimpleName(), retryArgsDTO.getMethodName(), retryCache.getTimes(), retryArgsDTO.getMaxRetryTimes());
                log.error("异常为:{}", e);
            } else {
                throw e;
            }
        }
    }

    private void checkAndRecoverArgsType(RetryArgsDTO retryArgsDTO) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mapper.setDateFormat(dateFormat);
        if (null != retryArgsDTO.getArgs().getData()) {
            for (int i = 0; i < retryArgsDTO.getArgs().getData().length; i++) {
                if (retryArgsDTO.getArgs().getClazzes() != null && retryArgsDTO.getArgs().getClazzes()[i] != null) {
                    Class clazz = retryArgsDTO.getArgs().getClazzes()[i];
                    if (Map.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz)) {
                        Type type = retryArgsDTO.getArgsTypes()[i];
                        TypeReference typeReference = new ParameterizedTypeReference(type);
                        retryArgsDTO.getArgs().getData()[i] = mapper.convertValue(retryArgsDTO.getArgs().getData()[i], typeReference);
                    } else {
                        retryArgsDTO.getArgs().getData()[i] = mapper.convertValue(retryArgsDTO.getArgs().getData()[i], clazz);
                    }
                }
            }
        }
    }
}
