package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin03 on 2016/8/5.
 */
public class DataCardRDO implements Serializable {
    List<GlobalCard> datacards;

    public List<GlobalCard> getDatacards() {
        return datacards;
    }

    public void setDatacards(List<GlobalCard> datacards) {
        this.datacards = datacards;
    }
}
