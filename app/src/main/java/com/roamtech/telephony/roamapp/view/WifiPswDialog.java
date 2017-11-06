package com.roamtech.telephony.roamapp.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by long
 * on 2016/10/8 09:48
 */

public class WifiPswDialog extends Dialog {
    private Button cancelButton;
    private Button okButton;
    private EditText pswEdit;
    private OnCustomDialogListener customDialogListener;

    public WifiPswDialog(Context context) {
        super(context);
    }

    public WifiPswDialog(Context context, int theme) {
        super(context, theme);
    }

    public WifiPswDialog(Context context, OnCustomDialogListener customListener) {
        super(context);
        customDialogListener = customListener;
    }

    protected WifiPswDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** * 定义dialog的回调事件 */
    public interface OnCustomDialogListener {
        void back(String str);
    }

}
