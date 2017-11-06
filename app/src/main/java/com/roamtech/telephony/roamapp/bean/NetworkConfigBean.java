package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by long
 * on 2016/10/13.
 * 络漫宝 网络配置
 */

public class NetworkConfigBean implements Serializable {
    public String wan_proto = "";
    public String wan_type = "";
    public String wan_ip = "";
    public String wan_isup = "false"; //true 联网 false 没网
    public List<String> wan_dnsaddrs;
    public String wan_uptime = "";
    public String wan_gateway = ""; //网关
    public String wan_netmask = "";//子网掩码

    public String lan_ssid = ""; // 络漫宝ssID
    public String lan_channel = "";
    public String lan_password = "";
    public String lan_ip = "";

    public String apcli_ssid = ""; //无线中继
    public String apcli_password = "";

    public String pppoe_username= ""; //宽带拨号
    public String pppoe_password = "";

    public String devid = "";
    public String phone = "";
    public String operator = ""; //运营商

    public String version = "";
    public String sip_status = "false"; //UP 正常上网 状态
    public int type = 0;// 1 是有线 自动获取IP 2是有线拨号 3是有线静态  4是无线中继
    public String check_ssId = ""; //选中连接的wifi

    @Override
    public String toString() {
        return "NetworkConfigDBModel{" +
                "wan_proto='" + wan_proto + '\'' +
                ", wan_type='" + wan_type + '\'' +
                ", wan_ip='" + wan_ip + '\'' +
                ", wan_isUp='" + wan_isup + '\'' +
                ", wan_dnsaddrs=" + wan_dnsaddrs +
                ", wan_uptime='" + wan_uptime + '\'' +
                ", wan_gateway='" + wan_gateway + '\'' +
                ", wan_netMask='" + wan_netmask + '\'' +
                ", lan_ssid='" + lan_ssid + '\'' +
                ", lan_channel='" + lan_channel + '\'' +
                ", lan_password='" + lan_password + '\'' +
                ", lan_ip='" + lan_ip + '\'' +
                ", apcli_ssid='" + apcli_ssid + '\'' +
                ", apcli_password='" + apcli_password + '\'' +
                ", devid='" + devid + '\'' +
                ", phone='" + phone + '\'' +
                ", operator='" + operator + '\'' +
                ", version='" + version + '\'' +
                ", sip_status='" + sip_status + '\'' +
                '}';
    }
}
