package com.ruoxu.slidebar.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruoxu.slidebar.R;
import com.ruoxu.slidebar.model.ContactListItem;

public class ContactListItemView extends RelativeLayout {
    TextView mSection;
    TextView mUserName;

    public ContactListItemView(Context context) {
        this(context, null);
    }

    public ContactListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_contact_item, this);
        mSection = (TextView) findViewById(R.id.section);
        mUserName = (TextView) findViewById(R.id.user_name);

    }

    public void bindView(ContactListItem contactListItem) {
        mUserName.setText(contactListItem.userName);
        if (contactListItem.showFirstLetter) {
            mSection.setVisibility(VISIBLE);
            mSection.setText(contactListItem.getFirstLetterString());
        } else {
            mSection.setVisibility(GONE);
        }
    }
}
