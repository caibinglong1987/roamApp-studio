package com.roamtech.telephony.roamapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.CallDetailRecordDBModel;
import com.will.common.tool.time.DateTimeTool;

import org.linphone.mediastream.Log;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by caibinglong
 * on 2017/2/6.
 */

public class PlayerDialog extends Dialog {
    public static final int IDLE = 0;
    public static final int PAUSE = 1;
    public static final int START = 2;
    public static final int STOP = 3;
    public static final int RESUME = 4;
    public static final int NEXT = 5;
    public static final int PREVIOUS = 6;
    public static int PLAY_STATE = 0;
    public static MediaPlayer mediaPlayer;

    public PlayerDialog(Activity context, int theme) {
        super(context, theme);
    }

    public static class Builder implements View.OnClickListener {
        private SeekBar mSeekBar;
        private Activity context;
        public PlayerDialog playerDialog;
        private ImageView iv_play, iv_directory_see, iv_delete, iv_close;
        private TextView tv_play_total_time, tv_play_time, tv_dateTime;
        private String path;
        private int fileTime = 0;
        private Timer mTimer;
        private TimerTask mTimerTask;
        private boolean isChanging = false;
        private iRefreshCall callback;
        private CallDetailRecordDBModel model;

        public Builder(Activity context, String path, iRefreshCall callback, CallDetailRecordDBModel model) {
            this.context = context;
            this.path = path;
            this.callback = callback;
            this.model = model;
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileTime = mediaPlayer.getDuration();
            mediaPlayer.stop();
            PLAY_STATE = IDLE;
        }

        public PlayerDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            playerDialog = new PlayerDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_player, null);
            Window win = playerDialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            playerDialog.setContentView(layout);
            mSeekBar = (SeekBar) layout.findViewById(R.id.seekBar);
            iv_play = (ImageView) layout.findViewById(R.id.iv_play);
            tv_play_total_time = (TextView) layout.findViewById(R.id.tv_play_total_time);
            tv_dateTime = (TextView) layout.findViewById(R.id.tv_dateTime);
            iv_directory_see = (ImageView) layout.findViewById(R.id.iv_directory_see);
            iv_delete = (ImageView) layout.findViewById(R.id.iv_delete);
            iv_close = (ImageView) layout.findViewById(R.id.iv_close);
            tv_play_time = (TextView) layout.findViewById(R.id.tv_play_time);
            initListener();
            initData();
            return playerDialog;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_play:
                    if (PLAY_STATE == START) {
                        iv_play.setImageResource(R.drawable.recording_stop);
                        PLAY_STATE = PAUSE;
                        mediaPlayer.pause();
                    } else if (PLAY_STATE == PAUSE) {
                        PLAY_STATE = START;
                        iv_play.setImageResource(R.drawable.recording_play);
                        mediaPlayer.start();
                    } else if (PLAY_STATE == IDLE) {
                        iv_play.setImageResource(R.drawable.recording_play);
                        PLAY_STATE = START;
                        startPlay(path, completionListener);
                    }
                    break;
                case R.id.iv_directory_see:
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setDataAndType(Uri.fromFile(new File(RoamApplication.FILEPATH_RECORD)), "file/*");
//                    try {
//                        context.startActivity(intent);
//                        //context.startActivity(Intent.createChooser(intent,"选择浏览工具"));
//                    } catch (ActivityNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    break;
                case R.id.iv_delete:
                    final TipDialog dialog = new TipDialog(context, context.getString(R.string.confirm_delete), "");
                    dialog.setRightButton(context.getString(R.string.button_ok), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            File file = new File(path);
                            if (file.isFile() && file.exists()) {
                                file.delete();
                                dialog.dismiss();
                                if (callback != null) {
                                    callback.refreshCall();
                                }
                            }
                        }
                    });

                    dialog.setLeftButton(context.getString(R.string.button_cancel), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                    break;
                case R.id.iv_close:
                    closeDialog();
                    break;
            }
        }

        private void initListener() {
            iv_play.setOnClickListener(this);
            iv_directory_see.setOnClickListener(this);
            iv_delete.setOnClickListener(this);
            iv_close.setOnClickListener(this);
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    Log.w("播放进度" + progress + "||fromUser" + fromUser);
                    //mSeekBar.setProgress(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isChanging = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    isChanging = false;
                }
            });

            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {

                }
            });
        }

        private void initData() {
            mSeekBar.setProgress(0);
            mSeekBar.setMax(fileTime);
            tv_play_total_time.setText(DateTimeTool.DateFormat(fileTime, "mm:ss"));
            tv_dateTime.setText(DateTimeTool.DateFormat(model.getTimestamp(), "yy/MM/dd HH:mm:ss"));
        }

        public void closeDialog() {
            playerDialog.dismiss();
            if (mTimer != null) {
                mTimer.cancel();
                mTimerTask.cancel();
                mTimer = null;
                mTimerTask = null;
            }
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                PLAY_STATE = IDLE;
                iv_play.setImageResource(R.drawable.recording_stop);
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimerTask.cancel();
                    mTimer = null;
                    mTimerTask = null;
                }
            }
        };

        //开始播放
        private void startPlay(String path, MediaPlayer.OnCompletionListener listener) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(listener);
                mediaPlayer.start();
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (isChanging) {
                            return;
                        }
                        final int time = mediaPlayer.getCurrentPosition();
                        Log.w("播放进度" + time);
                        mSeekBar.setProgress(time);
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_play_time.setText(DateTimeTool.DateFormat(time, "mm:ss"));
                            }
                        });

                    }
                };
                mTimer.schedule(mTimerTask, 0, 10);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void stopPlay() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                PLAY_STATE = IDLE;
                iv_play.setImageResource(R.drawable.recording_stop);
            }
        }
    }

    public interface iRefreshCall {
        void refreshCall();
    }
}
