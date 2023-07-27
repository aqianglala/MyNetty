package com.example.mynetty.netty.bean;

import java.io.Serializable;

/**
 * @author Gaocs
 * @classname DeviceInfo
 * @description 设备信息
 * @date 2022-01-18 22:03
 */
public class EquipmentInfo implements Serializable {

    /**
     * 设备sn
     */
    protected String equipmentSn;

    /**
     * 设备ip地址
     */
    protected String ip;

    /**
     * 设备版本号
     */
    protected String version;

    public EquipmentInfo() {
    }

    public EquipmentInfo(String equipmentSn, String ip, String version) {
        this.equipmentSn = equipmentSn;
        this.ip = ip;
        this.version = version;
    }

    public String getEquipmentSn() {
        return equipmentSn;
    }

    public void setEquipmentSn(String equipmentSn) {
        this.equipmentSn = equipmentSn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
