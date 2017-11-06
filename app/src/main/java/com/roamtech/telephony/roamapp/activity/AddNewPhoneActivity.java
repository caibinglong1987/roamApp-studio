package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddNewPhoneActivity extends BaseActivity {
    private EditText etUserName;
    private EditText etDentifyingCode;
    private EditText etPassword;
    private Button btnRegister;
    private TextView tvSendDentifyingCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_newphone);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        etUserName = (EditText) findViewById(R.id.tv_username);
        etDentifyingCode = (EditText) findViewById(R.id.id_identifying_code);
        etPassword = (EditText) findViewById(R.id.id_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        tvSendDentifyingCode = (TextView) findViewById(R.id.id_send_identifying_code);
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        btnRegister.setOnClickListener(this);
        tvSendDentifyingCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == btnRegister) {

        } else if (v == tvSendDentifyingCode) {

        }
    }
}
