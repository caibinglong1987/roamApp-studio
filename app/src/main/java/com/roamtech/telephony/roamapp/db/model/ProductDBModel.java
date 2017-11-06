package com.roamtech.telephony.roamapp.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.nostra13.universalimageloader.utils.L;
import com.roamtech.telephony.roamapp.bean.PackagesModel;
import com.roamtech.telephony.roamapp.bean.PrdattrsModel;
import com.roamtech.telephony.roamapp.bean.ProductModel;
import com.roamtech.telephony.roamapp.db.DBConfig;

import java.io.Serializable;
import java.util.List;

/**
 * Created by long
 * on 2016/9/27 09:23
 * 商品表
 */
@DatabaseTable(tableName = DBConfig.TABLE_PRODUCT)
public class ProductDBModel implements Serializable {
    @DatabaseField(columnName = "id")
    public int id;
    @DatabaseField(columnName = "brandid")
    public int brandid;
    @DatabaseField(columnName = "categoryid")
    public int categoryid;
    @DatabaseField(columnName = "createtime")
    public String createtime;
    @DatabaseField(columnName = "description")
    public String description;
    @DatabaseField(columnName = "groupid")
    public int groupid;
    @DatabaseField(columnName = "image")
    public String image;
    @DatabaseField(columnName = "is_delete")
    public String is_delete;
    @DatabaseField(columnName = "is_package")
    public String is_package;
    @DatabaseField(columnName = "is_real")
    public String is_real;
    @DatabaseField(columnName = "modifytime")
    public String modifytime;
    @DatabaseField(columnName = "name")
    public String name;
    @DatabaseField(columnName = "quantity_per_unit")
    public String quantity_per_unit;
    @DatabaseField(columnName = "sale_state")
    public String sale_state;
    @DatabaseField(columnName = "sell_count")
    public String sell_count;
    @DatabaseField(columnName = "stock_number")
    public String stock_number;
    @DatabaseField(columnName = "storeid")
    public String storeid;
    @DatabaseField(columnName = "subname")
    public String subname;
    @DatabaseField(columnName = "typeid")
    public int typeid;
    @DatabaseField(columnName = "unit_price")
    public String unit_price;
    @DatabaseField(columnName = "validity")
    public String validity;

    public List<PackagesModel> getPackages() {
        return packages;
    }

    public void setPackages(List<PackagesModel> packages) {
        this.packages = packages;
    }

    public List<PrdattrsModel> getPrdattrs() {
        return prdattrs;
    }

    public void setPrdattrs(List<PrdattrsModel> prdattrs) {
        this.prdattrs = prdattrs;
    }

    private List<PackagesModel> packages;
    private List<PrdattrsModel> prdattrs;
}
