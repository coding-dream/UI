package com.ruoxu.slidebar;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;
import com.ruoxu.slidebar.factory.FragmentFactory;


public class MainActivity extends AppCompatActivity {

    BottomBar mBottomBar;
    FrameLayout mFragmentContainer;
    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mFragmentManager = getSupportFragmentManager();

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, FragmentFactory.getInstance().getFragment(tabId)).commit();
            }
        });

    }


    private void initView() {
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);

    }


    private void updateUnreadCount(int count) {

        BottomBarTab bottomBar = mBottomBar.getTabWithId(R.id.message);
        bottomBar.setBadgeCount(count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadCount(3); //更新未读消息数，这里是测试数据
    }


}
