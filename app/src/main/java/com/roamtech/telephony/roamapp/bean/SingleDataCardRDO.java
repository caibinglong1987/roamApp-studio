package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin03 on 2016/8/5.
 */
public class SingleDataCardRDO implements Serializable {
    GlobalCard datacard;

    public GlobalCard getDatacard() {
        return datacard;
    }

    public void setDatacard(GlobalCard datacard) {
        this.datacard = datacard;
    }
}
