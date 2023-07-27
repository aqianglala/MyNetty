package com.example.mynetty.netty.bean;


import java.io.Serializable;

/**
 * <p>
 * 设备心跳表
 * </p>
 *
 * @author zhangx
 * @since 2021-12-08
 */
public class EquipHeartbeat implements Serializable {
    public static final long serialVersionUID = 1L;
    /**
     * FId
     */
    public String fId = "0";

    /**
     * 状态
     */
    public Integer fStatus ;

    /**
     * 编号
     */
    public String fNumber ;

    /**
     * 名称
     */
    public String fName ;

    /**
     * 表名
     */
    public String fTableName ;

    /**
     * 签名
     */
    public String fSign ;

    /**
     * 创建时间
     */
    public Long fCreateTime ;

    /**
     * 创建人ID
     */
    public String fCreatorId ;

    /**
     * 修改时间
     */
    public Long fLastUpdateTime ;

    /**
     * 修改人ID
     */
    public String fLastUpdateUserId ;
}
