package com.roamtech.telephony.roamapp.enums;
import android.support.v4.app.Fragment;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.fragment.ContactsFragment;
import com.roamtech.telephony.roamapp.fragment.HomeFragment;
import com.roamtech.telephony.roamapp.fragment.KeyboardGroupFragment;
import com.roamtech.telephony.roamapp.fragment.MessageFragment;
import com.roamtech.telephony.roamapp.fragment.RDMallFragment;

public enum MainTab {

    HOME("home", "首页", R.drawable.tab_home, HomeFragment.class),

    CONTACTS("contacts", "通讯录", R.drawable.tab_contacts, ContactsFragment.class),

    KEYBOARD("keyboard", "拨号键盘", R.drawable.tab_keyboard, KeyboardGroupFragment.class),

    MESSAGE("message", "消息", R.drawable.tab_message, MessageFragment.class),

    MALL("RD_mall", "RD商城", R.drawable.tab_mall, RDMallFragment.class);

    private String tag;
    private String name;
    private int resIcon;
    private Class<? extends Fragment> fragmentClazz;

    private MainTab(String tag, String name, int resIcon,
                    Class<? extends Fragment> fragmentClazz) {
        this.tag = tag;
        this.name = name;
        this.resIcon = resIcon;
        this.fragmentClazz = fragmentClazz;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<? extends Fragment> getFragmentClazz() {
        return fragmentClazz;
    }

    public void setFragmentClazz(Class<? extends Fragment> fragmentClazz) {
        this.fragmentClazz = fragmentClazz;
    }

    public static int getCurrentTagIndex(String tag) {
        MainTab mainTabs[] = MainTab.values();
        for (int index = 0; index < mainTabs.length; index++) {
            if (mainTabs[index].getTag().equals(tag)) {
                return index;
            }
        }
        return 0;
    }
}
