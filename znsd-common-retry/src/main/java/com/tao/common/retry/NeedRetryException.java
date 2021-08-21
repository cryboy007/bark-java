package com.tao.common.retry;

import lombok.Data;

@Data
public class NeedRetryException extends RuntimeException {
    private String code = "600000";

    public NeedRetryException() {
        super();
    }

    public NeedRetryException(String message) {
        super(message);
    }

    public NeedRetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NeedRetryException(Throwable cause) {
        super(cause);
    }
}
