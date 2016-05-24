package com.shephertz.cumbari;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shephertz.cumbari.adapter.MyHotDealsRecyclerViewAdapter;
import com.shephertz.cumbari.interfaces.OnLoadMoreListener;
import com.shephertz.cumbari.location.Coordinate;
import com.shephertz.cumbari.model.ListOfCoupons;
import com.shephertz.cumbari.model.ListOfStores;
import com.shephertz.cumbari.model.ResponseGetBrandedCoupons;
import com.shephertz.cumbari.sync.CustomAsyncTask;
import com.shephertz.cumbari.utils.APPConstants;
import com.shephertz.cumbari.utils.RecyclerItemClickListener;
import com.shephertz.cumbari.utils.SharedPrefKeys;
import com.shephertz.cumbari.utils.SortDataTableByDistanceFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CouponsInSelectedBrandsActivity extends BaseActivity
{
    private RecyclerView recyclerView;
    private SearchView searchView;

    private TextView title;
    private ImageView map_icon;
    private String brandName;

    public ResponseGetBrandedCoupons getBrandedCouponsData;
    private ArrayList<ListOfCoupons> listOfCoupons ;
    private ArrayList<ListOfStores> listOfStores ;

    private boolean maxNumberReached = false;
    private MyHotDealsRecyclerViewAdapter mMyHotDealsRecyclerViewAdapter = null;

    private int visibleThreshold = 10;
    private int totalItemCount;
    private boolean loading=false;
    private int visibleItemCount;
    private int batchNo = 1;

    private ProgressBar data_load_progress;
    private boolean callBackHomeScreen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_selected_category_or_brand);
        getDataFromBundle();
        initialiseResources();
        new CustomAsyncTask(CouponsInSelectedBrandsActivity.this, APPConstants.GetSelectedBrandWebservice,
                brandName, 1,true, new OnLoadMoreListener<String>() {
            @Override
            public void onLoadMore(String resultModel) {
                getCategoriesData(resultModel);
            }
        }).execute();
    }

    private void getCategoriesData(String resultModel) {
        getBrandedCouponsData = extractDataFromResponse();
        Gson gson = new Gson();
        try {
            JSONObject onj = new JSONObject(resultModel);
            if (onj != null) {
                ResponseGetBrandedCoupons r = gson.fromJson(onj.toString(), ResponseGetBrandedCoupons.class);
                if (r != null) {
                    if (getBrandedCouponsData == null) {
                        getBrandedCouponsData = new ResponseGetBrandedCoupons();
                    }

                    if (getBrandedCouponsData.getListOfCoupons() == null) {
                        getBrandedCouponsData.setListOfCoupons(new ArrayList<ListOfCoupons>());
                    }

                    if (getBrandedCouponsData.getListOfStores() == null) {
                        getBrandedCouponsData.setListOfStores(new ArrayList<ListOfStores>());
                    }

                    getBrandedCouponsData.setListOfCoupons(r.getListOfCoupons());
                    getBrandedCouponsData.setListOfStores(r.getListOfStores());
                    getBrandedCouponsData.setMaxNumberReached(r.isMaxNumberReached());

                    sharedPreferenceUtil.saveData(SharedPrefKeys.GET_BRANDEDCOUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + brandName, gson.toJson(getBrandedCouponsData, ResponseGetBrandedCoupons.class));
                }
            }
        } catch (JSONException e) {
        }catch (Exception e) {
        }

        if(getBrandedCouponsData!=null) {
            setDataToDisplayInList();
        }else{
            alertMessage.alertBoxForSelectedBrandsError();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMyHotDealsRecyclerViewAdapter != null){
            getBrandedCouponsData = extractDataFromResponse();
            if(getBrandedCouponsData!=null) {
                listOfCoupons.clear();
                listOfStores.clear();

                setDataToDisplayInList();
            }
        }
    }

    private void getDataFromBundle() {
        brandName = getIntent().getStringExtra("brandName");
        callBackHomeScreen = getIntent().getBooleanExtra("callBackHomeScreen",false);
    }

    private void initialiseResources() {

        listOfCoupons = new ArrayList<>();
        listOfStores = new ArrayList<>();

        title = (TextView) findViewById(R.id.activity_header_app_title);
        typeFaceClass.setTypefaceMed(title);
        title.setText(brandName);

        map_icon = (ImageView) findViewById(R.id.activity_header_map_icon);
        data_load_progress = (ProgressBar) findViewById(R.id.data_load_progress);

        recyclerView = (RecyclerView) findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            if (dy > 0) {
                                visibleItemCount = linearLayoutManager.getChildCount();
                                totalItemCount = linearLayoutManager.getItemCount();
                                int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                                if (getBrandedCouponsData != null && !getBrandedCouponsData.isMaxNumberReached()) {
                                    if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount && totalItemCount != 0 && totalItemCount >= visibleThreshold) {
                                        try {
                                            data_load_progress.setVisibility(View.VISIBLE);
                                            batchNo = (totalItemCount / visibleThreshold) + 1;
                                            new CustomAsyncTask(CouponsInSelectedBrandsActivity.this, APPConstants.GetSelectedBrandWebservice,
                                                    brandName, batchNo, false, new OnLoadMoreListener<String>() {
                                                @Override
                                                public void onLoadMore(String resultModel) {
                                                    data_load_progress.setVisibility(View.GONE);
                                                    getBrandedCouponsData = extractDataFromResponse();
                                                    if (getBrandedCouponsData != null) {
                                                        setDataToDisplayInList();
                                                    }
                                                    mMyHotDealsRecyclerViewAdapter.notifyDataSetChanged();
                                                    visibleThreshold = sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER, 10);
                                                    loading = false;
                                                }
                                            }).execute();
                                            loading = true;
                                        }catch(Exception e){
                                        }
                                    }
                                }
                            }
                        }
                    });
        }

        searchView = (SearchView) findViewById(R.id.data_searchview);
        searchView.setVisibility(View.GONE);

        setOnClickListeners();
    }

    private void setOnClickListeners() {

        map_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pass listOfStores
                Intent intent=new Intent(CouponsInSelectedBrandsActivity.this, MapsActivity.class);
                intent.putExtra("fromWhere",SharedPrefKeys.GET_BRANDEDCOUPONS);
                intent.putExtra("filter",brandName);
                startActivity(intent);
            }
        });
    }

    private ResponseGetBrandedCoupons extractDataFromResponse() {
        String getCouponString = sharedPreferenceUtil.getData(SharedPrefKeys.GET_BRANDEDCOUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + brandName, "");

        Gson gson = new Gson();
        JSONObject onj = null;
        if(getCouponString!=null)
        {
            try{
                onj = new JSONObject(getCouponString);
            }catch (JSONException e) {
                return null;
            }

            if(onj != null) {
                return gson.fromJson(onj.toString(), ResponseGetBrandedCoupons.class);
            }else{
                return null;
            }

        }else{
            return null;
        }
    }

    private void setDataToDisplayInList() {
        try {
            maxNumberReached = getBrandedCouponsData.isMaxNumberReached();// if this is false, do not fetch more data

            for (ListOfStores store : getBrandedCouponsData.getListOfStores()) {
                listOfStores.add(store);
            }

            for (ListOfCoupons model : getBrandedCouponsData.getListOfCoupons()) {
                model.setDistanceForSort(calculateDistanceForCoupon(model).replace(",", "."));
                listOfCoupons.add(model);
            }

            Collections.sort(listOfCoupons, new SortDataTableByDistanceFormatter());

            if (mMyHotDealsRecyclerViewAdapter == null) {
                mMyHotDealsRecyclerViewAdapter = new MyHotDealsRecyclerViewAdapter(CouponsInSelectedBrandsActivity.this, listOfCoupons, CouponsInSelectedBrandsActivity.this);
                mMyHotDealsRecyclerViewAdapter.listOfStores.clear();
                mMyHotDealsRecyclerViewAdapter.listOfStores.addAll(listOfStores);
                recyclerView.setAdapter(mMyHotDealsRecyclerViewAdapter);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(CouponsInSelectedBrandsActivity.this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                try {
                                    ListOfCoupons item = listOfCoupons.get(position);
                                    Intent moveToDetailing = new Intent(CouponsInSelectedBrandsActivity.this, DetailedCouponActivity.class);
                                    moveToDetailing.putExtra("fromWhere", SharedPrefKeys.GET_BRANDEDCOUPONS);
                                    moveToDetailing.putExtra("couponID", item.getCouponId());
                                    startActivity(moveToDetailing);

//                                    listOfCoupons.clear();
//                                    listOfStores.clear();
//                                    mMyHotDealsRecyclerViewAdapter.setNewCouponsData(listOfCoupons, listOfStores);
                                }catch (Exception e){

                                }
                            }
                        }));
            } else {
                mMyHotDealsRecyclerViewAdapter.setNewCouponsData(listOfCoupons, listOfStores);
            }
        }catch (Exception e){
        }
    }

    private String calculateDistanceForCoupon(ListOfCoupons couponData) {
        if( getBrandedCouponsData!= null
                && getBrandedCouponsData.getListOfStores() != null) {
            ArrayList<ListOfStores> storeList = getBrandedCouponsData.getListOfStores();
            for (int storeIndex = 0; storeIndex < storeList.size(); storeIndex++) {
                if (couponData.getStoreId().equalsIgnoreCase(storeList.get(storeIndex).getStoreId())) {
                    double lat1 = (double) (storeList.get(storeIndex).getLatitude());
                    double lng1 = (double) (storeList.get(storeIndex).getLongitude());
                    double currentLat = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LATITUDE, 0f);
                    double currentLng = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LONGITUDE, 0f);

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
                            return String.format("%.2f", distance) + " m";
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
    public void onBackPressed() {
        if(!callBackHomeScreen) {
            super.onBackPressed();
        }else{
            Intent moveToHomeScreen = new Intent(CouponsInSelectedBrandsActivity.this,HomeScreenActivity.class);
            moveToHomeScreen.putExtra("tabIndex",2);
            startActivity(moveToHomeScreen);
            finish();
        }
    }
}
