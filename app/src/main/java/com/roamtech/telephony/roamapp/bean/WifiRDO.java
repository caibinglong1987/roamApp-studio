package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by caibinglong
 * on 2016/11/17.
 * wifi 络漫宝集合
 */

public class WifiRDO<T> implements Serializable {
    public String id;
    public String jsonrpc;
    public List<T> result;
}
