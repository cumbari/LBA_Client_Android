package com.moblyo.market;

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
import com.moblyo.market.adapter.MyHotDealsRecyclerViewAdapter;
import com.moblyo.market.interfaces.OnLoadMoreListener;
import com.moblyo.market.location.Coordinate;
import com.moblyo.market.model.ListOfCoupons;
import com.moblyo.market.model.ListOfStores;
import com.moblyo.market.model.ResponseGetCoupons;
import com.moblyo.market.sync.CustomAsyncTask;
import com.moblyo.market.utils.APPConstants;
import com.moblyo.market.utils.RecyclerItemClickListener;
import com.moblyo.market.utils.SharedPrefKeys;
import com.moblyo.market.utils.SortDataTableByDistanceFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CouponsInSelectedCategoryActivity extends BaseActivity
{
    private RecyclerView recyclerView;
    private SearchView searchView;

    private TextView title;
    private ImageView map_icon;
    private String categoryID;
    private String categoryName;

    public ResponseGetCoupons getCouponsData;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons_selected_category_or_brand);
        getDataFromBundle();
        initialiseResources();
        new CustomAsyncTask(CouponsInSelectedCategoryActivity.this, APPConstants.GetSelectedCategoryWebservice,
                categoryName, batchNo,true, new OnLoadMoreListener<String>() {
            @Override
            public void onLoadMore(String resultModel) {
                getCategoriesData(resultModel);
            }
        }).execute();
    }

    private void getCategoriesData(String resultModel) {
        getCouponsData = extractDataFromResponse();
        Gson gson = new Gson();
        try {
            JSONObject onj = new JSONObject(resultModel);
            if (onj != null) {
                ResponseGetCoupons r = gson.fromJson(onj.toString(), ResponseGetCoupons.class);
                if (r != null) {
                    if (getCouponsData == null) {
                        getCouponsData = new ResponseGetCoupons();
                    }

                    if (getCouponsData.getListOfCoupons() == null) {
                        getCouponsData.setListOfCoupons(new ArrayList<ListOfCoupons>());
                    }

                    if (getCouponsData.getListOfStores() == null) {
                        getCouponsData.setListOfStores(new ArrayList<ListOfStores>());
                    }

                    getCouponsData.setListOfCoupons(r.getListOfCoupons());
                    getCouponsData.setListOfStores(r.getListOfStores());
                    getCouponsData.setMaxNumberReached(r.isMaxNumberReached());

                    sharedPreferenceUtil.saveData(SharedPrefKeys.GET_CATEGORIES + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + categoryName, gson.toJson(getCouponsData, ResponseGetCoupons.class));
                }
            }
        } catch (JSONException e) {
        }catch (Exception e) {
        }
        if(getCouponsData!=null) {
            setDataToDisplayInList();
        }else{
            alertMessage.alertBoxForSelectedCategorgyError();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMyHotDealsRecyclerViewAdapter != null){
            getCouponsData = extractDataFromResponse();
            listOfCoupons.clear();
            listOfStores.clear();
            if(getCouponsData!=null) {
                setDataToDisplayInList();
            }
        }
    }

    private void getDataFromBundle() {
        categoryID = getIntent().getStringExtra("categoryID");
        categoryName = getIntent().getStringExtra("categoryName");
    }

    private void initialiseResources() {

        batchNo = 1;
        listOfCoupons = new ArrayList<>();
        listOfStores = new ArrayList<>();

        title = (TextView) findViewById(R.id.activity_header_app_title);
        typeFaceClass.setTypefaceMed(title);
        title.setText(categoryName);

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

                                if (getCouponsData != null && !getCouponsData.isMaxNumberReached()) {
                                    if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount && totalItemCount != 0 && totalItemCount >= visibleThreshold) {
                                       try {
                                           data_load_progress.setVisibility(View.VISIBLE);
                                           batchNo = (totalItemCount / visibleThreshold) + 1;
                                           new CustomAsyncTask(CouponsInSelectedCategoryActivity.this, APPConstants.GetSelectedCategoryWebservice,
                                                   categoryName, batchNo, false, new OnLoadMoreListener<String>() {
                                               @Override
                                               public void onLoadMore(String resultModel) {
                                                   try {
                                                       data_load_progress.setVisibility(View.GONE);
                                                       getCouponsData = extractDataFromResponse();
                                                       if (getCouponsData != null) {
                                                           setDataToDisplayInList();
                                                       }
                                                       mMyHotDealsRecyclerViewAdapter.notifyDataSetChanged();
                                                       visibleThreshold = sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER, 10);
                                                   } catch (Exception e) {

                                                   }
                                                   loading = false;
                                               }
                                           }).execute();
                                           loading = true;
                                       }catch (Exception e){
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
                Intent intent=new Intent(CouponsInSelectedCategoryActivity.this, MapsActivity.class);
                intent.putExtra("fromWhere",SharedPrefKeys.GET_CATEGORIES);
                intent.putExtra("filter",categoryName);
                startActivity(intent);
            }
        });
    }

    private ResponseGetCoupons extractDataFromResponse() {
        String getCouponString = sharedPreferenceUtil.getData(SharedPrefKeys.GET_CATEGORIES + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + categoryName, "");

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
                return gson.fromJson(onj.toString(), ResponseGetCoupons.class);
            }else{
                return null;
            }

        }else{
            return null;
        }
    }

    private void setDataToDisplayInList() {
        try {
            maxNumberReached = getCouponsData.isMaxNumberReached();// if this is false, do not fetch more data

            for (ListOfStores store : getCouponsData.getListOfStores()) {
                listOfStores.add(store);
            }

            for (ListOfCoupons model : getCouponsData.getListOfCoupons()) {
                model.setDistanceForSort(calculateDistanceForCoupon(model).replace(",", "."));
                listOfCoupons.add(model);
            }

            Collections.sort(listOfCoupons, new SortDataTableByDistanceFormatter());


            if (mMyHotDealsRecyclerViewAdapter == null) {
                mMyHotDealsRecyclerViewAdapter = new MyHotDealsRecyclerViewAdapter(CouponsInSelectedCategoryActivity.this, listOfCoupons, CouponsInSelectedCategoryActivity.this);
                mMyHotDealsRecyclerViewAdapter.listOfStores.clear();
                mMyHotDealsRecyclerViewAdapter.listOfStores.addAll(listOfStores);
                recyclerView.setAdapter(mMyHotDealsRecyclerViewAdapter);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(CouponsInSelectedCategoryActivity.this,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                try {
                                    ListOfCoupons item = listOfCoupons.get(position);
                                    Intent moveToDetailing = new Intent(CouponsInSelectedCategoryActivity.this, DetailedCouponActivity.class);
                                    moveToDetailing.putExtra("fromWhere", SharedPrefKeys.GET_CATEGORIES);
                                    moveToDetailing.putExtra("couponID", item.getCouponId());
                                    moveToDetailing.putExtra("distanceToShow", item.getDistanceForSort());
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
        if( getCouponsData!= null
                && getCouponsData.getListOfStores() != null) {
            ArrayList<ListOfStores> storeList = getCouponsData.getListOfStores();
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

}
