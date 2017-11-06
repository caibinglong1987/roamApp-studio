package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

/**
 * Created by asus on 2016/8/21.
 */
public class SendCardsActivity extends BaseActivity {


    private Button mBtnSendFreeCard;
    private Button mBtnToBack;


    @Override

    public int getLayoutId() {
        return R.layout.acticity_send_card;
    }

    @Override
    protected Bundle getBundle() {
        return super.getBundle();
    }

    @Override
    public void setListener() {

        mBtnSendFreeCard.setOnClickListener(this);
        mBtnToBack.setOnClickListener(this);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mBtnSendFreeCard = (Button) findViewById(R.id.btn_free_card);
        mBtnToBack = (Button) findViewById(R.id.id_toback);
    }

    @Override
    public void onClick(View v) {
        if(v==mBtnSendFreeCard){
            Intent intent=new Intent();

            setResult(RESULT_OK, intent);

         SendCardsActivity.this.finish();

        }else if(v==mBtnToBack){
            finish();
        }


    }




}
