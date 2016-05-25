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
import com.moblyo.market.HomeScreenActivity;
import com.moblyo.market.R;
import com.moblyo.market.model.ListOfCategories;
import com.moblyo.market.model.ListOfCategoryHits;
import com.moblyo.market.utils.TypeFaceClass;

import java.util.ArrayList;


public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private TypeFaceClass typeClass;
    private ArrayList<ListOfCategories> listOfCategories;

    public CategoriesRecyclerViewAdapter(Context context, ArrayList<ListOfCategories> data)
    {
        super();
        this.context = context;
        this.listOfCategories = data;
        typeClass=new TypeFaceClass(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView categories_coupon_image;
        TextView categories_title;
        TextView categories_number;

        ViewHolder(View itemView) {
            super(itemView);
            categories_coupon_image = (ImageView)itemView.findViewById(R.id.categories_coupon_image);

            categories_title = (TextView)itemView.findViewById(R.id.categories_title);
            categories_number = (TextView)itemView.findViewById(R.id.categories_number);
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
        typeClass.setTypefaceBold(holder.categories_title);
        typeClass.setTypefaceNormal(holder.categories_number);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        ListOfCategories couponData = listOfCategories.get(position);
        holder.categories_title.setText(couponData.getCategoryName());
        holder.categories_number.setText(getNumberOfCoupons(couponData));
        Ion.with(context).load(couponData.getSmallImage())
                .withBitmap()
                .placeholder(R.drawable.no_image_icon).resize(dp2px(40),dp2px(40))
                .error(R.drawable.no_image_icon)
                .intoImageView(holder.categories_coupon_image);
    }

    private String getNumberOfCoupons(ListOfCategories couponData) {
        if(((HomeScreenActivity)context).getCouponsData != null
                && ((HomeScreenActivity)context).getCouponsData.getListOfCategoryHits() != null) {
            ArrayList<ListOfCategoryHits> listOfCouponsInCategory = ((HomeScreenActivity) context).getCouponsData.getListOfCategoryHits();
            for (int storeIndex = 0; storeIndex < listOfCouponsInCategory.size(); storeIndex++) {
                if (couponData.getCategoryId().equalsIgnoreCase(listOfCouponsInCategory.get(storeIndex).getCategoryId())) {
                    return listOfCouponsInCategory.get(storeIndex).getNumberOfCoupons()+"";
                }
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return listOfCategories.size();
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
