package com.example.mynetty.netty.bean;

import java.io.Serializable;

/**
 * 消息类型分离器
 * @author Administrator
 *
 */
public class Model implements Serializable {

    private static final long serialVersionUID = 1L;

    //类型
    private int style;//1发送心跳  2收到心跳  3消息
    private String method;

    //内容
    private String body;

    public String  getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return style;
    }

    public void setType(int type) {
        this.style = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Model{" +
                "type=" + style +
                ", method='" + method + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

