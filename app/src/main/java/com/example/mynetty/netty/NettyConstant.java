package com.example.mynetty.netty;

/**
 * <pre>
 *     author : zhangx
 *     time   : 2022/01/20
 *     desc   : Netty相关常量
 *     version: 1.0
 * </pre>
 */
public class NettyConstant {
    /**
     * IP，测试环境
     */
    public static final String HOST = "192.168.3.173";
    /**
     * 端口
     */
    public static final int PORT = 12009;

    /**
     * 最大长度
     */
    public static final int MAX_FRAME_LENGTH = 10 * 1024 * 1024;
    /**
     * 长度字段所占的字节数
     */
    public static final int LENGTH_FIELD_LENGTH = 4;
    /**
     * 长度偏移
     */
    public static final int LENGTH_FIELD_OFFSET = 2;
    public static final int LENGTH_ADJUSTMENT = 0;


    //传输协议类型======================================
    public static final byte TYPE_TRUEFACE = 0X01;

    //传输协议标志======================================
    /**
     * 建立连接
     */
    public static final byte FLAG_CONNECT = 0X01;
    /**
     * 心跳信息
     */
    public static final byte FLAG_HEARTBEAT = 0X02;
    /**
     * 业务数据
     */
    public static final byte FLAG_BUSINESS = 0X04;
    /**
     * 关闭连接
     */
    public static final byte FLAG_DISCONNECT = 0X08;

    //业务数据类型======================================
    /**
     * 数据更新
     */
    public static final int BUSINESS_TYPE_SYC_DATA = 1;

    /**
     * 执行指令
     */
    public static final int BUSINESS_TYPE_INSTRUCTION = 2;
}
