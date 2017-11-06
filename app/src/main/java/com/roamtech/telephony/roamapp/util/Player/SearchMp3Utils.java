package com.roamtech.telephony.roamapp.util.Player;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Created by caibinglong on 17/2/6.
 * 扫描本地文件，找到mp3文件
 */
public class SearchMp3Utils {
    public static final int SEARCH_MUSIC_SUCCESS = 0;// 搜索成功标记
    public static final int SEARCH_MUSIC_ERROR = 1;// 搜索成功标记
    private Context context;
    private Handler handler;

    public SearchMp3Utils(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void SearchMp3() {
        //检测外部是否有存储设备
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            new Thread(new Runnable() {
                String[] ext = {".mp3"};
                File file = Environment.getExternalStorageDirectory();

                @Override
                public void run() {
                    search(file, ext);//另一种扫描歌曲方式文件扫描
                    handler.sendEmptyMessage(SEARCH_MUSIC_SUCCESS);
                }
            }).start();
        } else {
            Toast.makeText(context, "请插入外部存储设备..", Toast.LENGTH_LONG).show();
        }
    }

    //搜索音乐文件
    private void search(File file, String[] ext) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] listFile = file.listFiles();
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        search(listFile[i], ext);
                    }
                }
            } else {
                String filename = file.getAbsolutePath();
                for (int i = 0; i < ext.length; i++) {
                    if (filename.endsWith(ext[i])) {
                        //DebugLogs.d("----文件名称" + filename);
                        break;
                    }
                }
            }
        }
    }


    /**
     * 音乐搜索，
     */
    public void SearchMusic(final int number) {
        final ProgressDialog pd = ProgressDialog.show(context, "", "正在搜索音乐文件...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Audio> musicList = MediaUtils.getAudioList(context, number);
                if (musicList == null) {
                    if (handler != null) {
                        handler.sendEmptyMessage(SEARCH_MUSIC_ERROR);
                    }
                } else {
                    if (handler != null) {
                        handler.obtainMessage(SEARCH_MUSIC_SUCCESS, musicList).sendToTarget();
                    }
                }
                pd.dismiss();
            }
        }).start();
    }
}
