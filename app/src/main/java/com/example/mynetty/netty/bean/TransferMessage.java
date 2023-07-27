package com.example.mynetty.netty.bean;

import java.io.Serializable;

/**
 * @author Gaocs
 * @classname TransferMessage
 * @description 设备交互传输报文
 * @date 2022-01-19 20:54
 */
public class TransferMessage implements Serializable {

    /**
     * 传输唯一id--用于获取设备结果信息
     */
    String transferId;

    /**
     * 业务数据
     */
    String businessMessage;

    public TransferMessage() {
    }

    public TransferMessage(String transferId, String businessMessage) {
        this.transferId = transferId;
        this.businessMessage = businessMessage;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getBusinessMessage() {
        return businessMessage;
    }

    public void setBusinessMessage(String businessMessage) {
        this.businessMessage = businessMessage;
    }
}
