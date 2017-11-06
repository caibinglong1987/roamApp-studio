package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.RedeemCode;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.Evoucher;
import com.roamtech.telephony.roamapp.bean.RedeemCodeRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenblue23 on 2016/10/24.
 */

public class RedeemCodeActivity extends HeaderBaseActivity {
    private EditText etRedeemCode;
    private Button btnExchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_code);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        headerLayout.showTitle(getString(R.string.redeem_code));
        headerLayout.showLeftBackButton();
        etRedeemCode = (EditText) findViewById(R.id.et_redeem_code);
        btnExchange = (Button) findViewById(R.id.btn_exchange);
    }

    @Override
    public void setListener() {
        super.setListener();
        btnExchange.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_exchange) {
            String sn = etRedeemCode.getText().toString();
            if (StringUtil.isTrimBlank(sn)) {
                showToast(R.string.redeem_code_null);
            } else {
                exchange(sn);
            }
        }
    }

    private void exchange(String sn) {
        JSONObject loginInfo = getAuthJSONObject();
        try {
            loginInfo.put("evoucher_sn", sn);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new RedeemCode(this).exchange(loginInfo, hashCode(), new HttpBusinessCallback() {

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
                showDialog(getString(R.string.exchange_fail), null, false);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                if (response != null) {
                    UCResponse<RedeemCodeRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<RedeemCodeRDO>>() {
                    });
                    if (result.getErrorNo() == 0) {
                        Evoucher evoucher = result.getAttributes().getEvoucher();
                        showDialog(getString(R.string.exchange_success), evoucher.getDescription(), evoucher.isShowdetail());
                    } else {
                        showDialog(getString(R.string.exchange_fail), result.getErrorInfo(), false);
                    }
                }
            }
        });
    }

    private void showDialog(final String title, final String message, final boolean showDetail) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TipDialog dialog = new TipDialog(RedeemCodeActivity.this, title, message);
                if (showDetail) {
                    dialog.setLeftButton(getString(R.string.know), null);
                    dialog.setRightButton(getString(R.string.show_detail), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            toActivity(CouponActivity.class, null);
                        }
                    });
                } else {
                    dialog.setRightButton(getString(R.string.button_ok), null);
                }
                dialog.show();
            }
        });
    }
}
