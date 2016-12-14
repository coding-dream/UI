package com.ruoxu.slidebar.model;


import com.ruoxu.slidebar.R;

public class ContactListItem {
    public static final String TAG = "ContactListItem";

    public int avatar = R.mipmap.avatar;

    public String userName;

    public boolean showFirstLetter = true;

    public char getFirstLetter() {
        return userName.charAt(0);
    }

    public String getFirstLetterString() {
        return String.valueOf(getFirstLetter()).toUpperCase();
    }
}
