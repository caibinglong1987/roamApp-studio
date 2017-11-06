package com.roamtech.telephony.roamapp.util.Player;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujian on 16/2/20.
 * 一个读取媒体音频的方法
 */
public class MediaUtils {
    private static int defaultCount = 5;
    public static final String[] AUDIO_KEYS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TITLE_KEY,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST_KEY,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM_KEY,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_PODCAST,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATA
    };

    /**
     * 搜索mp3 数值
     *
     * @param number int
     */
    public static void setNumber(int number) {
        defaultCount = number;
    }

    public static List<Audio> getAudioList(Context context, int number) {
        if (number != 0) {
            defaultCount = number;
        }
        List<Audio> audioList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                AUDIO_KEYS,
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }
        int cursorIndex = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (cursorIndex >= defaultCount) {
                break;
            }
            Bundle bundle = new Bundle();
            for (int i = 0; i < AUDIO_KEYS.length; i++) {
                final String key = AUDIO_KEYS[i];
                final int columnIndex = cursor.getColumnIndex(key);
                final int type = cursor.getType(columnIndex);
                switch (type) {
                    case Cursor.FIELD_TYPE_BLOB:
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        float floatValue = cursor.getFloat(columnIndex);
                        bundle.putFloat(key, floatValue);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        int intValue = cursor.getInt(columnIndex);
                        bundle.putInt(key, intValue);
                        break;
                    case Cursor.FIELD_TYPE_NULL:
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        String strValue = cursor.getString(columnIndex);
                        bundle.putString(key, strValue);
                        break;
                }
            }
            Audio audio = new Audio(bundle);
            if (audio.isMusic() && audio.getPath().endsWith(".mp3")) {
                String displayName = audio.getDisplayName().substring(0, audio.getDisplayName().lastIndexOf(".mp3"));
                //displayName = displayName.substring(0, displayName.("_"));
                if (displayName.indexOf("_") > 0) {
                    displayName = displayName.substring(0, displayName.indexOf("_"));
                }
                String[] str = displayName.replace(" - ", "_").replace("-", "_").replace(" ", "_").split("_");

                if (str.length > 1) {
                    audio.setmDisplayName(str[1]);
                    audio.setmArtist(str[0]);
                    audio.setNameArtist(audio.getDisplayName() + " - " + audio.getArtist());
                } else {
                    audio.setmDisplayName(str[0]);
                    audio.setNameArtist(audio.getDisplayName());
                }

                audioList.add(audio);
            } else {
                continue;
            }
            cursorIndex++;
        }
        cursor.close();
        return audioList;
    }
}
