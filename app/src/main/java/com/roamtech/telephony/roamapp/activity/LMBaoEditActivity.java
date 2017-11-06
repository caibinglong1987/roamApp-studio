package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.Phone;
import com.roamtech.telephony.roamapp.bean.WiFi;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 络漫宝 编辑界面
 * 输入wifi 账号 密码
 * 设置 手机
 */
public class LMBaoEditActivity extends BaseActivity {
    private static final int REQUEST_CODE_PHONE = 1;
    private static final int REQUEST_CODE_WIFI = 2;
    private Button btnBack;
    private TextView tvEdit;
    private boolean isEdit = false;
    private TextView tvSelectwifi;
    private TextView tvSelectWifishow;

    private TextView tvWifiPassword;
    private EditText etWifiPassword;

    private TextView tvBindPhone;
    private TextView tvBindPhoneShow;

    private LinearLayout layoutUnBindLMBao;
    private TouchDBModel mTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTouch = (TouchDBModel) getIntent().getSerializableExtra("touch");
        setContentView(R.layout.activity_lmbao_edit);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        btnBack = (Button) findViewById(R.id.id_toback);
        tvEdit = (TextView) findViewById(R.id.id_edit);

        tvSelectwifi = (TextView) findViewById(R.id.tv_selectwifi);
        tvSelectWifishow = (TextView) findViewById(R.id.tv_selectwifiShow);
        tvWifiPassword = (TextView) findViewById(R.id.tv_wifipassword);
        etWifiPassword = (EditText) findViewById(R.id.et_wifipassword);
        tvBindPhone = (TextView) findViewById(R.id.tv_bindPhone);
        tvBindPhoneShow = (TextView) findViewById(R.id.tv_bindPhoneshow);
        layoutUnBindLMBao = (LinearLayout) findViewById(R.id.ll_unbindlmbao);
        tvSelectWifishow.setText(mTouch.getWifiSsid());
        etWifiPassword.setText(mTouch.getWifiPassword());
        tvBindPhoneShow.setText(mTouch.getPhone());
        /**初始化编辑状态***/
        setEditState(isEdit);
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvEdit.setOnClickListener(this);
        tvSelectwifi.setOnClickListener(this);
        tvBindPhone.setOnClickListener(this);
        layoutUnBindLMBao.setOnClickListener(this);
    }

    private void setEditState(boolean isEdit) {
        this.isEdit = isEdit;
        layoutUnBindLMBao.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);
        tvSelectwifi.setEnabled(isEdit);
        tvSelectWifishow.setEnabled(isEdit);
        etWifiPassword.setEnabled(isEdit);
        tvBindPhone.setEnabled(isEdit);
        tvBindPhoneShow.setEnabled(isEdit);
        tvEdit.setText(!isEdit ? R.string.edit : R.string.complete);
        btnBack.setVisibility(isEdit ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvEdit) {
            setEditState(!isEdit);
        } else if (v == tvSelectwifi) {
            Bundle bundle = new Bundle();
            bundle.putString("wifi", tvSelectWifishow.getText().toString());
            toActivityForResult(SelectWiFiActivity.class, REQUEST_CODE_WIFI, bundle);
        } else if (v == tvBindPhone) {
            Bundle bundle = new Bundle();
            bundle.putString("phone", tvBindPhoneShow.getText().toString());
            toActivityForResult(MinePhoneActivity.class, REQUEST_CODE_PHONE, bundle);
        } else if (v == layoutUnBindLMBao) {

        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == RESULT_OK) {
            if (arg0 == REQUEST_CODE_PHONE) {
                Phone phone = (Phone) arg2.getSerializableExtra("phone");
                tvBindPhoneShow.setText(phone.getPhoneNumber());
            } else if (arg0 == REQUEST_CODE_WIFI) {
                WiFi Wifi = (WiFi) arg2.getSerializableExtra("wifi");
                tvSelectWifishow.setText(Wifi.getSsid());
            }
        }
    }
}
