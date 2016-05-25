package com.moblyo.market.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moblyo.market.R;
import com.moblyo.market.utils.SharedPrefKeys;
import com.moblyo.market.utils.SharedPreferenceUtil;
import com.moblyo.market.utils.TypeFaceClass;

import java.util.ArrayList;


public class MoreRecyclerViewAdapter extends RecyclerView.Adapter<MoreRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private TypeFaceClass typeClass;
    private ArrayList<String> moreArray;

    public MoreRecyclerViewAdapter(Context context, ArrayList<String> data)
    {
        super();
        this.context = context;
        this.moreArray = data;
        typeClass=new TypeFaceClass(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView more_title;
        TextView more_sub_title;

        ViewHolder(View itemView) {
            super(itemView);

            more_title = (TextView)itemView.findViewById(R.id.more_title);
            more_sub_title = (TextView)itemView.findViewById(R.id.more_sub_title);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_more, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        typeClass.setTypefaceBold(holder.more_title);
        typeClass.setTypefaceNormal(holder.more_sub_title);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.more_title.setText(moreArray.get(position));
        if(position == 0){
            holder.more_sub_title.setVisibility(View.VISIBLE);
            if(SharedPreferenceUtil.getInstance(context).getData(SharedPrefKeys.IS_CURRENT_POSITION,true)){
                holder.more_sub_title.setText(context.getResources().getString(R.string.current_position));
            }else{
                holder.more_sub_title.setText(context.getResources().getString(R.string.new_position));
            }
        }else{
            holder.more_sub_title.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return moreArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  void notifyData() {
        notifyDataSetChanged();
    }
}
