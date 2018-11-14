package com.yibao.music.model;

/**
 * @ Author: Luoshipeng
 * @ Name:   Message
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/11/14/ 22:11
 * @ Des:    TODO
 */
public class Message {
    private int code;
    private Object object;

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
}
