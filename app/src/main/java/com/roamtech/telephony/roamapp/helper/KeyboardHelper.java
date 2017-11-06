package com.roamtech.telephony.roamapp.helper;

import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.KeyBordGridAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import org.linphone.LinphoneManager;

import java.util.ArrayList;
import java.util.List;

public class KeyboardHelper implements OnClickListener, OnItemClickListener {
    private static final List<Key> keyList = new ArrayList<Key>();

    static {
        keyList.add(new Key("1", null));
        keyList.add(new Key("2", "ABC"));
        keyList.add(new Key("3", "DEF"));
        keyList.add(new Key("4", "GHI"));
        keyList.add(new Key("5", "JKL"));
        keyList.add(new Key("6", "MNO"));
        keyList.add(new Key("7", "PDRS"));
        keyList.add(new Key("8", "TUV"));
        keyList.add(new Key("9", "WXYZ"));
        keyList.add(new Key("*", null));
        keyList.add(new Key("0", "+"));
        keyList.add(new Key("#", null));
    }

    private BaseActivity mActivity;
    /**
     * 号码显示框
     **/
    private EditText mNumberEditText;
    /****
     * 键盘控件
     **/
    private LinearLayout mLayoutKeybord;
    private GridView mGvKeybord;
    private ImageView mKeyDelete;
    private ImageView mKeyCall;

    public interface OnKeybordListener {
        /**
         * 输入文字发生改变
         *
         * @param inputText
         */
        void onTextChange(String inputText);

        /**
         * 点击拨打电话
         *
         * @param inputText
         */
        void onCall(String inputText);

    }

    private OnKeybordListener mOnKeybordListener;

    public KeyboardHelper(BaseActivity mActivity, EditText mNumberEditText,
                          LinearLayout mLayoutKeybord) {
        super();
        this.mActivity = mActivity;
        this.mNumberEditText = mNumberEditText;
        this.mLayoutKeybord = mLayoutKeybord;
        init();
    }

    private void init() {
        mGvKeybord = (GridView) mLayoutKeybord.findViewById(R.id.gv_keyboard);
        mGvKeybord.setOnItemClickListener(this);
        mGvKeybord.setAdapter(new KeyBordGridAdapter(mActivity, keyList));
        mKeyCall = (ImageView) mLayoutKeybord.findViewById(R.id.ivCall);
        mKeyCall.setOnClickListener(this);
        mKeyDelete = (ImageView) mLayoutKeybord.findViewById(R.id.ivDelete);
        mKeyDelete.setOnClickListener(this);
        //长按清楚所有
        mKeyDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mNumberEditText.getText().clear();
                mOnKeybordListener.onTextChange(mNumberEditText.getText().toString());
                return true;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Key key = (Key) mGvKeybord.getItemAtPosition(position);
        String code = key.getCode();
        LinphoneManager.getInstance().playDtmf(mActivity.getContentResolver(), code.charAt(0));
        Editable editable = mNumberEditText.getText();
        int start = mNumberEditText.getSelectionStart();
        editable.insert(start, code);
        if (mOnKeybordListener != null) {
            mOnKeybordListener.onTextChange(mNumberEditText.getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == mKeyCall) {
            if (mOnKeybordListener != null) {
                mOnKeybordListener.onCall(mNumberEditText.getText().toString());
                mNumberEditText.setText("");
                mOnKeybordListener.onTextChange(mNumberEditText.getText().toString());

            }

        } else if (v == mKeyDelete) {
            Editable editable = mNumberEditText.getText();
            int start = mNumberEditText.getSelectionStart();
            if (editable != null && editable.length() > 0) {
                if (start > 0) {
                    editable.delete(start - 1, start);
                    if (mOnKeybordListener != null) {
                        mOnKeybordListener.onTextChange(mNumberEditText.getText().toString());
                    }
                }
            }
        }
    }

    public OnKeybordListener getOnKeybordListener() {
        return mOnKeybordListener;
    }

    public void setOnKeybordListener(OnKeybordListener onKeybordListener) {
        this.mOnKeybordListener = onKeybordListener;
    }

    public static class Key {
        private String code;
        private String letter;

        public Key(String code, String letter) {
            super();
            this.code = code;
            this.letter = letter;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLetter() {
            return letter;
        }

        public void setLetter(String letter) {
            this.letter = letter;
        }
    }
}
