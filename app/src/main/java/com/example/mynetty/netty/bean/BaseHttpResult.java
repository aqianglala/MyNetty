package com.example.mynetty.netty.bean;


import org.jetbrains.annotations.NotNull;

/**
 * 根据后台返回的基本格式修改此类
 * @param <T>
 */
public class BaseHttpResult<T> {
    private String code;
    private String message;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 是否成功响应
     * @return true  成功 false失败
     */
    public boolean isSuccess(){
        return code != null && code.equals("0");
    }

    @NotNull
    @Override
    public String toString() {
        return "BaseHttpResult{" +
                "status=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}