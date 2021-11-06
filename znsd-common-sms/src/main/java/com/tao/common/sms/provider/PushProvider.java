package com.tao.common.sms.provider;

import com.tao.common.sms.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * @InterfaceName PushProvider
 * @Author HETAO
 * @Date 2021/11/6 13:53
 */
public interface PushProvider<T extends Message> {
    static final Logger logger = LoggerFactory.getLogger(PushProvider.class);

    /**
     * 调用接口
     */
    void send(T message);

    /**
     * 异步调用接口
     */

    default void asyncSend(T message) {
        CompletableFuture.runAsync(() -> {
           this.send(message);
        }).exceptionally(ex -> {
            this.saveErrorProvider(ex);
            return null;
        });
    }

    /**
     * 发送失败回调
     */
    default void saveErrorProvider(Throwable e) {
        logger.error("消息通知异常:",e);
        //HETAO_TODO
    };

    /**
     * 成功回调
     */
    default void saveSuccessProvider() {
        //HETAO_TODO
    };


}
