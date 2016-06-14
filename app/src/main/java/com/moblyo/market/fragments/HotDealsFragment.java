package com.moblyo.market.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moblyo.market.DetailedCouponActivity;
import com.moblyo.market.HomeScreenActivity;
import com.moblyo.market.MapsActivity;
import com.moblyo.market.R;
import com.moblyo.market.adapter.MyHotDealsRecyclerViewAdapter;
import com.moblyo.market.adapter.MyHotDealsSearchRecyclerViewAdapter;
import com.moblyo.market.interfaces.OnLoadMoreListener;
import com.moblyo.market.location.Coordinate;
import com.moblyo.market.model.ListOfCoupons;
import com.moblyo.market.model.ListOfStores;
import com.moblyo.market.model.ResponseGetCoupons;
import com.moblyo.market.sync.CustomAsyncTask;
import com.moblyo.market.sync.SyncApplicationData;
import com.moblyo.market.utils.APPConstants;
import com.moblyo.market.utils.RecyclerItemClickListener;
import com.moblyo.market.utils.SharedPrefKeys;
import com.moblyo.market.utils.SharedPreferenceUtil;
import com.moblyo.market.utils.SortDataTableByDistanceFormatter;
import com.moblyo.market.utils.TCLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class HotDealsFragment extends ParentFragment implements SearchView.OnQueryTextListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final long TIME_INTERVAL_FOR_SORT = 2*60000;//2 min
    private static final long TIME_INTERVAL_FOR_SYNC = 5000;//5sec check

    /**
     * The desired location distance parameter for location updates.
     */
    private static final long LOCATION_DISTANCE_PARAMETER = 300;


    private int mColumnCount = 1;
    private ArrayList<ListOfCoupons> originalhotDealsArray;
    private ArrayList<ListOfCoupons> hotDealsArray;
    private ArrayList<ListOfCoupons> hotDealsSearchArray;

    private RecyclerView recyclerView;
    private SearchView searchView;

    private MyHotDealsRecyclerViewAdapter mMyHotDealsRecyclerViewAdapter;
    private MyHotDealsSearchRecyclerViewAdapter mMyHotDealsSearchRecyclerViewAdapter;

    private boolean maxNumberReachedForSearch = false;

    private boolean isSearch = false;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 10;
    private int firstVisibleItem, lastVisibleItem, totalItemCount;
    private boolean loading = false;
    private int visibleItemCount;
    private String filterString = "";

    private int valueToDeductFromTotalListItems = 0;
    private ProgressBar data_load_progress;
    private ProgressDialog progressDialog;
    private boolean dataSyncingInProgress = false;

    private Handler handler;
    private Runnable runnable;

    private Handler handlerForSync;
    private Runnable runnableForSync;

    public HotDealsFragment() {
    }

    @SuppressWarnings("unused")
    public static HotDealsFragment newInstance(int columnCount) {
        HotDealsFragment fragment = new HotDealsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    private void startTimerForSortAndSync(){
        removeTimers();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    setDataAgainOnResume();
                    if (handler != null)
                        handler.postDelayed(this, TIME_INTERVAL_FOR_SORT);
                }catch(Exception e){
                }
            }
        };
        handler.postDelayed(runnable, TIME_INTERVAL_FOR_SORT);



        handlerForSync = new Handler();
        runnableForSync = new Runnable() {
            @Override
            public void run() {
                try {

                    double lat = SharedPreferenceUtil.getInstance(mActivity).getData(SharedPrefKeys.PREVIOUS_LATITUDE, 0f);
                    double lng = SharedPreferenceUtil.getInstance(mActivity).getData(SharedPrefKeys.PREVIOUS_LONGITUDE, 0f);

                    double currentLat = SharedPreferenceUtil.getInstance(mActivity).getData(SharedPrefKeys.CURRENT_LATITUDE, 0f);
                    double currentLng = SharedPreferenceUtil.getInstance(mActivity).getData(SharedPrefKeys.CURRENT_LONGITUDE, 0f);

                    double distance = new Coordinate().distance(
                            lat,
                            lng,
                            currentLat,
                            currentLng,
                            Coordinate.Unit.Meter);


                    if (distance >= (LOCATION_DISTANCE_PARAMETER))
                    {
                        SharedPreferenceUtil.getInstance(mActivity).saveData(SharedPrefKeys.PREVIOUS_LATITUDE,(float)currentLat);
                        SharedPreferenceUtil.getInstance(mActivity).saveData(SharedPrefKeys.PREVIOUS_LONGITUDE,(float)currentLng);
                        sortData(false);
                    }

                    if (handlerForSync != null)
                        handlerForSync.postDelayed(this, TIME_INTERVAL_FOR_SYNC);
                }catch(Exception e){
                }
            }
        };
        handlerForSync.postDelayed(runnableForSync, TIME_INTERVAL_FOR_SYNC);

    }

    private void removeTimers() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }

        if (handlerForSync != null) {
            handlerForSync.removeCallbacks(runnableForSync);
            handlerForSync = null;
            runnableForSync = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        removeTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimerForSortAndSync();
        setDataAgainOnResume();
    }

    private void setDataAgainOnResume() {
        try {
            if (isSearch) {
                if (mMyHotDealsSearchRecyclerViewAdapter != null) {
                    ResponseGetCoupons overallResponse = extractDataFromResponse();
                    if (overallResponse == null) {
                        overallResponse = new ResponseGetCoupons();
                    }

                    if (overallResponse.getListOfCoupons() == null) {
                        overallResponse.setListOfCoupons(new ArrayList<ListOfCoupons>());
                    }

                    if (overallResponse.getListOfStores() == null) {
                        overallResponse.setListOfStores(new ArrayList<ListOfStores>());
                    }

                    maxNumberReachedForSearch = overallResponse.isMaxNumberReached();
                    mMyHotDealsSearchRecyclerViewAdapter.setNewCouponsData(overallResponse.getListOfCoupons(), overallResponse.getListOfStores());
                    hotDealsSearchArray = overallResponse.getListOfCoupons();
                }
            } else {
                if (mMyHotDealsRecyclerViewAdapter != null) {
                    setHotDealData();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        Context context = view.getContext();
        dataSyncingInProgress = false;
        recyclerView = (RecyclerView) view.findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        data_load_progress = (ProgressBar) view.findViewById(R.id.data_load_progress);

        searchView = (SearchView) view.findViewById(R.id.data_searchview);
        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                searchView.setIconified(false);
                hotDealsSearchArray.clear();
                mMyHotDealsSearchRecyclerViewAdapter.setNewCouponsData(hotDealsSearchArray, new ArrayList<ListOfStores>());
                recyclerView.setAdapter(mMyHotDealsSearchRecyclerViewAdapter);
                isSearch = true;
                return true;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotDealsSearchArray.clear();
                mMyHotDealsSearchRecyclerViewAdapter.setNewCouponsData(hotDealsSearchArray, new ArrayList<ListOfStores>());
                recyclerView.setAdapter(mMyHotDealsSearchRecyclerViewAdapter);
                isSearch = true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setAdapter(mMyHotDealsRecyclerViewAdapter);
                isSearch = false;
                filterString = "";
                return false;
            }
        });
        searchView.setOnQueryTextListener(this);

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
            if (searchText != null) {
                searchText.setTextColor(Color.BLACK);
                typeFace.setTypefaceMed(searchText);
            }
        }

        hotDealsArray = new ArrayList<>();
        originalhotDealsArray = new ArrayList<>();
        hotDealsSearchArray = new ArrayList<>();

        setHotDealData();

        mMyHotDealsRecyclerViewAdapter = new MyHotDealsRecyclerViewAdapter(getActivity(), hotDealsArray, mActivity);
        if (((HomeScreenActivity) mActivity).getCouponsData != null
                && ((HomeScreenActivity) mActivity).getCouponsData.getListOfStores() != null) {
            for (ListOfStores model : ((HomeScreenActivity) mActivity).getCouponsData.getListOfStores()) {
                mMyHotDealsRecyclerViewAdapter.listOfStores.add(model);
            }
        }

        mMyHotDealsSearchRecyclerViewAdapter = new MyHotDealsSearchRecyclerViewAdapter(getActivity(), hotDealsSearchArray, mActivity);

        recyclerView.setAdapter(mMyHotDealsRecyclerViewAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    ListOfCoupons item = null;
                    if (isSearch) {
                        item = hotDealsSearchArray.get(position);
                    } else {
                        item = hotDealsArray.get(position);
                    }

                    if (item.getCategoryId() != null && (item.getCategoryId().equals("-1000") || item.getCategoryId().equals("-2000"))) {
                        //Do nothing
                    } else {
                        Intent moveToDetailing = new Intent(mActivity, DetailedCouponActivity.class);
                        moveToDetailing.putExtra("fromWhere", SharedPrefKeys.GET_COUPONS);
                        moveToDetailing.putExtra("couponID", item.getCouponId());
                        moveToDetailing.putExtra("filter", filterString);
                        moveToDetailing.putExtra("distanceToShow", item.getDistanceForSort());
                        mActivity.startActivity(moveToDetailing);
                    }
                }catch (Exception e){

                }
            }
        }));

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            try {
                                if (dy > 0) {

                                    visibleItemCount = linearLayoutManager.getChildCount();
                                    totalItemCount = linearLayoutManager.getItemCount();
                                    int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                                    //condition for searching list
                                    if (isSearch && !maxNumberReachedForSearch) {
                                        if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount && totalItemCount != 0 && totalItemCount >= visibleThreshold) {
                                            data_load_progress.setVisibility(View.VISIBLE);
                                            int batchNo = (totalItemCount/visibleThreshold) + 1;
                                            syncCouponDataWhileSearching(batchNo,false);
                                            loading = true;
                                        }
                                    } else if (((HomeScreenActivity) mActivity).getCouponsData != null && !((HomeScreenActivity) mActivity).getCouponsData.isMaxNumberReached()) {
                                        if (!loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount && totalItemCount != 0 && totalItemCount >= visibleThreshold) {
                                            data_load_progress.setVisibility(View.VISIBLE);
                                            int batchNo = (totalItemCount/visibleThreshold) + 1;
                                            TCLogger.writeToCumbariLogFile(getActivity(), "HotDeals > onScroll", " Batch- "+batchNo);
                                            syncCouponData(batchNo,false);
                                            loading = true;
                                        }
                                    }
                                }
                            }catch (Exception e){
                                try {
                                    TCLogger.writeToCumbariLogFile(getActivity(), "Exception Scroll ", "Exception :" + e.getMessage());
                                }catch (Exception e1){

                                }
                            }
                        }
                    });
        }

        return view;
    }

    private void syncCouponDataWhileSearching(int batchNo, boolean showProgress) {
        new CustomAsyncTask(getActivity(), APPConstants.FindCouponsWebservice, filterString, batchNo,showProgress, new OnLoadMoreListener<String>() {
            @Override
            public void onLoadMore(String resultModel) {
                dataSyncingInProgress = false;
                try {
                    data_load_progress.setVisibility(View.GONE);
                    setSearchData(resultModel);
                    visibleThreshold = sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER, 10);
                } catch (Exception e) {
                }
                loading = false;
            }
        }).execute();
    }

    private void syncCouponData(int batchNo, boolean showProgress) {
        new CustomAsyncTask(getActivity(), APPConstants.GetCouponsWebservice, "", batchNo,showProgress, new OnLoadMoreListener<String>() {
            @Override
            public void onLoadMore(String resultModel) {
                try {
                    data_load_progress.setVisibility(View.GONE);

                    if(extractDataFromLocal(resultModel)) {//set new data in share pref and extact array list from that
                        setHotDealData();//again set hot deals array list and then notify
                        mMyHotDealsRecyclerViewAdapter.notifyDataSetChanged();
                        visibleThreshold = sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER, 10);
                    }

                } catch (Exception e) {
                }
                loading = false;
            }
        }).execute();
    }

    private void setHotDealData() {
        try {
            if (((HomeScreenActivity) mActivity).getCouponsData != null
                    && ((HomeScreenActivity) mActivity).getCouponsData.getListOfCoupons() != null) {

                ArrayList<ListOfCoupons> sponsoredArray = new ArrayList<>();
                ArrayList<ListOfCoupons> unsponsoredArray = new ArrayList<>();
                ArrayList<ListOfCoupons> advertiseArray = new ArrayList<>();

                valueToDeductFromTotalListItems = 0;

                hotDealsArray.clear();
                originalhotDealsArray.clear();

                for (ListOfCoupons model : ((HomeScreenActivity) mActivity).getCouponsData.getListOfCoupons()) {
                    hotDealsArray.add(model);
                }

                for (int i = 0; i < hotDealsArray.size(); i++) {
                    if (hotDealsArray.get(i).getOfferType().equals("ADVERTISE")) {
                        hotDealsArray.get(i).setDistanceForSort(calculateDistanceForCoupon(hotDealsArray.get(i)).replace(",","."));
                        advertiseArray.add(hotDealsArray.get(i));
                    } else if (hotDealsArray.get(i).isSponsored()) {
                        hotDealsArray.get(i).setDistanceForSort(calculateDistanceForCoupon(hotDealsArray.get(i)).replace(",","."));
                        sponsoredArray.add(hotDealsArray.get(i));
                    } else {
                        hotDealsArray.get(i).setDistanceForSort(calculateDistanceForCoupon(hotDealsArray.get(i)).replace(",","."));
                        unsponsoredArray.add(hotDealsArray.get(i));
                    }
                }

                hotDealsArray.clear();
                if (sponsoredArray.size() > 0) {
                    Collections.sort(sponsoredArray, new SortDataTableByDistanceFormatter());
                    /*++valueToDeductFromTotalListItems;
                    ListOfCoupons listOfCoupons = new ListOfCoupons();
                    listOfCoupons.setOfferTitle(getResources().getString(R.string.sponsored));
                    listOfCoupons.setCategoryId("-1000");
                    hotDealsArray.add(listOfCoupons);*/
                    hotDealsArray.addAll(sponsoredArray);
                }

                if (unsponsoredArray.size() > 0) {
                    Collections.sort(unsponsoredArray, new SortDataTableByDistanceFormatter());
                   /* ++valueToDeductFromTotalListItems;
                    ListOfCoupons listOfCoupons = new ListOfCoupons();
                    listOfCoupons.setOfferTitle(getResources().getString(R.string.unSponsored));
                    listOfCoupons.setCategoryId("-2000");
                    hotDealsArray.add(listOfCoupons);*/
                    hotDealsArray.addAll(unsponsoredArray);
                }

                if (advertiseArray.size() > 0) {
                    Collections.sort(advertiseArray, new SortDataTableByDistanceFormatter());
                    hotDealsArray.addAll(advertiseArray);
                }

                if (mMyHotDealsRecyclerViewAdapter != null) {
                    mMyHotDealsRecyclerViewAdapter.setNewCouponsData(hotDealsArray, ((HomeScreenActivity) mActivity).getCouponsData.getListOfStores());
                }

                for (ListOfCoupons model : hotDealsArray) {
                    originalhotDealsArray.add(model);
                }
            }
        }catch (Exception e){
        }
    }

    private String calculateDistanceForCoupon(ListOfCoupons couponData) {
        ResponseGetCoupons responseGetCoupons = ((HomeScreenActivity) mActivity).getCouponsData;
        if (responseGetCoupons != null
                && responseGetCoupons.getListOfStores() != null) {
            ArrayList<ListOfStores> storeList = responseGetCoupons.getListOfStores();
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
        }

        return "";
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        filterString = s;
        //To avoid having a used coupon, when comes back from Detailed description
        sharedPreferenceUtil.saveData(SharedPrefKeys.GET_COUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + filterString, "");
       try {
           new CustomAsyncTask(getActivity(), APPConstants.FindCouponsWebservice, filterString, 1, true, new OnLoadMoreListener<String>() {
               @Override
               public void onLoadMore(String resultModel) {
                   if (resultModel != null) {
                       //here we have to add fetched items in current array list
                       setSearchData(resultModel);
                   }
               }
           }).execute();
       }catch (Exception e){
       }
        return true;
    }

    private void setSearchData(String resultModel) {
        hotDealsSearchArray.clear();

        Gson gson = new Gson();
        try {
            JSONObject onj = new JSONObject(resultModel);
            if (onj != null) {
                ResponseGetCoupons r = gson.fromJson(onj.toString(), ResponseGetCoupons.class);
                if (r != null) {
                    ResponseGetCoupons overallResponse = extractDataFromResponse();
                    if (overallResponse == null) {
                        overallResponse = new ResponseGetCoupons();
                    }

                    if (overallResponse.getListOfCoupons() == null) {
                        overallResponse.setListOfCoupons(new ArrayList<ListOfCoupons>());
                    }

                    if (overallResponse.getListOfStores() == null) {
                        overallResponse.setListOfStores(new ArrayList<ListOfStores>());
                    }

                    overallResponse.getListOfCoupons().addAll(r.getListOfCoupons());
                    overallResponse.getListOfStores().addAll(r.getListOfStores());
                    overallResponse.setMaxNumberReached(r.isMaxNumberReached());

                    sharedPreferenceUtil.saveData(SharedPrefKeys.GET_COUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + filterString, gson.toJson(overallResponse, ResponseGetCoupons.class));

                    maxNumberReachedForSearch = r.isMaxNumberReached();
                    mMyHotDealsSearchRecyclerViewAdapter.setNewCouponsData(overallResponse.getListOfCoupons(), overallResponse.getListOfStores());
                    hotDealsSearchArray = overallResponse.getListOfCoupons();
                }
            }
        } catch (JSONException e) {
        }catch (Exception e) {
        }
    }

    private ResponseGetCoupons extractDataFromResponse() {
        String getCouponString = sharedPreferenceUtil.getData(SharedPrefKeys.GET_COUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + filterString, "");

        Gson gson = new Gson();
        JSONObject onj = null;
        if (getCouponString != null) {
            try {
                onj = new JSONObject(getCouponString);
            } catch (JSONException e) {
                return null;
            }

            if (onj != null) {
                return gson.fromJson(onj.toString(), ResponseGetCoupons.class);
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return true;
    }

    @Override
    public void isEditEnabledPassToFragment(boolean isEnabled) {

    }

    @Override
    public void isMapSelectedToFragment(int mapSelectedIndex) {
        if (mapSelectedIndex == 0) {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtra("fromWhere", SharedPrefKeys.GET_COUPONS);
            intent.putExtra("filter", filterString);
            startActivity(intent);
        }
    }

    @Override
    public void sortData(boolean sortData) {
        if(!sortData){
            if (isSearch) {
                if(!dataSyncingInProgress) {
                    dataSyncingInProgress = true;
                    syncCouponDataWhileSearching(1, true);
                }
            } else {
                if(!dataSyncingInProgress) {
                    dataSyncingInProgress = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog = ProgressDialog.show(mActivity, "", getResources().getString(R.string.load_data_message), false);
                                    progressDialog.setCancelable(false);
                                }
                            });
                            new SyncApplicationData(sharedPreferenceUtil).syncData();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dataSyncingInProgress = false;
                                    ((HomeScreenActivity) mActivity).extractDataFromLocal();
                                    setHotDealData();//again set hot deals array list and then notify
                                    mMyHotDealsRecyclerViewAdapter.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }).start();
                }
            }
        }
    }

    private boolean extractDataFromLocal(String resultModel) {
        try {
            Gson gson = new Gson();
            JSONObject onj = null;
            if (resultModel != null) {
                try {
                    onj = new JSONObject(resultModel);
                } catch (JSONException e) {
                }
                if (onj != null) {
                    ResponseGetCoupons getCouponsData = gson.fromJson(onj.toString(), ResponseGetCoupons.class);
                    if (getCouponsData == null) {
                        getCouponsData = new ResponseGetCoupons();
                    }

                    if (getCouponsData.getListOfCoupons() == null) {
                        getCouponsData.setListOfCoupons(new ArrayList<ListOfCoupons>());
                    }

                    if (getCouponsData.getListOfStores() == null) {
                        getCouponsData.setListOfStores(new ArrayList<ListOfStores>());
                    }

                    if(mActivity!=null &&
                            mActivity instanceof HomeScreenActivity) {
                        if (((HomeScreenActivity) mActivity).getCouponsData != null
                                && ((HomeScreenActivity) mActivity).getCouponsData.getListOfCoupons() != null) {
                            ((HomeScreenActivity) mActivity).getCouponsData.getListOfCoupons().addAll(getCouponsData.getListOfCoupons());
                            ((HomeScreenActivity) mActivity).getCouponsData.getListOfStores().addAll(getCouponsData.getListOfStores());

                            ((HomeScreenActivity) mActivity).getCouponsData.getListOfBrandHits().addAll(getCouponsData.getListOfBrandHits());
                            ((HomeScreenActivity) mActivity).getCouponsData.getListOfCategoryHits().addAll(getCouponsData.getListOfCategoryHits());
                            ((HomeScreenActivity) mActivity).getCouponsData.setMaxNumberReached(getCouponsData.isMaxNumberReached());

                            sharedPreferenceUtil.saveData(SharedPrefKeys.GET_COUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG"), gson.toJson(((HomeScreenActivity) mActivity).getCouponsData, ResponseGetCoupons.class));
                            return true;
                        }
                    }
                }
            }
        }catch (Exception e){
        }

        return false;
    }

}
