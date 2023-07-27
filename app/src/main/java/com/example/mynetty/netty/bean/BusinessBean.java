package com.example.mynetty.netty.bean;

/**
 * <pre>
 *     author : zhangx
 *     time   : 2022/01/20
 *     desc   : Netty业务实体
 *     version: 1.0
 * </pre>
 */
public class BusinessBean {
    /**
     * 业务类型 1：数据更新 2：执行指令
     */
    private int type;
    /**
     * 函数名称
     */
    private String method;

    /**
     * json格式请求参数
     */
    private String requestContext;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(String requestContext) {
        this.requestContext = requestContext;
    }
}
