package com.roamtech.telephony.roamapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.roamtech.telephony.roamapp.application.AppConfig;
import com.roamtech.telephony.roamapp.helper.numberAttr.NumberResource;

import java.io.IOException;

/**
 * Created by caibinglong
 * on 2017/2/15.
 */

public class PhoneAddressDatabaseHelper {
    private SQLiteDatabase database;
    private Context context;

    public PhoneAddressDatabaseHelper(Context context) {
        this.context = context;
        this.database = openDatabase(AppConfig.NUMBER_ADDRESS_DB_PATH);
    }

    private SQLiteDatabase openDatabase(String dbPath) {
        try {
            NumberResource.copyDataBase(context, dbPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
        return database;
    }

    public String queryData(String number) {
        String address = "未知";
        if (database != null) {
            Cursor cs = null;
            try {
                cs = database.rawQuery("select * from areas where phone= ? or city_code= ? limit 1", new String[]{number, number});
                while (cs.moveToNext()) {
                    int pIndex = cs.getColumnIndex("province");
                    int cIndex = cs.getColumnIndex("city");
                    if (pIndex > 0 && cIndex > 0) {
                        String province = cs.getString(pIndex);
                        String city = cs.getString(cIndex);
                        if (province.equals(city)) {
                            address = province;
                        } else {
                            address = province + city;
                        }
                    }
                    break;
                }
                cs.close();
                database.close();
            } catch (Exception ex) {
                if (cs != null) {
                    cs.close();
                    database.close();
                }
            }
        }
        return address;
    }
}
