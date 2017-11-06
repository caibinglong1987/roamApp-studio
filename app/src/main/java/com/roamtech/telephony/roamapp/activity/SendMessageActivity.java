package com.roamtech.telephony.roamapp.activity;

import java.util.ArrayList;
import java.util.List;

import org.linphone.Contact;
import org.linphone.ContactsManager;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;

import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.KeyboardSearchAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.view.FlowLayout;
import com.roamtech.telephony.roamapp.widget.sortlist.SortModel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SendMessageActivity extends BaseActivity implements OnFocusChangeListener {
    private TextView mBtnSend;// 发送btn
    private ListView mListView;
    private EditText mEditTextContent;
    private ImageView ivAddMessageContact;
    private FlowLayout flowLayout;
    private EditText mEditContact;
    private KeyboardSearchAdapter mKeybordSearchAdapter;
    private List<RDContact> mAllContactsList;
    private ContactsManager contactsManager;
    private RDContact mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmessage);

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mListView = (ListView) findViewById(android.R.id.list);
        mEditContact = (EditText) findViewById(R.id.et_searchContact);
        mBtnSend = (TextView) findViewById(R.id.tv_send);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        ivAddMessageContact = (ImageView) findViewById(R.id.tv_addmessageContact);
        flowLayout = (FlowLayout) findViewById(R.id.flowlayout);
        mListView = (ListView) findViewById(android.R.id.list);
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        mEditTextContent.setOnFocusChangeListener(this);
        mBtnSend.setOnClickListener(this);
        ivAddMessageContact.setOnClickListener(this);
        mEditContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if (inputText.equals("")) {
                    //titleLayout.setVisibility(View.INVISIBLE);
                    mKeybordSearchAdapter.refreash(new ArrayList<RDContact>());
                } else {
                    //titleLayout.setVisibility(View.VISIBLE);
                    List<RDContact> fileterList = RDContactHelper.search(inputText);
                    mKeybordSearchAdapter.refreash(fileterList);

                    mKeybordSearchAdapter.setSearchText(inputText);
                    mKeybordSearchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

        });
        mAllContactsList = new ArrayList<>();
        mKeybordSearchAdapter = new KeyboardSearchAdapter(this,
                mAllContactsList);
        loadContacts();
        mListView.setAdapter(mKeybordSearchAdapter);
        mListView.setOnItemClickListener(this);
    }

    private void loadContacts() {
        mAllContactsList = RDContactHelper.getOriginSystemContacts();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == mBtnSend) {


        } else if (v == ivAddMessageContact) {
            toActivity(SelectContactsActivity.class, null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        mContact = (RDContact) parent.getItemAtPosition(position);
        String sipUri = mContact.getDialPhone().getNumber();
        try {
            LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(CallUtil.getSipTo());
            if (address.getUserName().equals(sipUri)) {
                sipUri = CallUtil.getSipTo();
            } else {
                sipUri = CallUtil.getSipTo() + ";to=" + CallUtil.getRealToNumber(sipUri);
            }
        } catch (LinphoneCoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //sipUri = CallUtil.getSipTo()+";to="+CallUtil.getRealToNumber(sipUri);
        if (LinphoneActivity.isInstanciated()) {
            LinphoneActivity.instance().displayChat(sipUri);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mEditTextContent && hasFocus) {

        }
    }
}
