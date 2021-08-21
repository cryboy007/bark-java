package com.tao.common.core.common.exception;

import lombok.Data;

@Data
public class ProcessingException extends RuntimeException {
    private String code = "600000";

    private Object data;

    public ProcessingException() {
        super();
    }

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingException(Throwable cause) {
        super(cause);
    }

    public ProcessingException(String message, String code, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }
}
