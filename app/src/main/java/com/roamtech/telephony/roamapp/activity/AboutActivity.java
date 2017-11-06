package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.AppNewVersionRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.will.common.tool.PackageTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class AboutActivity extends HeaderBaseActivity {
    private TextView tvFeedback;
    private TextView tvNewVersion;
    private TextView tvUserAgree;
    private AppNewVersionRDO appNewVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        setListener();
    }

    private void initView() {
        tvFeedback = (TextView) findViewById(R.id.tv_feedback);
        tvNewVersion = (TextView) findViewById(R.id.tv_newversion);
        tvUserAgree = (TextView) findViewById(R.id.tv_userAgree);
        headerLayout.showTitle(getString(R.string.about));
        headerLayout.showLeftBackButton();
        tvNewVersion.setText(String.format("V%s",getVersionName()));
        tvFeedback.setOnClickListener(this);
        tvNewVersion.setOnClickListener(this);
        tvUserAgree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvFeedback) {
            toActivity(FeedbackActivity.class, null);
        } else if (v == tvNewVersion) {

        } else if (v == tvUserAgree) {
            Bundle bundle = new Bundle();
            bundle.putString("title", "用户协议");
            bundle.putString("url", getString(R.string.useragreement_url));
            toActivity(WebViewActivity.class, bundle);
        }
    }

    private void requestAppNewVersion() {

        JSONObject json = getAuthJSONObject();
        final String version_code = PackageTool.getVersionCode(this);
        try {
            json.put("version", version_code);
            json.put("type", 2);
            OkHttpUtil.postRequest(Constant.UPGRADE_CHECK, json, hashCode(), new OKCallback<AppNewVersionRDO>(new TypeToken<UCResponse<AppNewVersionRDO>>() {
            }) {
                @Override
                public void onResponse(int statuscode, @Nullable UCResponse<AppNewVersionRDO> ucResponse) {
                    if (isSucccess()) {
                        if (ucResponse.getAttributes().getNeeded()) {
                            appNewVersion = ucResponse.getAttributes();
                            setAppNewVersion(appNewVersion);
                            tvNewVersion.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_find_new_version), null);
                            String webLinkText = "<font color='#0bd3a6'><a href='" + appNewVersion.getUrl() + "' style='text-decoration:none; color:#0000FF'>" + String.format("V%s",getVersionName()) + "</a>";
                            tvNewVersion.setText(Html.fromHtml(webLinkText));
                            tvNewVersion.setMovementMethod(LinkMovementMethod.getInstance());
//                            tvNewVersion.setText(covertToVersionName(appNewVersion.getVersion()));
                        } else {
                            tvNewVersion.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                    }
                }

                @Override
                public void onFailure(IOException e) {

                }
            });
        } catch (JSONException ex) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestAppNewVersion();
    }
}
