package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.Blacklist;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.RDContactPhone;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.event.EventBlacklist;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.view.RoundImageView;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneProxyConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.roamtech.telephony.roamapp.util.CallMessageUtil.blackDao;


public class ContactInfoActivity extends BaseActivity {
    public static final int EDIT_REQUESTCODE = 1;

    private RDContact mContact;
    private boolean displayChatAddressOnly = false;
    private TextView mEdit;
    private boolean isEdit = false;
    private RoundImageView userPhoto;

    /**
     * 用户名
     */
    private TextView etUserNameShow;
    private TextView tvSex;
    private TextView tvArea;


    private TextView tvPhonenumber;
    private ImageView ivsendphonemessage;
    private ImageView ivPhonecall;

    private TextView tvHoursetelnumber;
    private ImageView ivSendtelmessage;
    private ImageView ivtelcall;
    private TextView tvStopCall;
    private TextView tvAddInCollection;

    private TextView tvAddPhonenumber;

    private LinearLayout layout_black;
    private TextView tv_blacklist;
    private BlacklistDBModel queryModel;
    private Boolean isAddBlacklist = false;
    private List<RDContactPhone> phones = null;
    private Blacklist blacklistFun;
    private OnClickListener dialListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (LinphoneActivity.isInstanciated()) {
                LinphoneCore lc = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull();
                if (lc != null) {
                    LinphoneProxyConfig lpc = lc.getDefaultProxyConfig();
                    String to;
                    if (lpc != null) {
                        String address = v.getTag().toString();
                        if (!address.contains("@")) {
                            to = lpc.normalizePhoneNumber(address);
                        } else {
                            to = v.getTag().toString();
                        }
                    } else {
                        to = v.getTag().toString();
                    }
                    //	LinphoneActivity.instance().setAddressGoToDialerAndCall(to, mContact.getName(), mContact.getPhotoUri());
                    LinphoneActivity.instance().setAddressGoToDialerAndCall(to, mContact.getDisplayName(), RDContactHelper.getContactPhotoUri(mContact.getId()));

                }
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_contact_info;
    }

    @Override
    public void initData() {
        super.initData();
        mContact = (RDContact) getBundle().getSerializable(RDContact.class.getName());
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mEdit = (TextView) findViewById(R.id.id_edit);
        mEdit.setOnClickListener(this);
        userPhoto = (RoundImageView) findViewById(R.id.id_circle_image);
        etUserNameShow = (TextView) findViewById(R.id.id_username_show);
        /**非编辑状态**/

        tvHoursetelnumber = (TextView) findViewById(R.id.tv_hoursetelnumber);
        tvStopCall = (TextView) findViewById(R.id.tvStopCall);
        tvAddInCollection = (TextView) findViewById(R.id.tvaddInCollection);
        layout_black = (LinearLayout) findViewById(R.id.layout_black);
        tv_blacklist = (TextView) findViewById(R.id.tv_blacklist);
        if (mContact.getId() < 0) {
            mEdit.setVisibility(View.GONE);
        } else {
            mEdit.setVisibility(View.VISIBLE);
        }
        displayContact();
        blacklistFun = new Blacklist(getApplicationContext());
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        userPhoto.setOnClickListener(this);
        layout_black.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_black:
                confirmDialog();
                break;
            case R.id.id_edit:
                RDContactHelper.editContact(this, mContact.getId(), EDIT_REQUESTCODE);
                break;
        }
    }

    private String getPhoneTypeString(int type) {
        switch (type) {
            case 1:
                return getString(R.string.house);
            case 2:
                return getString(R.string.phone);
            case 3:
                return getString(R.string.work);
            default:
                return getString(R.string.phone);
        }
    }

    private void displayContact() {

        if (mContact != null && mContact.getPhotoId() > 0) {
            //InputStream input = Compatibility.getContactPictureInputStream(LinphoneActivity.instance().getContentResolver(), mContact.getID());
            userPhoto.setImageBitmap(RDContactHelper.getContactHeadBitmap(getContentResolver(), mContact.getId()));
        } else {
            userPhoto.setImageResource(R.drawable.logo_default_userphoto);
        }

        TextView contactName = (TextView) findViewById(R.id.contactName);
        contactName.setText(mContact.getDisplayName());
        etUserNameShow.setText(mContact.getDisplayName());

        TableLayout controls = (TableLayout) findViewById(R.id.controls);
        controls.removeAllViews();
        phones = mContact.getPhoneList();
        if (phones == null) {
            if (mContact.getDialPhone() != null) {
                RDContact rdContact = RDContactHelper.queryContactById(getContentResolver(), mContact.getId());
                phones = rdContact.getPhoneList();
            }
        }
        if (phones == null) return;
        for (RDContactPhone contactPhone : phones) {
            final String phoneNumber = contactPhone.getNumber().replace("-", "").replace(" ", "").replace("+86", "");
            queryModel = CallMessageUtil.blackDao.queryBlacklistByPhone(phoneNumber, getUserId());
            if (queryModel != null) {
                tv_blacklist.setText(getString(R.string.cancel_blacklist));
                isAddBlacklist = true;
            }

            String numberOrAddress = phoneNumber;
            View v = getLayoutInflater().inflate(R.layout.contact_control_row, null);
            String displayednumberOrAddress = numberOrAddress;
            if (numberOrAddress.startsWith("sip:")) {
                displayednumberOrAddress = displayednumberOrAddress.replace("sip:", "");
            }
            TextView phonetype = (TextView) v.findViewById(R.id.phonetype);
            phonetype.setText(getPhoneTypeString(contactPhone.getType()));
            TextView tv = (TextView) v.findViewById(R.id.numberOrAddress);
            tv.setText(displayednumberOrAddress);
            tv.setSelected(true);

            if (!displayChatAddressOnly) {
                v.findViewById(R.id.dial).setOnClickListener(dialListener);
                v.findViewById(R.id.dial).setTag(displayednumberOrAddress);
            } else {
                v.findViewById(R.id.dial).setVisibility(View.GONE);
            }

            v.findViewById(R.id.start_chat).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (LinphoneActivity.isInstanciated()) {
                        LinphoneActivity.instance().displayChat(phoneNumber);
                    }
                }
            });
            LinphoneProxyConfig lpc = LinphoneManager.getLc().getDefaultProxyConfig();
            if (lpc != null) {
                displayednumberOrAddress = lpc.normalizePhoneNumber(displayednumberOrAddress);
                if (!displayednumberOrAddress.startsWith("sip:")) {
                    numberOrAddress = "sip:" + displayednumberOrAddress;
                }

                String tag = numberOrAddress;
                if (!numberOrAddress.contains("@")) {
                    tag = numberOrAddress + "@" + lpc.getDomain();
                }
                v.findViewById(R.id.start_chat).setTag(tag);
            } else {
                v.findViewById(R.id.start_chat).setTag(numberOrAddress);
            }

            if (getResources().getBoolean(R.bool.disable_chat)) {
                v.findViewById(R.id.start_chat).setVisibility(View.GONE);
            }
            controls.addView(v);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUESTCODE && resultCode != RESULT_CANCELED) {
            //long contactId = ContentUris.parseId(data.getData());
            long contactId = mContact.getId();
            //查询联系人重新启动
            RDContact contact = RDContactHelper.queryContactById(getContentResolver(), contactId);
            if (contact != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(RDContact.class.getName(), contact);
                toActivityClearTop(ContactInfoActivity.class, bundle);
            } else {
                showToast(getString(R.string.contacts_has_been_delete));
                finish();
            }
        }
    }

    private void confirmDialog() {
        String strConfirm = isAddBlacklist ? getString(R.string.confirm_cancel_blacklist) : getString(R.string.confirm_add_blacklist);
        final TipDialog dialog = new TipDialog(this, strConfirm, "");
        dialog.setRightButton(getString(R.string.button_ok), new TipDialog.OnClickListener() {
            @Override
            public void onClick(int which) {
                ArrayList<String> phoneList = new ArrayList<>();
                if (!isAddBlacklist) {
                    queryModel = new BlacklistDBModel();
                    if (phones != null) {
                        for (RDContactPhone item : phones) {
                            if (item.getNumber() != null) {
                                queryModel.phone = item.getNumber().replace("-", "").replace("+86", "").replace(" ", "");
                                queryModel.userId = getUserId();
                                queryModel.serverId = -1;
                                blackDao.add(queryModel);
                                phoneList.add(queryModel.phone);
                            }
                        }
                    }
                    tv_blacklist.setText(getString(R.string.cancel_blacklist));
                    isAddBlacklist = true;
                } else {
                    if (phones != null) {
                        for (RDContactPhone item : phones) {
                            if (item.getNumber() != null) {
                                queryModel = CallMessageUtil.blackDao.queryBlacklistByPhone(item.getNumber().replace("-", "").replace("+86", "").replace(" ", ""), getUserId());
                                if (queryModel != null) {
                                    String phoneNumber = queryModel.phone;
                                    blackDao.delete(queryModel);
                                    phoneList.add(phoneNumber);
                                }
                            }
                        }
                    }
                    tv_blacklist.setText(getString(R.string.add_blacklist));
                    isAddBlacklist = false;
                }
                addOrDeleteBlacklist(phoneList);
                EventBus.getDefault().postSticky(new EventBlacklist());
            }
        });

        dialog.setLeftButton(getString(R.string.button_cancel), new TipDialog.OnClickListener() {
            @Override
            public void onClick(int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 服务端 黑名单操作
     */
    private void addOrDeleteBlacklist(ArrayList<String> phoneList) {
        JSONObject jsonObject = getAuthJSONObject();
        try {
            jsonObject.put("phones", phoneList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isAddBlacklist) {
            blacklistFun.addBlacklist(jsonObject, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                }

                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    super.onFailure(errorMap);
                }
            });
        } else {
            blacklistFun.deleteBlacklist(jsonObject, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    super.onFailure(errorMap);
                }

                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                }
            });
        }
    }
}
