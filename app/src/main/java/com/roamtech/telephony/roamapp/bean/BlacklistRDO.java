package com.roamtech.telephony.roamapp.bean;

import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by caibinglong
 * on 2017/3/13.
 */

public class BlacklistRDO implements Serializable {
    public List<BlacklistDBModel> blackList;
}
