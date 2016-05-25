package com.moblyo.market.adapter;

import android.app.Activity;
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
import com.moblyo.market.location.Coordinate;
import com.moblyo.market.model.ListOfCoupons;
import com.moblyo.market.model.ListOfStores;
import com.moblyo.market.model.ResponseGetCoupons;
import com.moblyo.market.utils.SharedPrefKeys;
import com.moblyo.market.utils.SharedPreferenceUtil;
import com.moblyo.market.utils.TypeFaceClass;

import java.util.ArrayList;


public class MyHotDealsSearchRecyclerViewAdapter extends RecyclerView.Adapter<MyHotDealsSearchRecyclerViewAdapter.ViewHolder>
{
    private Context context;
    private Activity activity;
    private TypeFaceClass typeClass;
    private ArrayList<ListOfCoupons> listOfCoupons;
    public ArrayList<ListOfStores> listOfStores;

    public MyHotDealsSearchRecyclerViewAdapter(Context context, ArrayList<ListOfCoupons> data , Activity activity)
    {
        super();
        this.context = context;
        this.activity = activity;
        this.listOfCoupons = data;
        typeClass=new TypeFaceClass(context);
        listOfStores = new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView hot_deals_coupon_image;
        TextView hot_deals_offer_title;
        TextView hot_deals_offer_slogan;
        TextView hot_deals_offer_distance;
        ImageView hot_deals_arrow;

        ViewHolder(View itemView) {
            super(itemView);
            hot_deals_coupon_image = (ImageView)itemView.findViewById(R.id.hot_deals_coupon_image);

            hot_deals_offer_title = (TextView)itemView.findViewById(R.id.hot_deals_offer_title);
            hot_deals_offer_slogan = (TextView)itemView.findViewById(R.id.hot_deals_offer_slogan);
            hot_deals_offer_distance = (TextView)itemView.findViewById(R.id.hot_deals_offer_distance);
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

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
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

            holder.hot_deals_offer_distance.setText(calculateDistanceForCoupon(couponData).replace(",","."));

            Ion.with(context).load(couponData.getSmallImage())
                    .withBitmap()
                    .placeholder(R.drawable.no_image_icon).resize(dp2px(50), dp2px(50))
                    .error(R.drawable.no_image_icon)
                    .intoImageView(holder.hot_deals_coupon_image);
        }
        holder.hot_deals_offer_title.setText(couponData.getOfferTitle());
    }

    private String calculateDistanceForCoupon(ListOfCoupons couponData) {
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance(context);

        ResponseGetCoupons responseGetCoupons = null;
        if(activity instanceof  HomeScreenActivity){
            responseGetCoupons = ((HomeScreenActivity)context).getCouponsData;
        }

        if( responseGetCoupons!= null
                && responseGetCoupons.getListOfStores() != null) {
            ArrayList<ListOfStores> storeList = responseGetCoupons.getListOfStores();
            for (int storeIndex = 0; storeIndex < storeList.size(); storeIndex++) {
                if (couponData.getStoreId().equalsIgnoreCase(storeList.get(storeIndex).getStoreId())) {
                    double lat1 = (double) (storeList.get(storeIndex).getLatitude());
                    double lng1 = (double) (storeList.get(storeIndex).getLongitude());
                    double currentLat = SharedPreferenceUtil.getInstance(context).getData(SharedPrefKeys.CURRENT_LATITUDE, 0f);
                    double currentLng = SharedPreferenceUtil.getInstance(context).getData(SharedPrefKeys.CURRENT_LONGITUDE, 0f);
                    double distance = new Coordinate().distance(
                            lat1,
                            lng1,
                            currentLat,
                            currentLng,
                            Coordinate.Unit.Meter);
                    if(sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Meter.getValue()){
                        if(distance >1000.0) {
                            return String.format("%.2f",(distance / 1000.0))+" km";
                        }else {
                            return couponData.getDistanceToStore() + " m";
                        }
                    }else if(sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Miles.getValue()) {
                        distance = distance / 1000.0;
                        distance = distance / 1.6;
                        if (distance < 1.0 && distance > 0.1) {
                            return String.format("%.2f", distance) + " mi";
                        } else if (distance < 0.1) {
                            return String.format("%.2f", distance * 5280) + " ft";
                        } else {
                            return String.format("%.1f", distance) + " mi";
                        }
                    }
                }
            }
        }

        return "";
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
    
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
