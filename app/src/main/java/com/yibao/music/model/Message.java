package com.yibao.music.model;

/**
 * @ Author: Luoshipeng
 * @ Name:   Message
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/11/14/ 22:11
 * @ Des:    TODO
 * @author Luoshipeng
 */
public class Message {
    private int code;
    private String msgKey;
    private Object object;

    public Message(int code, String msgKey) {
        this.code = code;
        this.msgKey = msgKey;
    }

    public Message(String msgKey, Object object) {
        this.msgKey = msgKey;
        this.object = object;
    }

    public Message() {
    }

    public Message(int code, Object o) {
        this.code = code;
        this.object = o;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getMsgKey() {
        return msgKey;
    }

    public void setMsgKey(String msgKey) {
        this.msgKey = msgKey;
    }

    @Override
    public String toString() {
        return "Message{" +
                "code=" + code +
                ", msgKey='" + msgKey + '\'' +
                ", object=" + object +
                '}';
    }
}
