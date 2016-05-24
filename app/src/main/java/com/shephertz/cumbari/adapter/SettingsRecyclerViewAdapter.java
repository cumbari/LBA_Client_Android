package com.shephertz.cumbari.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shephertz.cumbari.R;
import com.shephertz.cumbari.location.Coordinate;
import com.shephertz.cumbari.utils.SharedPrefKeys;
import com.shephertz.cumbari.utils.SharedPreferenceUtil;
import com.shephertz.cumbari.utils.TypeFaceClass;

import java.util.ArrayList;


public class SettingsRecyclerViewAdapter extends RecyclerView.Adapter<SettingsRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private TypeFaceClass typeClass;
    private ArrayList<String> moreArray;
    private SharedPreferenceUtil sharedPreferenceUtil;

    public SettingsRecyclerViewAdapter(Context context, ArrayList<String> data)
    {
        super();
        this.context = context;
        this.moreArray = data;
        typeClass=new TypeFaceClass(context);
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(context);
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
        switch (position){
            case 0:
                holder.more_sub_title.setText(sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG"));
                break;
            case 1:
                if(sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Meter.getValue()){
                    holder.more_sub_title.setText(context.getResources().getString(R.string.meter));
                }else if(sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Miles.getValue()) {
                    holder.more_sub_title.setText(context.getResources().getString(R.string.mile));
                }
                break;
            case 2:
                holder.more_sub_title.setText(sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,10)+"");
                break;
            case 3:
                int range = sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,10000);
                if(sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Meter.getValue()){
                    holder.more_sub_title.setText(range+" m");
                }else if(sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Miles.getValue()) {
                    double range1 = (float) range / 1000.0;
                    range1 = range1 / 1.6;
                    holder.more_sub_title.setText(String.format("%.1f", range1) + " mi");
                }
                break;
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
