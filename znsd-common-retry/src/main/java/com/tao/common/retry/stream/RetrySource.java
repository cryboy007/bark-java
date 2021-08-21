package com.tao.common.retry.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface RetrySource {

    public static final String RETRY_OUTPUT = "retryOutput";
    public static final String RETRY_INPUT = "retryInput";

    @Output(RETRY_OUTPUT)
    MessageChannel retryOutput();

    @Input(RETRY_INPUT)
    SubscribableChannel retryInput();
}
