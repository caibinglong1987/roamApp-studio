package org.linphone;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.bean.VoiceNumber;
import com.roamtech.telephony.roamapp.db.dao.CommonDao;
import com.roamtech.telephony.roamapp.db.model.ChatDBModel;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

/**
 * @author Sylvain Berfini
 */
public class ChatStorage {
    private static final int INCOMING = 1;
    private static final int OUTGOING = 0;
    private static final int READ = 1;
    private static final int NOT_READ = 0;

    private static ChatStorage instance;
    private Context context;
    private SQLiteDatabase db;
    private boolean useNativeAPI;
    private static final String TABLE_NAME = "chat";
    private static final String DRAFT_TABLE_NAME = "chat_draft";

    public synchronized static final ChatStorage getInstance() {
        if (instance == null)
            instance = new ChatStorage(LinphoneService.instance().getApplicationContext());
        return instance;
    }

    public void restartChatStorage() {
        if (instance != null)
            instance.close();
        instance = new ChatStorage(LinphoneService.instance().getApplicationContext());
    }

    private boolean isVersionUsingNewChatStorage() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode >= 2200;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private ChatStorage(Context c) {
        context = c;
        boolean useLinphoneStorage = c.getResources().getBoolean(R.bool.use_linphone_chat_storage);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LinphoneService.instance());
        boolean updateNeeded = prefs.getBoolean(c.getString(R.string.pref_first_time_linphone_chat_storage), !LinphonePreferences.instance().isFirstLaunch());
        updateNeeded = updateNeeded && !isVersionUsingNewChatStorage();
        useNativeAPI = useLinphoneStorage && !updateNeeded;
        Log.d("Using native API: " + useNativeAPI);
        if (!useNativeAPI) {
            ChatHelper chatHelper = new ChatHelper(context);
            db = chatHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (!useNativeAPI) {
            db.close();
        }
    }

    public int saveImageMessage(String from, String to, Bitmap image, String url, long time) {
        if (useNativeAPI) {
            return -1;
        }
        ContentValues values = new ContentValues();
        if (from.equals("")) {
            values.put("fromContact", from);
            values.put("toContact", to);
            values.put("direction", OUTGOING);
            values.put("read", READ);
            values.put("status", LinphoneChatMessage.State.InProgress.toInt());
        } else if (to.equals("")) {
            values.put("fromContact", to);
            values.put("toContact", from);
            values.put("direction", INCOMING);
            values.put("read", NOT_READ);
            values.put("status", LinphoneChatMessage.State.Idle.toInt());
        }
        values.put("url", url);

        if (image != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(CompressFormat.JPEG, 100, baos);
            values.put("image", baos.toByteArray());
        }

        values.put("timestamp", time);
        return (int) db.insert(TABLE_NAME, null, values);
    }

    public int saveDraft(String to, String message) {
        if (useNativeAPI) {
            //TODO
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("toContact", to);
        values.put("message", message);
        return (int) db.insert(DRAFT_TABLE_NAME, null, values);
    }

    public void updateDraft(String to, String message) {
        if (useNativeAPI) {
            //TODO
            return;
        }

        ContentValues values = new ContentValues();
        values.put("message", message);

        db.update(DRAFT_TABLE_NAME, values, "toContact LIKE \"" + to + "\"", null);
    }

    public void deleteDraft(String to) {
        if (useNativeAPI) {
            //TODO
            return;
        }

        db.delete(DRAFT_TABLE_NAME, "toContact LIKE \"" + to + "\"", null);
    }

    public String getDraft(String to) {
        if (useNativeAPI) {
            //TODO
            return "";
        }

        Cursor c = db.query(DRAFT_TABLE_NAME, null, "toContact LIKE \"" + to + "\"", null, null, null, "id ASC");

        String message = null;
        while (c.moveToNext()) {
            try {
                message = c.getString(c.getColumnIndex("message"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();

        return message;
    }

    public List<String> getDrafts() {
        List<String> drafts = new ArrayList<String>();

        if (useNativeAPI) {
            //TODO
        } else {
            Cursor c = db.query(DRAFT_TABLE_NAME, null, null, null, null, null, "id ASC");

            while (c.moveToNext()) {
                try {
                    String to = c.getString(c.getColumnIndex("toContact"));
                    drafts.add(to);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }

        return drafts;
    }

    public List<ChatMessage> getMessages(String correspondent) {
        List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

        if (!useNativeAPI) {
            Cursor c = db.query(TABLE_NAME, null, "toContact LIKE \"" + correspondent + "\"", null, null, null, "id ASC");

            while (c.moveToNext()) {
                try {
                    String message, timestamp, url;
                    int id = c.getInt(c.getColumnIndex("id"));
                    int direction = c.getInt(c.getColumnIndex("direction"));
                    message = c.getString(c.getColumnIndex("message"));
                    timestamp = c.getString(c.getColumnIndex("timestamp"));
                    int status = c.getInt(c.getColumnIndex("status"));
                    byte[] rawImage = c.getBlob(c.getColumnIndex("image"));
                    int read = c.getInt(c.getColumnIndex("read"));
                    url = c.getString(c.getColumnIndex("url"));

                    ChatMessage chatMessage = new ChatMessage(id, message, rawImage, timestamp, direction == INCOMING, status, read == READ);
                    chatMessage.setUrl(url);
                    chatMessages.add(chatMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.close();
        } else {
            LinphoneChatRoom room = LinphoneManager.getLc().getOrCreateChatRoom(correspondent);
            LinphoneChatMessage[] history = room.getHistory();
            for (int i = 0; i < history.length; i++) {
                LinphoneChatMessage message = history[i];

                Bitmap bm = null;
                String url = message.getExternalBodyUrl();
                if (url != null && !url.startsWith("http")) {
                    bm = BitmapFactory.decodeFile(url);
                }
                ChatMessage chatMessage = new ChatMessage(i + 1, message.getText(), bm,
                        String.valueOf(message.getTime()), !message.isOutgoing(),
                        message.getStatus().toInt(), message.isRead());
                chatMessage.setUrl(url);
                chatMessages.add(chatMessage);
            }
        }

        return chatMessages;
    }

    public String getTextMessageForId(LinphoneChatRoom chatroom, int id) {
        String message = null;

        if (useNativeAPI) {
            LinphoneChatMessage[] history = chatroom.getHistory();
            for (LinphoneChatMessage msg : history) {
                if (msg.getStorageId() == id) {
                    message = msg.getText();
                    break;
                }
            }
        } else {
            Cursor c = db.query(TABLE_NAME, null, "id LIKE " + id, null, null, null, null);

            if (c.moveToFirst()) {
                try {
                    message = c.getString(c.getColumnIndex("message"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            c.close();
        }

        return message;
    }

    public LinphoneChatMessage getMessage(LinphoneChatRoom chatroom, int id) {
        if (useNativeAPI) {
            LinphoneChatMessage[] history = chatroom.getHistory();
            for (LinphoneChatMessage msg : history) {
                if (msg.getStorageId() == id) {
                    return msg;
                }
            }
        }
        return null;
    }

    public void removeDiscussion(String correspondent) {
        if (useNativeAPI) {
            LinphoneChatRoom chatroom = LinphoneManager.getLc().getOrCreateChatRoom(correspondent);
            chatroom.deleteHistory();
        } else {
            db.delete(TABLE_NAME, "toContact LIKE \"" + correspondent + "\"", null);
        }
    }

    public static List<LinphoneAddress> getAddresses() {
        List<LinphoneAddress> addressList = new ArrayList<>();
        LinphoneProxyConfig[] proxyConfigs = LinphoneManager.getLc().getProxyConfigList();
        for (LinphoneProxyConfig proxyConfig : proxyConfigs) {
            LinphoneAddress address;
            try {
                address = LinphoneCoreFactory.instance().createLinphoneAddress(proxyConfig.getIdentity());
                addressList.add(address);
            } catch (LinphoneCoreException e) {
            }
        }
        List<TouchDBModel> myTouchs = LinphoneActivity.instance().getMyTouchs();
        if (myTouchs != null && !myTouchs.isEmpty()) {
            for (TouchDBModel touch : myTouchs) {
                LinphoneAddress address;
                address = LinphoneCoreFactory.instance().createLinphoneAddress(touch.getPhone(), LinphoneManager.getLc().getDefaultProxyConfig().getDomain(), null);
                addressList.add(address);
            }
        }
        VoiceNumber vn = LinphoneActivity.instance().getVoiceNumber();
        if (vn != null && vn.getPhone() != null) {
            LinphoneAddress address;
            address = LinphoneCoreFactory.instance().createLinphoneAddress(vn.getPhone(), LinphoneManager.getLc().getDefaultProxyConfig().getDomain(), null);
            addressList.add(address);
        }
        return addressList;
    }

    public static boolean isMyChatRoom(LinphoneChatRoom room, List<LinphoneAddress> addressList) {
        String sipuri = room.getPeerAddress().asString();
        String userid = CallUtil.getSipUriParam(sipuri, "userid");
        String loginUserId = LinphoneActivity.instance().getLoginInfo().getUserId();
        Log.e("验证是否是自己的会话--->", "from->" + sipuri + "||->" + userid + "||登录用户--》" + loginUserId);
        Log.e("验证是否是自己的会话--->", "to->" + sipuri + "||->" + userid);
        if (userid != null && userid.equals(loginUserId)) {
            return true;
        }

        String username = room.getPeerAddress().getUserName();
        Log.e("验证是否是自己的会话--->", "username->" + username + "||->" + userid + "||登录用户--》" + loginUserId);
        if (userid == null && !username.equals(loginUserId)) {
            return true;
        }
        boolean isFromChat = false;
        if (username.startsWith("T")) {
            username = username.substring(1);
        } else {
            for (LinphoneAddress address : addressList) {
                String myusername = address.getUserName();
                if (myusername.equals(username)) {
                    return true;
                }
            }
            return false;
        }
        String realNumber = CallUtil.getRealToNumber(sipuri);
        Log.e("验证是否是自己的会话--->", "realNumber->" + realNumber + "||->" + userid + "||登录用户--》" + loginUserId);
        for (LinphoneAddress address : addressList) {
            String myusername = address.getUserName();
            Log.e("验证是否是自己的会话--->", "myusername->" + myusername + "||->" + userid + "||登录用户--》" + loginUserId);
            if (myusername.equals(username) || myusername.equals(realNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 消息会话验证
     * 单个用户
     *
     * @param room
     * @param addressList
     * @param contact     对方会话者
     * @return
     */
    public static boolean isMyChatRoom(LinphoneChatRoom room, List<LinphoneAddress> addressList, String contact) {
        String sipuri = room.getPeerAddress().asString();
        String userid = CallUtil.getSipUriParam(sipuri, "userid");
        if (userid != null && !userid.equals(LinphoneActivity.instance().getLoginInfo().getUserId())) {
            return false;
        }
        String username = room.getPeerAddress().getUserName();
        if (username.startsWith("T")) {
            username = username.substring(1);
        } else {
            if (username.equals(contact)) {
                return true;
            }
        }
        String realNumber = CallUtil.getRealToNumber(sipuri);
        for (LinphoneAddress address : addressList) {
            String myusername = address.getUserName();
            if (myusername.equals(username) || myusername.equals(realNumber)
                    || (userid != null && userid.equals(LinphoneActivity.instance().getLoginInfo().getUserId()))) {
                if (contact.equals(username) || contact.equals(realNumber)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isToSelf(LinphoneChatRoom room, List<LinphoneAddress> addressList) {
        String realNumber = CallUtil.getRealToNumber(room.getPeerAddress().asString());
        for (LinphoneAddress address : addressList) {
            String myusername = address.getUserName();
            if (myusername.equals(realNumber)) {
                return true;
            }
        }
        return false;
    }

    public static List<LinphoneChatRoom> initChatRooms(LinphoneChatRoom[] chats, List<LinphoneAddress> addressList) {
        List<LinphoneChatRoom> rooms = new ArrayList<>();
        for (LinphoneChatRoom chatroom : chats) {
            if (isMyChatRoom(chatroom, addressList) && chatroom.getHistory(1).length > 0) {
                Log.i("mychatroom:" + chatroom.getPeerAddress().asString());
                rooms.add(chatroom);
            }
        }
        return rooms;
    }

    public ArrayList<String> getChatList() {
        ArrayList<String> chatList = new ArrayList<>();
        if (useNativeAPI) {
            LinphoneChatRoom[] chats = LinphoneManager.getLc().getChatRooms();
            List<LinphoneAddress> addressList = getAddresses();
            List<LinphoneChatRoom> rooms = initChatRooms(chats, addressList);
            if (rooms.size() > 1) {
                Collections.sort(rooms, new Comparator<LinphoneChatRoom>() {
                    @Override
                    public int compare(LinphoneChatRoom a, LinphoneChatRoom b) {
                        LinphoneChatMessage[] messagesA = a.getHistory(1);
                        LinphoneChatMessage[] messagesB = b.getHistory(1);
                        long atime = messagesA[0].getTime();
                        long btime = messagesB[0].getTime();
                        if (atime > btime)
                            return -1;
                        else if (btime > atime)
                            return 1;
                        else
                            return 0;
                    }
                });
            }

            for (LinphoneChatRoom chatroom : rooms) {
                if (isToSelf(chatroom, addressList)) {
                    Log.i(chatroom.getPeerAddress().asString());
                    String username = chatroom.getPeerAddress().getUserName();
                    if (username.startsWith("T")) {
                        username = username.substring(1);
                    }
                    if (!chatList.contains(username)) {
                        chatList.add(username);
                    }
                } else {
                    String username = CallUtil.getRealToNumber(chatroom.getPeerAddress().asString());
                    if (!chatList.contains(username)) {
                        chatList.add(username);
                    }
                }
            }
        } else {
            Cursor c = db.query(TABLE_NAME, null, null, null, "toContact", null, "id DESC");
            while (c != null && c.moveToNext()) {
                try {
                    String remoteContact = c.getString(c.getColumnIndex("toContact"));
                    chatList.add(CallUtil.getRealToNumber(remoteContact));
                } catch (IllegalStateException ise) {
                }
            }
            c.close();
        }

        return chatList;
    }

    public void deleteMessage(LinphoneChatRoom chatroom, int id) {
        if (useNativeAPI) {
            LinphoneChatMessage[] history = chatroom.getHistory();
            for (LinphoneChatMessage message : history) {
                if (message.getStorageId() == id) {
                    chatroom.deleteMessage(message);
                    break;
                }
            }
        } else {
            db.delete(TABLE_NAME, "id LIKE " + id, null);
        }
    }

    public void markMessageAsRead(int id) {
        if (!useNativeAPI) {
            ContentValues values = new ContentValues();
            values.put("read", READ);
            db.update(TABLE_NAME, values, "id LIKE " + id, null);
        }
    }

    public void markConversationAsRead(LinphoneChatRoom chatroom) {
        if (useNativeAPI) {
            chatroom.markAsRead();
        }
    }

    public int getUnreadMessageCount() {
        int count;
        if (!useNativeAPI) {
            Cursor c = db.query(TABLE_NAME, null, "read LIKE " + NOT_READ, null, null, null, null);
            count = c.getCount();
            c.close();
        } else {
            count = 0;
            LinphoneChatRoom[] chats = LinphoneManager.getLc().getChatRooms();
            for (LinphoneChatRoom chatroom : chats) {
                count += chatroom.getUnreadMessagesCount();
            }
        }
        return count;
    }

    public int getUnreadMessageCount(String contact) {
        int count;
        if (!useNativeAPI) {
            Cursor c = db.query(TABLE_NAME, null, "toContact LIKE \"" + contact + "\" AND read LIKE " + NOT_READ, null, null, null, null);
            count = c.getCount();
            c.close();
        } else {
            LinphoneChatRoom chatroom = LinphoneManager.getLc().getOrCreateChatRoom(contact);
            count = chatroom.getUnreadMessagesCount();
        }
        return count;
    }

    public byte[] getRawImageFromMessage(int id) {
        if (useNativeAPI) {
            //Handled before this point
            return null;
        }

        String[] columns = {"image"};
        Cursor c = db.query(TABLE_NAME, columns, "id LIKE " + id + "", null, null, null, null);

        if (c.moveToFirst()) {
            byte[] rawImage = c.getBlob(c.getColumnIndex("image"));
            c.close();
            return (rawImage == null || rawImage.length == 0) ? null : rawImage;
        }

        c.close();
        return null;
    }

    class ChatHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 18;
        private static final String DATABASE_NAME = "linphone-android";

        ChatHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e("database-->", " chatStorage base onCreate");
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, fromContact TEXT NOT NULL, toContact TEXT NOT NULL, direction INTEGER, message TEXT, image BLOB, url TEXT, timestamp NUMERIC, read INTEGER, status INTEGER, userid TEXT);");
            db.execSQL("CREATE TABLE " + DRAFT_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, toContact TEXT NOT NULL, message TEXT, userid TEXT);");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e("database-->", " chatStorage base onUpgrade");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            db.execSQL("DROP TABLE IF EXISTS " + DRAFT_TABLE_NAME + ";");
            onCreate(db);
        }
    }
}
