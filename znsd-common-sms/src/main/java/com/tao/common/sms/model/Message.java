package com.tao.common.sms.model;

import com.sun.tracing.dtrace.ArgsAttributes;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName Message
 * @Author tao.he
 * @Since 2021/11/6 13:56
 */
@Data
public class Message {
    private String title;

    private String content;


    public Message(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Message(String title) {
        this.title = title;
    }
}
