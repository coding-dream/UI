package com.ruoxu.gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private List<Item> mlist=new ArrayList<>();
    private int[] mImgs ={R.mipmap.test1,R.mipmap.test2,R.mipmap.test3,R.mipmap.test4,R.mipmap.test5,R.mipmap.test6,R.mipmap.test7
            ,R.mipmap.test8,R.mipmap.test9,R.mipmap.test10,R.mipmap.test11,R.mipmap.test12,R.mipmap.test13,R.mipmap.test14,R.mipmap.test15
            ,R.mipmap.test16,R.mipmap.test17,R.mipmap.test18,R.mipmap.test19,R.mipmap.test20,R.mipmap.test21,R.mipmap.test22,R.mipmap.test23};

    private GalleryRecyclerView mGalleryRecyclerView;
    private TextView mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        for (int i=0;i<mImgs.length;i++){
            Item item=new Item();
            item.setImg(mImgs[i]);
            item.setName(i+"km");
            mlist.add(item);
        }

    }
    private void initView() {
        mGalleryRecyclerView = (GalleryRecyclerView) findViewById(R.id.gallery);
        mPosition = (TextView) findViewById(R.id.position);

        mGalleryRecyclerView.setTouchDownlistem(new GalleryRecyclerView.TouchDownListem() {
            @Override
            public void onTouchDown() {
                Log.d("MainActivity", "onTouchDown");
            }
        });


        mGalleryRecyclerView.setAdapter(new ItemAdapter(this,mlist));

//        mGalleryRecyclerView.setAdapter(new CommonAdapter<Item>() {
//            @Override
//            protected void convert(ViewHolder holder, final Item item, int position) {
//                holder.setText(R.id.name, item.getName());
//                holder.setImageResource(R.id.profile_image,item.getImg());
//                holder.getView(R.id.item_btn).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(mContext, item.getName(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });


        mGalleryRecyclerView.setOnViewSelectedListener(new GalleryRecyclerView.OnViewSelectedListener() {
            @Override
            public void onSelected(View view, final int position) {
                Log.v("MainActivity", ""+mlist.get(position).getName());
                mPosition.setText(mlist.get(position).getName());
            }
        });
    }

    public void select(View view) {
        int position = new Random().nextInt(10);
        Log.d("MainActivity", "position " + position);
        mGalleryRecyclerView.setSelectPosition(position);
    }
}
