package com.ruoxu.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wangli on 16/12/22.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private Context mContext;
    private List<Item> mList;
    ItemAdapter(Context context, List<Item> mlist){
        this.mContext = context;
        this.mList = mlist;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = mList.get(position);

        MyViewHolder myViewHolder = holder;
        myViewHolder.name.setText(item.getName());
        myViewHolder.profile.setImageResource(item.getImg());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ImageView profile;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            profile = (ImageView) itemView.findViewById(R.id.profile_image);

        }
    }
}
