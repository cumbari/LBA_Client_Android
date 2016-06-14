package com.moblyo.market.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.moblyo.market.R;
import com.moblyo.market.model.ListOfCoupons;
import com.moblyo.market.model.ListOfStores;
import com.moblyo.market.utils.TypeFaceClass;

import java.util.ArrayList;


public class MyHotDealsRecyclerViewAdapter extends RecyclerView.Adapter<MyHotDealsRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private Activity activity;
    private TypeFaceClass typeClass;
    private ArrayList<ListOfCoupons> listOfCoupons;
    public ArrayList<ListOfStores> listOfStores;

    public MyHotDealsRecyclerViewAdapter(Context context, ArrayList<ListOfCoupons> data , Activity activity)
    {
        super();
        this.context = context;
        this.activity = activity;
        this.listOfCoupons = data;
        typeClass=new TypeFaceClass(context);
        listOfStores = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout hot_deals_coupon_mainlayout;
        ImageView hot_deals_coupon_image;
        TextView hot_deals_offer_title;
        TextView hot_deals_offer_slogan;
        TextView hot_deals_offer_distance;
        TextView sponsored_label;
        ImageView hot_deals_arrow;

        ViewHolder(View itemView) {
            super(itemView);
            hot_deals_coupon_mainlayout = (RelativeLayout) itemView.findViewById(R.id.hot_deals_coupon_mainlayout);
            hot_deals_coupon_image = (ImageView)itemView.findViewById(R.id.hot_deals_coupon_image);

            hot_deals_offer_title = (TextView)itemView.findViewById(R.id.hot_deals_offer_title);
            hot_deals_offer_slogan = (TextView)itemView.findViewById(R.id.hot_deals_offer_slogan);
            hot_deals_offer_distance = (TextView)itemView.findViewById(R.id.hot_deals_offer_distance);
            sponsored_label = (TextView)itemView.findViewById(R.id.sponsored_label);
            hot_deals_arrow = (ImageView)itemView.findViewById(R.id.hot_deals_arrow);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_hot_deals, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        typeClass.setTypefaceBold(holder.hot_deals_offer_title);
        typeClass.setTypefaceNormal(holder.hot_deals_offer_slogan);
        typeClass.setTypefaceNormal(holder.hot_deals_offer_distance);
        typeClass.setTypefaceNormal(holder.sponsored_label);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        //reset the values
        ListOfCoupons couponData = listOfCoupons.get(position);
        if(couponData!= null && couponData.getCategoryId()!= null && (couponData.getCategoryId().equals("-1000") || couponData.getCategoryId().equals("-2000"))){
            holder.hot_deals_offer_slogan.setVisibility(View.GONE);
            holder.hot_deals_offer_distance.setVisibility(View.GONE);
            holder.hot_deals_coupon_image.setVisibility(View.GONE);
            holder.hot_deals_arrow.setVisibility(View.GONE);

        }else{
            holder.hot_deals_offer_slogan.setVisibility(View.VISIBLE);
            holder.hot_deals_offer_distance.setVisibility(View.VISIBLE);
            holder.hot_deals_coupon_image.setVisibility(View.VISIBLE);
            holder.hot_deals_arrow.setVisibility(View.VISIBLE);
            holder.hot_deals_offer_slogan.setText(couponData.getOfferSlogan());

            try {
                if (!couponData.isSponsored()) {
                    holder.sponsored_label.setVisibility(View.GONE);
                    holder.hot_deals_coupon_mainlayout.setBackgroundResource(0);
                } else {
                    holder.sponsored_label.setVisibility(View.VISIBLE);
                    holder.hot_deals_coupon_mainlayout.setBackgroundResource(R.color.sponsored_coupon_color);
                }
            }catch (OutOfMemoryError outOfMemoryError){

            }catch (Exception e){

            }
            holder.hot_deals_offer_distance.setText(couponData.getDistanceForSort());

            Ion.with(context).load(couponData.getSmallImage())
                    .withBitmap()
                    .placeholder(R.drawable.no_image_icon).resize(dp2px(50), dp2px(50))
                    .error(R.drawable.no_image_icon)
                    .intoImageView(holder.hot_deals_coupon_image);
        }
        holder.hot_deals_offer_title.setText(couponData.getOfferTitle());
    }


    @Override
    public int getItemCount() {
        return listOfCoupons.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  void notifyData() {
        notifyDataSetChanged();
    }

    public void setNewCouponsData(ArrayList<ListOfCoupons> newList ,ArrayList<ListOfStores> newListOfStores ){
        listOfCoupons = newList;

        listOfStores.clear();
        for(int i=0;i<newListOfStores.size();i++){
            listOfStores.add(newListOfStores.get(i));
        }
        notifyData();
    }

    public ListOfCoupons removeItem(int position) {
        final ListOfCoupons model = listOfCoupons.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ListOfCoupons model) {
        listOfCoupons.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ListOfCoupons model = listOfCoupons.remove(fromPosition);
        listOfCoupons.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(ArrayList<ListOfCoupons> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(ArrayList<ListOfCoupons> newModels) {
        for (int i = listOfCoupons.size() - 1; i >= 0; i--) {
            final ListOfCoupons model = listOfCoupons.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<ListOfCoupons> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ListOfCoupons model = newModels.get(i);
            if (!listOfCoupons.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<ListOfCoupons> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ListOfCoupons model = newModels.get(toPosition);
            final int fromPosition = listOfCoupons.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
