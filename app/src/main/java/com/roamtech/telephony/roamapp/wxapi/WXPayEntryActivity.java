package com.roamtech.telephony.roamapp.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	api = WXAPIFactory.createWXAPI(this, WXInterface.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			//String result;
			Intent intent = new Intent(WXInterface.WEIXIN_PAY_RESULT_ACTION);
			switch (resp.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					//result = getString(R.string.pay_success);
//					EventBus.getDefault().post(new EventPayResult(true));
					intent.putExtra("wxPayResult", MsgType.WEI_XIN_PAY_SUCCESS);
					sendBroadcast(intent);
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					//result = getString(R.string.pay_cancel);
					intent.putExtra("wxPayResult", MsgType.WEI_XIN_PAY_CANCEL);
					sendBroadcast(intent);
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED:
					//result = getString(R.string.pay_deny);
//					break;
				case BaseResp.ErrCode.ERR_COMM:
					//result = getString(R.string.pay_error);
//					break;
				default:
					//result = getString(R.string.pay_unknown);
//					EventBus.getDefault().post(new EventPayResult(false));
					intent.putExtra("wxPayResult", MsgType.WEI_XIN_PAY_ERROR);
					sendBroadcast(intent);
					break;
			}
			finish();
		}
	}
}