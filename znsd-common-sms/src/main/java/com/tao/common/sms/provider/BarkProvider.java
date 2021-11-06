package com.tao.common.sms.provider;

import com.tao.common.sms.constant.SmsConstant;
import com.tao.common.sms.model.BarkMessage;
import com.tao.common.sms.model.Message;
import com.tao.common.sms.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;


/**
 * @ClassName BarkProvider
 * @Author tao.he
 * @Since 2021/11/6 14:12
 */
@Slf4j
public class BarkProvider implements PushProvider<BarkMessage>{

    @Override
    public void send(BarkMessage message) {
        OkHttpUtils.builder().url(SmsConstant.BARK_URL.concat(message.getTitle()).concat("/").concat(message.getContent()))
                        .addParamMap(message.getParams())
                        .get()
                        //使用异步需要用信号量去锁住,避免主线程直接结束 Semaphore
                        .async(new OkHttpUtils.ICallBack() {
                            @Override
                            public void onSuccessful(Call call, String data) {
                                saveSuccessProvider();
                            }

                            @Override
                            public void onFailure(Call call, Throwable e) {
                                saveErrorProvider(e);
                            }
                        });
    }

    @Override
    public void asyncSend(BarkMessage message) {
        PushProvider.super.asyncSend(message);
    }

    @Override
    public void saveErrorProvider(Throwable e) {
        log.error("发送消息失败");
        PushProvider.super.saveErrorProvider(e);
    }

    @Override
    public void saveSuccessProvider() {
        log.info("发送消息成功");
        PushProvider.super.saveSuccessProvider();
    }
}
