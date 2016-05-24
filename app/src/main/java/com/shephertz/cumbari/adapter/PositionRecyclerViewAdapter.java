package com.shephertz.cumbari.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shephertz.cumbari.R;
import com.shephertz.cumbari.utils.TypeFaceClass;

import java.util.ArrayList;


public class PositionRecyclerViewAdapter extends RecyclerView.Adapter<PositionRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private TypeFaceClass typeClass;
    private ArrayList<String> positonArray;
    public int indexSelected = 0;

    public PositionRecyclerViewAdapter(Context context, ArrayList<String> data, int selected)
    {
        super();
        this.context = context;
        this.positonArray = data;
        this.indexSelected = selected;
        typeClass=new TypeFaceClass(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView position_title;
        ImageView position_selected;

        ViewHolder(View itemView) {
            super(itemView);

            position_title = (TextView)itemView.findViewById(R.id.position_title);
            position_selected = (ImageView)itemView.findViewById(R.id.position_selected);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_position, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        typeClass.setTypefaceBold(holder.position_title);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.position_title.setText(positonArray.get(position));
        if(indexSelected == position){
            holder.position_selected.setVisibility(View.VISIBLE);
        }else{
            holder.position_selected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return positonArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  void notifyData() {
        notifyDataSetChanged();
    }
}
