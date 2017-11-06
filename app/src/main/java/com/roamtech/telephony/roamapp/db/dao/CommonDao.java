package com.roamtech.telephony.roamapp.db.dao;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import com.roamtech.telephony.roamapp.db.DatabaseHelper;
import com.roamtech.telephony.roamapp.db.PhoneAddressDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CommonDao<T> {

    private Dao<T, Integer> mDao;
    private DatabaseHelper mHelper;

    private Dao<T, Integer> mAddressDao;
    private PhoneAddressDatabaseHelper addressDatabaseHelper;

    public CommonDao(DatabaseHelper helper, Class<?> clazz) {
        try {
            mHelper = helper;
            mDao = mHelper.getDao(clazz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     */
    public int add(T entity) {
        try {
            return mDao.create(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public T getMaxId() {
        QueryBuilder queryBuilder = mDao.queryBuilder();
        queryBuilder.orderBy("id", false);
        try {
            List<T> data = queryBuilder.query();
            if (data != null) {
                return data.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除一条记录
     */
    public void delete(T entity) {
        try {
            mDao.delete(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一条记录
     */
    public void deleteById(int id) {
        try {
            mDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll(Class<?> dataClass) {
        try {
            TableUtils.clearTable(mDao.getConnectionSource(), dataClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新一条记录
     */
    public void update(T entity) {
        try {
            mDao.update(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询一条记录
     *
     * @param id
     * @return
     */
    public T queryById(int id) {
        T data = null;
        try {
            data = mDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public T queryForFirst() throws SQLException {
        List<T> datas = queryAll();
        if (datas != null && !datas.isEmpty()) {
            return datas.get(0);
        }
        return null;
    }

    /**
     * 删除 数据 chatDBModel
     *
     * @param parent parent 字段
     * @return
     */
    public void deleteByParent(int parent, String loginUserId) {
        DeleteBuilder builder = mDao.deleteBuilder();
        Where where = builder.where();
        try {
            where.eq("parent", parent).and().eq("login_user_id", loginUserId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除 数据 chatDBModel
     *
     * @param parent parent 字段
     * @return
     */
    public void deleteByIdAndParent(String callId, int parent, String loginUserId) {
        DeleteBuilder builder = mDao.deleteBuilder();
        Where where = builder.where();
        try {
            where.eq("parent", parent).and().eq("callid", callId).and().eq("login_user_id", loginUserId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除 数据 chatDBModel
     *
     * @param callId callId 字段
     * @return
     */
    public void deleteByCallId(String callId, String loginUserId) {
        DeleteBuilder builder = mDao.deleteBuilder();
        Where where = builder.where();
        try {
            where.eq("callid", callId).and().eq("login_user_id", loginUserId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除 会话分组记录
     *
     * @param caller caller
     * @param callee callee
     */
    public void deleteByCallerAndCallee(String caller, String callee, String loginUserId) {
        DeleteBuilder deleteBuilder = mDao.deleteBuilder();
        Where where = deleteBuilder.where();
        try {
            where.or(where.like("fromContact", "%" + caller + "%").and().like("toContact", callee),
                    where.like("toContact", "%" + caller + "%").and().like("fromContact", callee));
            where.and().eq("login_user_id", loginUserId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据callStatus 状态 删除 通话分组记录
     *
     * @param otherPhone otherPhone
     */
    public void deleteCallByPhone(String otherPhone, String loginUserId, int callStatus) {
        DeleteBuilder deleteBuilder = mDao.deleteBuilder();
        Where where = deleteBuilder.where();
        try {
            where.or(where.eq("caller", otherPhone),
                    where.eq("callee", otherPhone));
            where.and().eq("login_user_id", loginUserId).and().eq("call_status", callStatus);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除 通话分组记录
     *
     * @param otherPhone otherPhone
     */
    public void deleteCallByPhone(String otherPhone, String loginUserId) {
        DeleteBuilder deleteBuilder = mDao.deleteBuilder();
        Where where = deleteBuilder.where();
        try {
            where.or(where.eq("caller", otherPhone),
                    where.eq("callee", otherPhone));
            where.and().eq("login_user_id", loginUserId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除 消息分组记录
     *
     * @param otherPhone otherPhone
     */
    public void deleteMessageByPhone(String otherPhone, String loginUserId) {
        DeleteBuilder deleteBuilder = mDao.deleteBuilder();
        Where where = deleteBuilder.where();
        try {
            where.or(where.eq("fromContact", otherPhone),
                    where.eq("toContact", otherPhone));
            where.and().eq("login_user_id", loginUserId);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询通话记录
     *
     * @param otherPhone  otherPhone
     * @param loginUserId loginUserId
     */
    public List<T> getAllCallByPhone(String otherPhone, String loginUserId, long startRow, long maxRows) throws SQLException {
        QueryBuilder queryBuilder = mDao.queryBuilder();
        Where where = queryBuilder.orderBy("timestamp", false).where();
        try {
            where.eq("show_number", otherPhone);
            where.and().eq("login_user_id", loginUserId).and().eq("parent", 0);
            queryBuilder.offset(startRow);
            queryBuilder.limit(maxRows);
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询通话记录
     *
     * @param otherPhone  otherPhone
     * @param loginUserId loginUserId
     */
    public List<T> getAllMissCallByPhone(String otherPhone, String loginUserId, long startRow, long maxRows) throws SQLException {
        QueryBuilder queryBuilder = mDao.queryBuilder();
        Where where = queryBuilder.orderBy("timestamp", false).where();
        try {
//            where.or(where.eq("caller", otherPhone),
//                    where.eq("callee", otherPhone));
            where.eq("show_number", otherPhone).and().eq("direction", "0");
            where.and().eq("login_user_id", loginUserId).and().eq("parent", 0).and().eq("call_status", 2);
            queryBuilder.offset(startRow);
            queryBuilder.limit(maxRows);
            return queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<T> queryAll() {
        List<T> datas = new ArrayList<>();
        try {
            datas = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public T queryItem(int id) {
        List<T> list;
        try {
            list = mDao.queryBuilder().where().eq("id", id).query();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T queryItem(String fromPhone) {
        List<T> list;
        try {
            list = mDao.queryBuilder().where().eq("fromContact", fromPhone).and().eq("parent", 1).query();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T queryBlacklistByPhone(String phone, String userId) {
        List<T> list;
        try {
            list = mDao.queryBuilder().where().eq("phone", phone).and().eq("user_id", userId).query();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询 通话 logId
     * 手机 号码 参数
     * 二级 数据
     *
     * @return
     */
    public T queryItemByLogIdAndState(boolean ascending, String from, String loginUserId, int parent, String otherPhone) {
        List<T> list;
        try {
            QueryBuilder queryBuilder = mDao.queryBuilder().orderBy("logId", ascending);
            Where where = queryBuilder.where();
            where.eq("show_number", otherPhone);
            where.and().eq("del_status", 0).and().eq("login_user_id", loginUserId)
                    .and().eq("parent", parent).and().eq("from", from);
            list = queryBuilder.query();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询 通话 logId
     * 一级数据 查询
     *
     * @param ascending true false
     * @return
     */
    public T queryItemByLogIdAndState(boolean ascending, String from, String loginUserId, int parent) {
        List<T> list;
        try {
            QueryBuilder queryBuilder = mDao.queryBuilder().orderBy("logId", ascending);
            Where where = queryBuilder.where();
            where.eq("del_status", 0).and().eq("login_user_id", loginUserId)
                    .and().eq("parent", parent).and().eq("from", from);
            list = queryBuilder.query();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<T> searchMessage(String orderByKey, boolean ascending, String searchKey, String loginPhone, String loginUserId) {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.orderBy(orderByKey, ascending).where();
        try {
            where.or(where.like("fromContact", "%" + searchKey + "%").and().ne("fromContact", loginPhone),
                    where.like("toContact", "%" + searchKey + "%").and().ne("toContact", loginPhone),
                    where.like("message", "%" + searchKey + "%"));
            where.and().eq("login_user_id", loginUserId);
            return builder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询 消息记录
     *
     * @param callId callId
     * @param parent parent
     * @return T
     */
    public T queryByCallIdAndParentAndFrom(String callId, int parent, String userId) {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.where();
        try {
            where.eq("callid", callId).and().eq("parent", parent).and().eq("login_user_id", userId);
            List<T> results = builder.query();
            if (results == null || results.isEmpty()) {
                return null;
            }
            return results.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询 callId 是否存在数据
     *
     * @param eqs map
     * @return T
     */
    public T queryByCallIdAndParentAndFrom(Map<String, Object> eqs) {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.where();
        try {
            int size = eqs.size();
            int index = 0;
            for (Map.Entry<String, Object> eq : eqs.entrySet()) {
                String key = eq.getKey();
                Object value = eq.getValue();
                if (key != null && !"".equals(key) && value != null) {
                    if (index++ != size - 1) {
                        where = where.eq(key, value).and();
                    } else {
                        where = where.eq(key, value);
                    }
                }
            }
            List<T> results = builder.query();
            if (results == null || results.isEmpty()) {
                return null;
            }
            return results.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T queryByConditionSingle(String orderByKey, boolean ascending, boolean eqWhere, String phone, String loginUserId)
            throws SQLException {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.orderBy(orderByKey, ascending).where();
        if (eqWhere) {
            where.or(where.eq("toContact", phone).and().eq("parent", 0),
                    where.eq("fromContact", phone).and().eq("parent", 0));
        } else {
            where.eq("parent", 1);
        }
        where.and().eq("login_user_id", loginUserId);
        T result = (T) builder.queryForFirst();
        if (result == null) {
            return null;
        }
        return result;
    }

    /**
     * 分页
     ***/
    public List<T> queryByConditionLimit(String orderByKey, boolean ascending, Map<String, Object> eqs,
                                         long startRow, long maxRows) throws SQLException {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.orderBy(orderByKey, ascending).where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            }
        }
        builder.offset(startRow);
        builder.limit(maxRows);
        List<T> results = builder.query();
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results;
    }

    /**
     * 分页
     ***/
    public List<T> queryByConditionLimit(String orderByKey, boolean ascending, String groupByKey,
                                         Map<String, Object> eqs,
                                         long startRow, long maxRows) throws SQLException {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.orderBy(orderByKey, ascending).groupBy(groupByKey).where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            }
        }
        builder.offset(startRow);
        builder.limit(maxRows);
        List<T> results = builder.query();
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results;
    }

    /**
     * 查询所有分页
     *
     * @param orderByKey
     * @param ascending
     * @param startRow
     * @param maxRows
     * @return
     * @throws SQLException
     */
    public List<T> queryAllLimit(String orderByKey, boolean ascending, String phone, String loginUserId,
                                 long startRow, long maxRows) throws SQLException {
        QueryBuilder builder = mDao.queryBuilder();
        Where where = builder.orderBy(orderByKey, ascending).where();
        where.or(where.eq("toContact", phone).and().eq("parent", 0),
                where.eq("fromContact", phone).and().eq("parent", 0));
        where.and().eq("login_user_id", loginUserId);
        builder.offset(startRow);
        builder.limit(maxRows);
        List<T> results = builder.query();
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results;
    }

    public void update(Map<String, Object> eqs, Map<String, Object> updates) throws SQLException {
        UpdateBuilder updateBuilder = mDao.updateBuilder();
        Where where = updateBuilder.where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            }
        }

        for (Map.Entry<String, Object> update : updates.entrySet()) {
            String key = update.getKey();
            Object value = update.getValue();
            if (key != null && !"".equals(key) && value != null) {
                updateBuilder = updateBuilder.updateColumnValue(key, value);
            }
        }
        updateBuilder.update();
    }

    public void updateUnreadNumber(String phone, int unreadNumber, String userId) {
        UpdateBuilder updateBuilder = mDao.updateBuilder();
        Where where = updateBuilder.where();
        try {
            where.eq("fromContact", phone).and().eq("parent", 1).and().eq("login_user_id", userId);
            updateBuilder.updateColumnValue("unread_num", unreadNumber);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Map<String, Object> eqs) throws SQLException {
        DeleteBuilder deleteBuilder = mDao.deleteBuilder();
        Where where = deleteBuilder.where();
        int size = eqs.size();
        int index = 0;
        for (Map.Entry<String, Object> eq : eqs.entrySet()) {
            String key = eq.getKey();
            Object value = eq.getValue();
            if (key != null && !"".equals(key) && value != null) {
                if (index++ != size - 1) {
                    where = where.eq(key, value).and();
                } else {
                    where = where.eq(key, value);
                }
            }
        }
        deleteBuilder.delete();
    }


    /**
     * 删除 集合数据
     *
     * @param list list
     */
    public void deleteMultiple(ArrayList<T> list) {
        try {
            mDao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
