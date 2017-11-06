package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.SingleDataCardRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.enums.LoadingState;
import com.roamtech.telephony.roamapp.event.EventAddCard;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.will.common.tool.PackageTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.roamtech.telephony.roamapp.enums.LoadingState.SESSION_TIME_OUT;


public class AddGlobalNetCardActivity extends BaseActivity {
    private static final int GO_SCAN_CODE = 3;
    private ImageView ivCapture;
    private Button btnAddcard;
    private EditText etIccid;
    private ImageView ivFrame;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_globalcard;
    }


    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        ivCapture = (ImageView) findViewById(R.id.iv_capture);
        ivCapture.setOnClickListener(this);
        btnAddcard = (Button) findViewById(R.id.btn_addcard);
        btnAddcard.setOnClickListener(this);
        etIccid = (EditText) findViewById(R.id.et_iccid);
        ivFrame = (ImageView) findViewById(R.id.iv_frame);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(1500);
        animation.setRepeatCount(Animation.INFINITE);
        ivFrame.startAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == ivCapture) {
//            toActivity(CaptureActivity.class,null);
            Bundle bundle = new Bundle();
            bundle.putBoolean("manualInput", false);
            toActivityForResult(CaptureActivity.class, GO_SCAN_CODE, bundle);
        } else if (v == btnAddcard) {
            String iccid = etIccid.getText().toString();
            if (StringUtil.isTrimBlank(iccid)) {
                showToast(getString(R.string.not_null_card_number));
                return;
            }
            bindCard(iccid);
        }
    }

    private void bindCard(String iccid) {
        showDialog(getString(R.string.now_bind_card));
        JSONObject json = getAuthJSONObject();
        try {
            json.put("datacardid", iccid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtil.postJsonRequest(Constant.DATA_CARD_BIND, json, hashCode(), new OKCallback<SingleDataCardRDO>(new TypeToken<UCResponse<SingleDataCardRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, UCResponse<SingleDataCardRDO> ucResponse) {
                dismissDialog();
                if (isSucccess()) {
                    showToast(getString(R.string.bind_card_success));
                    EventBus.getDefault().post(new EventAddCard("add new card"));
                    finish();
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                } else {
                    if (ucResponse != null) {
                        showToast(ucResponse.getErrorInfo());
                    } else {
                        showToast(getString(R.string.bind_card_error));
                    }
                }
            }

            @Override
            public void onFailure(IOException e) {
                dismissDialog();
                showToast(LoadingState.getErrorState(e).getText());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_SCAN_CODE && resultCode == RESULT_OK) {
            finish();
        }
    }
}
