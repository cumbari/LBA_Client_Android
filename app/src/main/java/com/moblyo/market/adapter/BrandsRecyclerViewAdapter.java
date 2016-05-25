package com.moblyo.market.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.moblyo.market.R;
import com.moblyo.market.model.ListOfBrandHits;
import com.moblyo.market.utils.TypeFaceClass;

import java.util.ArrayList;


public class BrandsRecyclerViewAdapter extends RecyclerView.Adapter<BrandsRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private TypeFaceClass typeClass;
    private ArrayList<ListOfBrandHits> listOfBrandHits;

    public BrandsRecyclerViewAdapter(Context context, ArrayList<ListOfBrandHits> data)
    {
        super();
        this.context = context;
        this.listOfBrandHits = data;
        typeClass=new TypeFaceClass(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView brand_image;
        TextView brand_name;
        TextView no_of_coupons;

        ViewHolder(View itemView) {
            super(itemView);
            brand_image = (ImageView)itemView.findViewById(R.id.categories_coupon_image);

            brand_name = (TextView)itemView.findViewById(R.id.categories_title);
            no_of_coupons = (TextView)itemView.findViewById(R.id.categories_number);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_categories, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        typeClass.setTypefaceBold(holder.brand_name);
        typeClass.setTypefaceNormal(holder.no_of_coupons);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        ListOfBrandHits couponData = listOfBrandHits.get(position);
        holder.brand_name.setText(couponData.getBrandName());
        holder.no_of_coupons.setText(couponData.getNumberOfCoupons()+"");
        Ion.with(context).load(couponData.getBrandIcon())
                .withBitmap()
                .placeholder(R.drawable.no_image_icon).resize(dp2px(40), dp2px(40))
                .error(R.drawable.no_image_icon)
                .intoImageView(holder.brand_image);

    }

    @Override
    public int getItemCount() {
        return listOfBrandHits.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  void notifyData() {
        notifyDataSetChanged();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
