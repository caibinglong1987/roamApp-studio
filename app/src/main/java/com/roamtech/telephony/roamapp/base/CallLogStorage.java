package com.roamtech.telephony.roamapp.base;
/*
ChatStorage.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.bean.CallDetailRecordDBModel;
import com.roamtech.telephony.roamapp.event.EventCallHistory;

import org.greenrobot.eventbus.EventBus;
import org.linphone.LinphoneService;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallLog;
import org.linphone.mediastream.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sylvain Berfini
 */
public class CallLogStorage {
    private static final int INCOMING = 1;
    private static final int OUTGOING = 0;

    private static CallLogStorage instance;
    private Context context;
    private SQLiteDatabase db;
    private boolean useNativeAPI;
    private static final String TABLE_NAME = "call_history";

    public synchronized static final CallLogStorage getInstance() {
        if (instance == null)
            instance = new CallLogStorage(LinphoneService.instance().getApplicationContext());
        return instance;
    }

    public void restartCallLogStorage() {
        if (instance != null)
            instance.close();
        instance = new CallLogStorage(LinphoneService.instance().getApplicationContext());
    }

    private boolean isVersionUsingNewChatStorage() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode >= 2200;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private CallLogStorage(Context c) {
        context = c;
        //boolean useLinphoneStorage = c.getResources().getBoolean(R.bool.use_linphone_chat_storage);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LinphoneService.instance());
        //boolean updateNeeded = prefs.getBoolean(c.getString(R.string.pref_first_time_linphone_chat_storage), !LinphonePreferences.instance().isFirstLaunch());
        //updateNeeded = updateNeeded && !isVersionUsingNewChatStorage();
        useNativeAPI = false;//useLinphoneStorage && !updateNeeded;
        Log.d("Using native API: " + useNativeAPI);

        if (!useNativeAPI) {
            CallHelper callHelper = new CallHelper(context);
            db = callHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (!useNativeAPI) {
            db.close();
        }
    }

    public void saveCallLog(LinphoneCall call, String userId) {
        if (useNativeAPI) {
            return;
        }

        ContentValues values = new ContentValues();
        LinphoneCallLog callLog = call.getCallLog();
        values.put("caller", callLog.getFrom().asString());
        values.put("callee", callLog.getTo().asString());
        values.put("direction", callLog.getDirection() == CallDirection.Incoming ? 1 : 0);
        values.put("duration", callLog.getCallDuration());
        values.put("start_time", callLog.getTimestamp() + "");
        //values.put("connected_time", "");
        values.put("status", callLog.getStatus().toInt());

        values.put("quality", (int) call.getAverageQuality());
        values.put("userid", userId);
        //values.put("call_id", "");
        //values.put("videoEnabled",0);
        //values.put("refkey","");
//		try {
//		//	db.execSQL("INSERT INTO call_history(caller,callee,direction,duration,start_time,status,quality,userid) VALUES(?,?,?,?,?,?,?,?);", new Object[]{callLog.getFrom().asString(), callLog.getTo().asString(), callLog.getDirection() == CallDirection.Incoming ? 1 : 0, callLog.getCallDuration(), callLog.getTimestamp(), callLog.getStatus().toInt(), call.getAverageQuality(), LinphoneActivity.instance().getLoginInfo().getUserId()});
//		} catch (SQLException ex) {
//			Log.w(ex,ex.getMessage());
//		}
        int id = (int) db.insert(TABLE_NAME, null, values);
        if (id > 0) {
            EventBus.getDefault().post(EventCallHistory.getInsertEvent(id));
        } else {
            Log.w("saveCallLog failed:" + id);
        }
        //return 0;
    }

    /**
     * 获取所有通话记录
     *
     * @param correspondent
     * @param limit
     * @return
     */
    public List<CallDetailRecordDBModel> getCallLogs(String correspondent, String limit) {
        List<CallDetailRecordDBModel> callLogs = new ArrayList<CallDetailRecordDBModel>();

        if (!useNativeAPI) {
            String selection = "caller LIKE \"%" + correspondent + "%\" or callee LIKE \"%" + correspondent + "%\"";
            Log.e("getCallLogs:" + selection);
            Cursor cursor;
            if (limit == null) {
                cursor = db.query(TABLE_NAME, null, selection, null, null, null, "id DESC");
            } else {
                cursor = db.query(TABLE_NAME, null, selection, null, null, null, "id DESC", limit);
            }
            while (cursor.moveToNext()) {
                try {
                    CallDetailRecordDBModel callLog = getCallLog(cursor);
                    if (callLog != null) {
                        callLogs.add(callLog);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        return callLogs;
    }

    /**
     * 获取未接来电
     *
     * @param correspondent
     * @param limit
     * @return
     */
    public List<CallDetailRecordDBModel> getMissCallLogs(String correspondent, String limit) {
        List<CallDetailRecordDBModel> callLogs = new ArrayList<>();
        if (!useNativeAPI) {
            String selection = "status = 2 and (caller LIKE \"%" + correspondent + "%\" or callee LIKE \"%" + correspondent + "%\")";
            Log.e("getCallLogs:" + selection);
            Cursor cursor;
            if (limit == null) {
                cursor = db.query(TABLE_NAME, null, selection, null, null, null, "id DESC");
            } else {
                cursor = db.query(TABLE_NAME, null, selection, null, null, null, "id DESC", limit);
            }
            while (cursor.moveToNext()) {
                try {
                    CallDetailRecordDBModel callLog = getCallLog(cursor);
                    if (callLog != null) {
                        callLogs.add(callLog);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        Log.e("未接电话:" + callLogs.size());
        return callLogs;
    }

    private CallDetailRecordDBModel getCallLog(Cursor c) {
        String caller, callee, timestamp;
        String userid = c.getString(c.getColumnIndex("userid"));
        if (userid == null || userid.isEmpty()) {
            userid = LinphoneActivity.instance().getLoginInfo().getUserId();
        } else if (!userid.equals(LinphoneActivity.instance().getLoginInfo().getUserId())) {
            return null;
        }
        int id = c.getInt(c.getColumnIndex("id"));
        int direction = c.getInt(c.getColumnIndex("direction"));
        caller = c.getString(c.getColumnIndex("caller"));
        callee = c.getString(c.getColumnIndex("callee"));
        timestamp = c.getString(c.getColumnIndex("start_time"));
        int status = c.getInt(c.getColumnIndex("status"));
        int duration = c.getInt(c.getColumnIndex("duration"));
        int quality = c.getInt(c.getColumnIndex("quality"));

        String callId = "-1";
        CallDetailRecordDBModel callLog = new CallDetailRecordDBModel(id, caller, callee, Long.parseLong(timestamp), duration, direction, status, quality, userid, callId, userid);
        return callLog;
    }

    public CallDetailRecordDBModel getCallLog(int id) {
        Cursor c = db.query(TABLE_NAME, null, "id=" + id, null, null, null, null);
        c.moveToFirst();
        return getCallLog(c);
    }

    public void deleteCallLog(int id) {
        {
            db.delete(TABLE_NAME, "id LIKE " + id, null);
//			if(rowcnt == 1) {
//				EventBus.getDefault().post(EventCallHistory.getDeleteEvent(id));
//			}
        }
    }

    class CallHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 18;
        private static final String DATABASE_NAME = "linphone-android";

        CallHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e("database--> callLogStorage onCreate");
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, caller TEXT NOT NULL, callee TEXT NOT NULL, direction INTEGER, duration INTEGER, start_time TEXT NOT NULL, status INTEGER, quality INTEGER, userid TEXT);");
            //db.execSQL("CREATE TABLE " + DRAFT_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, toContact TEXT NOT NULL, message TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e("database--> callLogStorage onUpgrade");
            /*if(oldVersion < 17) {

			} else {
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
				//db.execSQL("DROP TABLE IF EXISTS " + DRAFT_TABLE_NAME + ";");
				onCreate(db);
			}*/
            String columns = getColumnNames(db, TABLE_NAME);
            updateTable(db, TABLE_NAME, columns);
        }

        private void updateTable(SQLiteDatabase db, String tableName, String columns) {
            try {
                db.beginTransaction();
                String reColumn = columns.substring(0, columns.length() - 1);
// rename the table
                String tempTable = tableName + "texp_temptable";
                String sql = "alter table " + tableName + " rename to " + tempTable;
                db.execSQL(sql);


// drop the oldtable
                String dropString = "drop table if exists " + tableName;
                db.execSQL(dropString);
// creat table
                String ss = "create table if not exists " + tableName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, caller TEXT NOT NULL, callee TEXT NOT NULL, direction INTEGER, duration INTEGER, start_time TEXT NOT NULL, status INTEGER, quality INTEGER, userid Long);";
                db.execSQL(ss);
// load data
                String newStr = "userid";
                String newreColumn = reColumn + "," + newStr;
                String ins = "insert into " + tableName + " (" + newreColumn + ") " + "select " + reColumn + "" + " " + " from "
                        + tempTable;
                db.equals(ins);
                db.setTransactionSuccessful();
            } catch (Exception e) {
// TODO: handle exception
                Log.i("tag", e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        // 获取升级前表中的字段
        protected String getColumnNames(SQLiteDatabase db, String tableName) {
            StringBuffer sb = null;
            Cursor c = null;
            try {
                c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
                if (null != c) {
                    int columnIndex = c.getColumnIndex("name");
                    if (-1 == columnIndex) {
                        return null;
                    }

                    int index = 0;
                    sb = new StringBuffer(c.getCount());
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        sb.append(c.getString(columnIndex));
                        sb.append(",");
                        index++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }

            return sb.toString();
        }
    }
}
