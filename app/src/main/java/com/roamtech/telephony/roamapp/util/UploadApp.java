package com.roamtech.telephony.roamapp.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


import com.roamtech.telephony.roamapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者: Created by: long on Date: 16/9/8.
 */

public class UploadApp {
    private ProgressDialog mpDialog;// 创建精度条
    private int fileSize;// 设置文件大小
    private int downLoadFileSize;// 当前已下载的文件的大小
    private Context mContext;
    // APK的安装路径
    private String apk_name = "RoamChat.apk";
    private long loadApk = System.currentTimeMillis();
    private String downloadDir = "";
    private String savePath = downloadDir + File.separator + loadApk + File.separator; //保存下载文件的路径
    private String saveFileName = savePath + apk_name;//保存下载文件的名称

    public UploadApp(String downloadDir) {
        this.downloadDir = downloadDir;
        this.savePath = downloadDir + File.separator + loadApk + File.separator;
        this.saveFileName = savePath + apk_name;
    }

    /**
     * 本地弹出升级提示
     *
     * @param context 上下文
     * @param str     升级内容
     * @param url     升级地址
     * @param force   是否强制升级 true为强制升级
     */
    public void showUpApk(final Context context, String str, final String url, boolean force) {
        this.mContext = context;
        //final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        final Dialog dialog = new Dialog(context,R.style.dialog_tipStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);//设置无法取消
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(R.layout.dialog_upgrade);
        TextView tops_title = (TextView) window.findViewById(R.id.tops_title);
        TextView tv_content = (TextView) window.findViewById(R.id.content);
        Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
        btn_cancel.setVisibility(View.VISIBLE);
        if (force) {//强制升级
            btn_cancel.setVisibility(View.GONE);
        }
        tops_title.setText(mContext.getString(R.string.updata_tops));
        //String message =  str;
        tv_content.setText(str);
        //OK
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPreferencesTool.getInstance().putValue(mContext, SPreferencesTool.LOGIN_INFO, SPreferencesTool.every_day_update, 0l);
                //进度条界面
                mpDialog = new ProgressDialog(mContext);
                mpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mpDialog.setMessage(mContext.getString(R.string.updata_progress));
                mpDialog.setIndeterminate(false);// 是进度条是否明确
                mpDialog.setCancelable(false);// 点击返回按钮的时候无法取消对话框
                mpDialog.setCanceledOnTouchOutside(false);// 点击对话框外部取消对话框显示
                mpDialog.setProgress(0);// 设置初始进度条为0q
                mpDialog.incrementProgressBy(1);// 设置进度条增涨。
                mpDialog.show();
                new Thread() {
                    public void run() {
                        String apkUrl = url;//下载地址
                        URL url;
                        try {
                            url = new URL(apkUrl);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            InputStream in = con.getInputStream();
                            fileSize = con.getContentLength();// 获取下载文件的长度
                            File file = new File(savePath);
                            if (!file.exists()) {
                                file.mkdirs();
                                File fileOut = new File(saveFileName);// 下载文件的存放地址
                                FileOutputStream out = new FileOutputStream(fileOut);
                                byte[] bytes = new byte[1024];
                                downLoadFileSize = 0;
                                sendMsg(0);// sendMeg为0的时候显示下载完成
                                int c;
                                while ((c = in.read(bytes)) > 0) {
                                    out.write(bytes, 0, c);
                                    downLoadFileSize += c;
                                    sendMsg(1);
                                }
                                in.close();
                                out.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendMsg(2);
                    }
                }.start();
                dialog.dismiss();
            }
        });
        //Cancel
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPreferencesTool.getInstance().putValue(context, SPreferencesTool.UpgradeCheck.PROFILE_NAME, "cancel", true);
                dialog.dismiss();
            }
        });
    }

    // 安装apk方法
    private void installApk(String filename) {
        File file = new File(filename);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(file), type);
        mContext.startActivity(intent);
        if (mpDialog != null) {
            mpDialog.cancel();
        }
        File file2 = new File(downloadDir);
        getAllFiles(file2);
    }

    // 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并输出来
    private void getAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllFiles(f);
                } else {
                    String filename = String.valueOf(f);
                    String file = String.valueOf(f).split("/" + apk_name)[0];
                    File file1 = new File(file);
                    if (!saveFileName.equals(filename)) {
                        f.delete();
                        file1.delete();
                    } else {
                        System.out.println(f);
                    }

                }
            }
        }
    }

    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        mpDialog.setMax(100);
                        break;
                    case 1:
                        int result = downLoadFileSize * 100 / fileSize;
                        mpDialog.setProgress(result);
                        break;
                    case 2:
                        mpDialog.setMessage(R.string.updata_file_success + "");
                        installApk(saveFileName);
                        break;
                    case -1:
                        String error = msg.getData().getString("error");
                        mpDialog.setMessage(error);
                        break;
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };
}

