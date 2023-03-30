package com.wr.module;

import java.io.Serializable;

/**
 * @author : WangRui
 * @date : 2023/3/30
 */

public class ResponseData<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public ResponseData(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;

    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
