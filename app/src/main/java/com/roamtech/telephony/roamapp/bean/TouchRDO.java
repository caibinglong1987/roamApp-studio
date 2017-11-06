package com.roamtech.telephony.roamapp.bean;

import com.roamtech.telephony.roamapp.db.model.TouchDBModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin03 on 2016/8/10.
 */
public class TouchRDO implements Serializable {
    private List<TouchDBModel> touchs;
    public List<TouchDBModel> getTouchs() {
        return touchs;
    }

    public void setTouchs(List<TouchDBModel> touchs) {
        this.touchs = touchs;
    }
}
