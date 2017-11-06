package com.roamtech.telephony.roamapp.util.Player;

import android.media.AudioManager;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by caibinglong on 17/2/6.
 * 监听声音大小控制
 */
public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
    private AudioManager audioManager;
    private TextView textView;

    SeekBarListener(AudioManager Am, TextView tv) {
        this.audioManager = Am;
        this.textView = tv;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        if (fromUser) {
            int SeekPosition = seekBar.getProgress();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, SeekPosition, 0);
            new Runnable() {
                @Override
                public void run() {
                    if (textView != null) {
                        int MaxSound = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        float num = ((float) progress / MaxSound) * 100;
                        DecimalFormat df = new DecimalFormat("0");//格式化小数
                        String s = df.format(num) + "%";//返回的是String类型
                        textView.setText(s);
                    }
                }
            }.run();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
