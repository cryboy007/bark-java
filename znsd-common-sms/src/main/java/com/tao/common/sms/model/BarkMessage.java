package com.tao.common.sms.model;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName BarkMessage
 * @Author tao.he
 * @Since 2021/11/6 15:37
 */
public class BarkMessage extends Message{
    /**
     * group:分组
     * sound:铃声
     * isArchive:1 自动保存
     * icon:url 图标
     * level:时效性
     * url:url 点击自动跳转
     * copy: 只复制值
     * copy:optional 选择
     * automaticallyCopy 自动复制内容
     */
    private ConcurrentHashMap<String,String> params = new ConcurrentHashMap<>(8);


    public BarkMessage(String title, String content) {
        super(title, content);
    }

    public ConcurrentHashMap<String, String> getParams() {
        return params;
    }

    public void setParams(ConcurrentHashMap<String, String> params) {
        this.params = params;
    }
}
