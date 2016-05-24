package com.shephertz.cumbari;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shephertz.cumbari.adapter.MyHotDealsRecyclerViewAdapter;
import com.shephertz.cumbari.location.Coordinate;
import com.shephertz.cumbari.model.ListOfCoupons;
import com.shephertz.cumbari.model.ListOfStores;
import com.shephertz.cumbari.model.ResponseGetCoupons;
import com.shephertz.cumbari.utils.RecyclerItemClickListener;
import com.shephertz.cumbari.utils.SharedPrefKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoreDealsActivity extends BaseActivity
{
    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private SearchView searchView;

    private TextView title;
    private ImageView map_icon;

    private String fromWhere;
    private String storeId;

    public ResponseGetCoupons getCouponsData;
    private ArrayList<ListOfCoupons> listOfOffersFromStore ;
    private ArrayList<ListOfStores> listOfStores ;

    private MyHotDealsRecyclerViewAdapter mMyHotDealsRecyclerViewAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_selected_category_or_brand);
        getDataFromBundle();
        extractDataFromLocal();
        initialiseResources();
        setDataToDisplayInList();
    }

    private void getDataFromBundle() {
        fromWhere = getIntent().getStringExtra("fromWhere");
        storeId = getIntent().getStringExtra("storeId");
    }

    private void extractDataFromLocal() {
        Gson gson = new Gson();
        JSONObject onj = null;
        String getCouponString = sharedPreferenceUtil.getData(SharedPrefKeys.GET_COUPONS +
                sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG"), "");
        if(getCouponString!=null)
        {
            try{
                onj = new JSONObject(getCouponString);
            }catch (JSONException e) {
            }
            if(onj != null) {
                getCouponsData = gson.fromJson(onj.toString(), ResponseGetCoupons.class);
            }
        }
    }

    private void initialiseResources() {

        listOfOffersFromStore = new ArrayList<>();
        listOfStores = new ArrayList<>();

        title = (TextView) findViewById(R.id.activity_header_app_title);
        typeFaceClass.setTypefaceMed(title);
        title.setText(getResources().getString(R.string.more_deals));

        map_icon = (ImageView) findViewById(R.id.activity_header_map_icon);
        map_icon.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        searchView = (SearchView) findViewById(R.id.data_searchview);
        searchView.setVisibility(View.GONE);

    }

    private void setDataToDisplayInList() {

        if(getCouponsData != null){
            for(ListOfStores store: getCouponsData.getListOfStores()){
                if(store.getStoreId().equals(storeId)){
                    listOfStores.add(store);
                }
            }

            for(ListOfCoupons coupons: getCouponsData.getListOfCoupons()){
                if(coupons.getStoreId().equals(storeId)){
                    coupons.setDistanceForSort(calculateDistanceForCoupon(coupons).replace(",","."));
                    listOfOffersFromStore.add(coupons);
                }
            }
        }

        mMyHotDealsRecyclerViewAdapter = new MyHotDealsRecyclerViewAdapter(MoreDealsActivity.this, listOfOffersFromStore, MoreDealsActivity.this);
        mMyHotDealsRecyclerViewAdapter.listOfStores.addAll(listOfStores);
        recyclerView.setAdapter(mMyHotDealsRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(MoreDealsActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            ListOfCoupons item = listOfOffersFromStore.get(position);
                            //Open Detailing view of coupon
                            Intent moveToDetailing = new Intent(MoreDealsActivity.this, DetailedCouponActivity.class);
                            moveToDetailing.putExtra("fromWhere", fromWhere);
                            moveToDetailing.putExtra("couponID", item.getCouponId());
                            moveToDetailing.putExtra("distanceToShow", item.getDistanceForSort());
                            startActivity(moveToDetailing);
                        }catch (Exception e){

                        }
                    }
                }));
    }

    private String calculateDistanceForCoupon(ListOfCoupons couponData) {

            for (int storeIndex = 0; storeIndex < listOfStores.size(); storeIndex++) {
                if (couponData.getStoreId().equalsIgnoreCase(listOfStores.get(storeIndex).getStoreId())) {
                    double lat1 = (double) (listOfStores.get(storeIndex).getLatitude());
                    double lng1 = (double) (listOfStores.get(storeIndex).getLongitude());
                    double currentLat = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LATITUDE, 0f);
                    double currentLng = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LONGITUDE, 0f);

                    double distance = new Coordinate().distance(
                            lat1,
                            lng1,
                            currentLat,
                            currentLng,
                            Coordinate.Unit.Meter);
                    if (sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Meter.getValue()) {
                        if (distance > 1000.0) {
                            return String.format("%.2f", (distance / 1000.0)) + " km";
                        } else {
                            return String.format("%.2f", distance) + " m";
                        }
                    } else if (sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Miles.getValue()) {

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

        return "";
    }
}
