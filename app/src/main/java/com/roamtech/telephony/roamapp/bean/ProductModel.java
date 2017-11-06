package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by long
 * on 2016/9/26 14:37
 * 商品model
 */

public class ProductModel implements Serializable {
    public int brandid;
    public int categoryid;
    public String createtime;
    public String description;
    public int groupid;
    public int id;
    public String image;
    public boolean is_delete;
    public boolean is_package;
    public boolean is_real;
    public String modifytime;
    public String name;
    public List<Packages> packages;
    public List<Prdattrs> prdattrs;
    public int quantity_per_unit;
    public int sale_state;
    public int sell_count;
    public long stock_number;
    public int storeid;
    public String subname;
    public int typeid;
    public String unit_price;
    public int validity;

    public class Packages {

    }
    public class Prdattrs{

    }
}
