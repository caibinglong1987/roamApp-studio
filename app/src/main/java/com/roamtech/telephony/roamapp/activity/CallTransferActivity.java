package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.util.ToastUtils;

import static com.roamtech.telephony.roamapp.helper.SimHelper.MOBILE;
import static com.roamtech.telephony.roamapp.helper.SimHelper.TELECOM;
import static com.roamtech.telephony.roamapp.helper.SimHelper.UNICOM;
import static com.roamtech.telephony.roamapp.helper.SimHelper.getProvider;
import static com.roamtech.telephony.roamapp.helper.SimHelper.getSimState;


public class CallTransferActivity extends BaseActivity {
    private Button btnOpenTransfer;
    private Button btnCloseTransfer;
    private static final String JING = Uri.encode("#");
    private String roamPhone;

    @Override
    public int getLayoutId() {
        return R.layout.activity_call_transfer;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        btnOpenTransfer = (Button) findViewById(R.id.btn_open);
        btnCloseTransfer = (Button) findViewById(R.id.btn_closed);
        roamPhone = getRoamPhone();
        if (roamPhone == null || roamPhone.equals("0")) {
            showToast(getString(R.string.you_no_private_number), true);
            finish();
        }
    }

    /**
     * 检测时候有手机号码
     *
     * @return
     */
    public boolean checkSim() {
        int simState = getSimState(this);
        if (simState != TelephonyManager.SIM_STATE_READY) {
            //new TipDialog(this, getString(R.string.prompt), getString(R.string.no_sim_card)).show();
            ToastUtils.showToast(this, getString(R.string.no_sim_card));
            return false;
        }
        return true;
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        btnCloseTransfer.setOnClickListener(this);
        btnOpenTransfer.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        btnOpenTransfer.invalidate();
        if (v == btnOpenTransfer) {
            if (!checkSim()) {
                return;
            }
            // 始终进行呼叫转移
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            int provider = getProvider(this);
            String data = null;
            if (provider == TELECOM) {
                data = "tel:*72" + roamPhone;
            } else if (provider == MOBILE || provider == UNICOM) {
                data = "tel:**21*" + roamPhone + JING;
            } else {
                showToast(getString(R.string.not_supported_sim));
                return;
            }
            intent.setData(Uri
                    .parse(data));
            startActivity(intent);
        } else if (v == btnCloseTransfer) {
            if (!checkSim()) {
                return;
            }
            // 取消进行呼叫转移
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            int provider = getProvider(this);
            String data = null;
            if (provider == TELECOM) {
                data = "tel:*720";
            } else if (provider == MOBILE || provider == UNICOM) {
                data = "tel:" + JING + JING + "21" + JING;
            } else {
                showToast(getString(R.string.not_supported_sim));
                return;
            }
            intent.setData(Uri
                    .parse(data));
            startActivity(intent);
        }
    }
}
