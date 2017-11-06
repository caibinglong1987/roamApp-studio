package com.roamtech.telephony.roamapp.bean;

import com.roamtech.telephony.roamapp.db.model.HomePageDBModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by long
 * on 2016/10/23.
 */

public class HomePageRDO implements Serializable {
    public List<HomePageDBModel> homepages;
}
