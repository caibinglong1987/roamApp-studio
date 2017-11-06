package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.bean.Bell;
import com.roamtech.telephony.roamapp.bean.BellRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by roam-caochen on 2017/2/8.
 */

public class GetBell extends HttpFunction {

    public GetBell(Context context) {
        super(context);
    }

    public void getBell() {
        JSONObject json = new JSONObject();
        String userId = SPreferencesTool.getInstance().getStringValue(mContext, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId);
        String sessionId = SPreferencesTool.getInstance().getStringValue(mContext, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_sessionId);
        try {
            json.put("userid", userId);
            json.put("sessionid", sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(Constant.BELL_GET, json, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                UCResponse<BellRDO> ucResponse = JsonUtil.fromJson(response, new TypeToken<UCResponse<BellRDO>>() {
                });
                if (ucResponse != null && ucResponse.getErrorNo() == 0) {
                    Bell bell = ucResponse.getAttributes().getBell();
                    if (bell != null) {
                        if (RoamApplication.bell == null || !RoamApplication.bell.equals(bell)) {
//                    if (true) {
                            RoamApplication.bell = bell;
                            SPreferencesTool.getInstance().saveBell(mContext, bell);
                            try {
                                URL url = new URL(Constant.AUDIO_URL + bell.getUrl());
                                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                InputStream in = con.getInputStream();
                                File fileOut = new File(mContext.getFilesDir().getAbsolutePath() + "/ringback.wav");
                                FileOutputStream out = new FileOutputStream(fileOut);
                                byte[] bytes = new byte[1024];
                                int c;
                                while ((c = in.read(bytes)) > 0) {
                                    out.write(bytes, 0, c);
                                }
                                in.close();
                                out.close();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }
}
