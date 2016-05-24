package com.shephertz.cumbari.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.shephertz.cumbari.R;
import com.shephertz.cumbari.model.ListOfCoupons;
import com.shephertz.cumbari.model.ListOfStores;
import com.shephertz.cumbari.utils.TypeFaceClass;

import java.util.ArrayList;


public class FavoritiesRecyclerViewAdapter extends RecyclerView.Adapter<FavoritiesRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private TypeFaceClass typeClass;
    private ArrayList<ListOfCoupons> listOfCoupons;
    private ArrayList<ListOfStores> listOfStores;

    public boolean isEditEnabled;

    public FavoritiesRecyclerViewAdapter(Context context, ArrayList<ListOfCoupons> data,ArrayList<ListOfStores> storeData)
    {
        super();
        this.context = context;
        this.listOfCoupons = data;
        this.listOfStores = storeData;
        isEditEnabled = false;
        typeClass=new TypeFaceClass(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView hot_deals_coupon_image;
        TextView hot_deals_offer_title;
        TextView hot_deals_offer_slogan;
        TextView hot_deals_offer_distance;
        ImageView delete_favorite;

        ViewHolder(View itemView) {
            super(itemView);
            hot_deals_coupon_image = (ImageView)itemView.findViewById(R.id.hot_deals_coupon_image);

            hot_deals_offer_title = (TextView)itemView.findViewById(R.id.hot_deals_offer_title);
            hot_deals_offer_slogan = (TextView)itemView.findViewById(R.id.hot_deals_offer_slogan);
            hot_deals_offer_distance = (TextView)itemView.findViewById(R.id.hot_deals_offer_distance);
            delete_favorite = (ImageView)itemView.findViewById(R.id.delete_favorite);
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
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        ListOfCoupons couponData = listOfCoupons.get(position);
        holder.hot_deals_offer_title.setText(couponData.getOfferTitle());
        holder.hot_deals_offer_slogan.setText(couponData.getOfferSlogan());
        holder.hot_deals_offer_distance.setText(couponData.getDistanceForSort());
        Ion.with(context).load(couponData.getSmallImage())
                .withBitmap()
                .placeholder(R.drawable.no_image_icon).resize(dp2px(50), dp2px(50))
                .error(R.drawable.no_image_icon)
                .intoImageView(holder.hot_deals_coupon_image);

        if(isEditEnabled){
            holder.delete_favorite.setVisibility(View.VISIBLE);
            holder.delete_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Since the Coupons array is sorted, we cannot directly remove the same index element from ListOfStores as well
                    for(int i=0;i<listOfStores.size();i++){
                        if(listOfCoupons.get(position).getStoreId().equals(listOfStores.get(i).getStoreId())){
                            listOfStores.remove(i);
                            break;
                        }
                    }
                    listOfCoupons.remove(position);
                    notifyData();
                }
            });
        }else{
            holder.delete_favorite.setVisibility(View.GONE);
        }
    }

    public ArrayList<ListOfCoupons> getCouponsList(){
        return  listOfCoupons;
    }

    public ArrayList<ListOfStores> getStoresList(){
        return  listOfStores;
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
