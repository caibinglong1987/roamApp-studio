package com.roamtech.telephony.roamapp.helper;

import java.util.ArrayList;
import java.util.List;

import org.linphone.LinphoneManager;

import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.adapter.KeyBordCallingGridAdapter;
import com.roamtech.telephony.roamapp.R;

import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

public class KeyboardCallingHelper implements OnClickListener, OnItemClickListener {
    private static final List<Key> keyList = new ArrayList<>();

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
    private GridView mGvKeyboard;

    public interface OnKeyboardListener {
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

    private OnKeyboardListener mOnKeyboardListener;

    public KeyboardCallingHelper(BaseActivity mActivity) {
        super();
        this.mActivity = mActivity;
        init();
    }

    private void init() {
        mNumberEditText = new EditText(mActivity);
        mGvKeyboard = (GridView) mActivity.findViewById(R.id.gv_keyboard);
        mGvKeyboard.setOnItemClickListener(this);
        mGvKeyboard.setAdapter(new KeyBordCallingGridAdapter(mActivity, keyList));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Key key = (Key) mGvKeyboard.getItemAtPosition(position);
        String code = key.getCode();
        LinphoneManager.getInstance().playDtmf(mActivity.getContentResolver(), code.charAt(0));
        Editable editable = mNumberEditText.getText();
        int start = mNumberEditText.getSelectionStart();
        editable.insert(start, code);
        if (mOnKeyboardListener != null) {
            mOnKeyboardListener.onTextChange(code);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        this.mOnKeyboardListener = onKeyboardListener;
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
