package com.roamtech.telephony.roamapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.will.common.tool.PackageTool;

/**
 * Created by caibinglong
 * on 2016/11/16.
 */

public class AudioPermissionUtils {
    /**
     * 判断是否有录音权限
     *
     * @param context context
     * @return true false
     */

    public static boolean isHasAudioRecordPermission(Context context) {
        //Android sdk版本号
        int sdkInt = Build.VERSION.SDK_INT;
        //6.0之前的权限检测只是检测到是否在清单文件中注册,6.0系统一下
        // //无论是关闭或者打开app的录音权限都能获取到权限，6.0以上则正常，后来发现是6.0以后google加强了权限管理
        if (sdkInt >= 23) {
            PackageManager pm = context.getPackageManager();
            int permission = pm.checkPermission("android.permission.RECORD_AUDIO", context.getPackageName());
            if (PackageManager.PERMISSION_GRANTED == permission) {
                return true;
            } else {
                return false;
            }
        } else {
            // 音频获取源
            int audioSource = MediaRecorder.AudioSource.MIC;
            // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
            int sampleRateInHz = 44100;
            // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
            int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
            // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
            // 缓冲区字节大小
            int bufferSizeInBytes = 0;
            bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
            AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
            //开始录制音频
            try {
                // 防止某些手机崩溃
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            /**
             * 根据开始录音判断是否有录音权限
             */
            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                return true;
            }
            try {
                // 防止某些手机崩溃
                audioRecord.stop();
                // 彻底释放资源
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            audioRecord.release();
            audioRecord = null;
            return false;
        }
    }

    public static void showDialog(final Context context) {
        //如果没有权限，跳转到应用的权限设置页面
        TipDialog tipDialog = new TipDialog(context, context.getString(R.string.prompt), "录音功能好像有问题哦~您可以去设置里检查是否开启录音权限");
        tipDialog.setRightButton(context.getString(R.string.str_alert_now_set), new TipDialog.OnClickListener() {
            @Override
            public void onClick(int which) {
                Uri packageURI = Uri.parse("package:" + context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                context.startActivity(intent);
            }
        });
    }

    public static void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }
}
