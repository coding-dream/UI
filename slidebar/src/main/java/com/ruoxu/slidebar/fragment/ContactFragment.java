package com.ruoxu.slidebar.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ruoxu.slidebar.R;
import com.ruoxu.slidebar.adapter.ContactListAdapter;
import com.ruoxu.slidebar.model.ContactListItem;
import com.ruoxu.slidebar.view.SlideBar;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends BaseFragment {
    public static final String TAG = ContactFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SlideBar slideBar;
    private TextView mSession;

    private static final int POSITION_NOT_FOUND = -1;

    private ContactListAdapter mContactListAdapter;

    @Override
    protected void initView(View root) {
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        slideBar = (SlideBar) root.findViewById(R.id.slide_bar);
        mSession = (TextView) root.findViewById(R.id.section);

        // init recyclerView
        List list = loadData();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        mContactListAdapter = new ContactListAdapter(getContext(), list);
        mContactListAdapter.setOnItemClickListener(new ContactListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {

            }

            @Override
            public void onItemLongClick(String name) {

            }
        });
        recyclerView.setAdapter(mContactListAdapter);

        // init swipeRefresh
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        slideBar.setOnSlidingBarChangeListener(new SlideBar.OnSlideBarChangeListener() {
            @Override
            public void onSectionChange(int index, String section) {
                mSession.setVisibility(View.VISIBLE);
                mSession.setText(section);

//                RecyclerView直到界面出现对应section的联系人 才开始滚动
                int sectionPosition = findPosition(section);
                if (sectionPosition != POSITION_NOT_FOUND) {
                    recyclerView.smoothScrollToPosition(sectionPosition);
                }


            }

            @Override
            public void onSlidingFinish() {
                mSession.setVisibility(View.GONE);
            }
        });

    }

    /**
     * @param section 首字符
     * @return find 联系人列表中首字符是section的第一个联系人在联系人列表中的位置
     */
    private int findPosition(String section) {
        //遍历所有的item,查找某个item的name的首字符与session相等的 项
        List<ContactListItem> contactListItems = mContactListAdapter.getContactListItems();
        for (int i = 0; i < contactListItems.size(); i++) {
            if (section.equals(contactListItems.get(i).getFirstLetterString())) {
                return i;
            }
        }
        return POSITION_NOT_FOUND;
    }


    private List loadData() {
        List<ContactListItem> contactList = new ArrayList();
        //需要自己排序

        List<ContactListItem> list = new ArrayList<>();

        for(int i=1;i<20;i++) {
            ContactListItem contactListItem = new ContactListItem();
            contactListItem.userName = "B"+i;
            list.add(contactListItem);
        }


        for(int i=0;i<20;i++) {
            ContactListItem contactListItem = new ContactListItem();
            contactListItem.userName = "z"+i;
            list.add(contactListItem);
        }

        for(int i=0;i<20;i++) {
            ContactListItem contactListItem = new ContactListItem();
            contactListItem.userName = "A"+i;
            list.add(contactListItem);
        }



        for (int i = 0; i < list.size(); i++) {
            ContactListItem list_item = new ContactListItem();
            list_item.userName = list.get(i).userName;

            if (itemInSameGroup(i, list_item,list)) {
                list_item.showFirstLetter = false;
            }
            contactList.add(list_item);
        }


        return contactList;
    }


    /**
     * 当前联系人跟上个联系人比较，如果首字符相同则返回true
     * @param i 当前联系人下标
     * @param item 当前联系人数据模型
     * @return true 表示当前联系人和上一联系人在同一组
     */
    private boolean itemInSameGroup(int i, ContactListItem item,List<ContactListItem> list) {
        return i > 0 && (item.getFirstLetter() == list.get(i - 1).getFirstLetter());
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contacts;
    }



}