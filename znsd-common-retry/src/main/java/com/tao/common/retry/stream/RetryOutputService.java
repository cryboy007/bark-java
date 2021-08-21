package com.tao.common.retry.stream;

import com.tao.common.retry.dto.RetryArgsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;


public class RetryOutputService {
    @Autowired
    private RetrySource retrySource;

    public void sendMsg(RetryArgsDTO retryArgsDTO) {
        retrySource.retryOutput().send(MessageBuilder.withPayload(retryArgsDTO).build());
    }
}
