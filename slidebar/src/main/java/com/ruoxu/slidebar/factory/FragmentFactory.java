package com.ruoxu.slidebar.factory;


import com.ruoxu.slidebar.R;
import com.ruoxu.slidebar.fragment.BaseFragment;
import com.ruoxu.slidebar.fragment.ContactFragment;
import com.ruoxu.slidebar.fragment.ConversationFragment;
import com.ruoxu.slidebar.fragment.DynamicFragment;

import java.util.HashMap;

public class FragmentFactory {
    public static final String TAG = "FragmentFactory";

    private static FragmentFactory sFragmentFactory;

    private HashMap<Integer,BaseFragment> hashMap = new HashMap();

    public static FragmentFactory getInstance() {
        if (sFragmentFactory == null) {
            synchronized (FragmentFactory.class) {
                if (sFragmentFactory == null) {
                    sFragmentFactory = new FragmentFactory();
                }
            }
        }
        return sFragmentFactory;
    }

    public BaseFragment getFragment(int what) {
        BaseFragment fragment = hashMap.get(what);
        if (fragment == null) {
            switch (what) {
                case R.id.conversations:
                    fragment = new ConversationFragment();
                    break;
                case R.id.contacts:
                    fragment = new ContactFragment();
                    break;
                case R.id.dynamic:
                    fragment = new DynamicFragment();
                    break;
            }
            hashMap.put(what, fragment);

        }
        return  fragment;

    }


}
