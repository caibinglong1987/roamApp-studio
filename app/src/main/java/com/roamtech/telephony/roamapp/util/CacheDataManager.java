package com.roamtech.telephony.roamapp.util;

import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.db.dao.CommonDao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CacheDataManager<T> {

    private static CacheDataManager instance;
    private CommonDao<T> mDao;

    private CacheDataManager(Class<?> dataClass) {
        mDao = new CommonDao<>(RoamApplication.sDatabaseHelper, dataClass);
    }

    public static CacheDataManager getInstance(Class<?> dataClass) {
        if (instance == null) {
            synchronized (CacheDataManager.class) {
                if (instance == null) {
                    instance = new CacheDataManager(dataClass);
                }
            }
        }
        return instance;
    }

    public int save(T entity) {
        return mDao.add(entity);
    }

    public void update(String key, Object value, String userId) {
        Map<String, Object> eqs = new HashMap<>();
        eqs.put("id", userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put(key, value);
        try {
            mDao.update(eqs, updates);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll(Class<?> dataClass) {
        mDao.deleteAll(dataClass);
    }

}
