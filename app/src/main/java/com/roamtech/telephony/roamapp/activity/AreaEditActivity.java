package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class AreaEditActivity extends BaseActivity {
    private EditText etArea;
    private TextView tvCancel;
    private TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_edit);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        etArea = (EditText) findViewById(R.id.et_area);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvSave = (TextView) findViewById(R.id.tv_save);
        etArea.setText(getIntent().getStringExtra("address"));
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvCancel) {
            finish();
        } else if (v == tvSave) {
            Intent i = new Intent();
            i.putExtra("address", etArea.getText().toString());
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
