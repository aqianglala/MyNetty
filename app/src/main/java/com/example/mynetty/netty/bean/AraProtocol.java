package com.example.mynetty.netty.bean;

import java.io.Serializable;

/**
 * @author Gaocs
 * @classname AraProtocol
 * @description 协议
 * @date 2022-01-17 16:23
 */
public class AraProtocol implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 平台类型 0X01表示TruFace
     */
    private byte type;

    /**
     * 传输标志
     * 0X01: 表示建立连接
     * 0X02: 表示心跳信息
     * 0X04: 表示业务数据
     * 0X08: 表示关闭连接
     */
    private byte flag;

    /**
     * 报文长度
     */
    private int length;

    /**
     * 报文
     */
    private String content;

    /**
     * 无参构造
     */
    public AraProtocol() {
    }

    /**
     * 构造函数
     *
     * @param type    平台类型
     * @param flag    传输标志
     * @param content 报文
     */
    public AraProtocol(byte type, byte flag, String content) {
        this.type = type;
        this.flag = flag;
        this.content = content;
    }

    /**
     * 全参构造
     *
     * @param type    平台类型
     * @param flag    传输标志
     * @param length  报文长度
     * @param content 报文
     */
    public AraProtocol(byte type, byte flag, int length, String content) {
        this.type = type;
        this.flag = flag;
        this.length = length;
        this.content = content;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
