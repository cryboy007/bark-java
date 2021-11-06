package com.tao.common.sms.provider;

import com.tao.common.sms.model.BarkMessage;
import com.tao.common.sms.model.Message;
import org.junit.Test;


public class BarkProviderTest {
    PushProvider provider = new BarkProvider();

    @Test
    public void send() {
        BarkMessage message = new BarkMessage("智能时代","消息你收到了么");
        message.getParams().put("group","测试组1");
        message.getParams().put("sound","shake");
        provider.send(message);
    }
}