package com.shephertz.cumbari.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.shephertz.cumbari.DetailedCouponActivity;
import com.shephertz.cumbari.HomeScreenActivity;
import com.shephertz.cumbari.MapsActivity;
import com.shephertz.cumbari.R;
import com.shephertz.cumbari.adapter.FavoritiesRecyclerViewAdapter;
import com.shephertz.cumbari.location.Coordinate;
import com.shephertz.cumbari.model.FavouritesModel;
import com.shephertz.cumbari.model.ListOfCoupons;
import com.shephertz.cumbari.model.ListOfStores;
import com.shephertz.cumbari.model.ResponseGetCoupons;
import com.shephertz.cumbari.utils.RecyclerItemClickListener;
import com.shephertz.cumbari.utils.SharedPrefKeys;
import com.shephertz.cumbari.utils.SortDataTableByDistanceFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class FavoritesFragment extends ParentFragment {

    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private ArrayList<ListOfCoupons> favoritesArray;
    private ArrayList<ListOfStores> listOfStores;


    private RecyclerView recyclerView;
    private FavoritiesRecyclerViewAdapter mFavoritiesRecyclerViewAdapter;

    @SuppressWarnings("unused")
    public static FavoritesFragment newInstance(int columnCount) {
        FavoritesFragment fragment = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mFavoritiesRecyclerViewAdapter != null) {
            getAdapterData();
            mFavoritiesRecyclerViewAdapter = new FavoritiesRecyclerViewAdapter(getActivity(),favoritesArray,listOfStores);
            recyclerView.setAdapter(mFavoritiesRecyclerViewAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        Context context = view.getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.data_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        SearchView searchView = (SearchView) view.findViewById(R.id.data_searchview);
       searchView.setVisibility(View.GONE);

        getAdapterData();

        mFavoritiesRecyclerViewAdapter = new FavoritiesRecyclerViewAdapter(getActivity(),favoritesArray,listOfStores);
        recyclerView.setAdapter(mFavoritiesRecyclerViewAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(!mFavoritiesRecyclerViewAdapter.isEditEnabled) {
                    ListOfCoupons item = mFavoritiesRecyclerViewAdapter.getCouponsList().get(position);
                    //Open Detailing view of coupon
                    Intent moveToDetailing = new Intent(mActivity, DetailedCouponActivity.class);
                    moveToDetailing.putExtra("fromWhere", SharedPrefKeys.GET_COUPONS);
                    moveToDetailing.putExtra("couponID", item.getCouponId());
                    moveToDetailing.putExtra("distanceToShow", item.getDistanceForSort());
                    mActivity.startActivity(moveToDetailing);
                }else{
                    //Since the Coupons array is sorted, we cannot directly remove the same index element from ListOfStores as well
                    for(int i=0;i<mFavoritiesRecyclerViewAdapter.getStoresList().size();i++){
                        if(mFavoritiesRecyclerViewAdapter.getCouponsList().get(position).getStoreId().equals(mFavoritiesRecyclerViewAdapter.getStoresList().get(i).getStoreId())){
                            mFavoritiesRecyclerViewAdapter.getStoresList().remove(i);
                            break;
                        }
                    }
                    mFavoritiesRecyclerViewAdapter.getCouponsList().remove(position);
                    mFavoritiesRecyclerViewAdapter.notifyData();
                }
            }
        }));
        return view;
    }

    private void getAdapterData() {
        try {
            favoritesArray = new ArrayList<ListOfCoupons>();
            listOfStores = new ArrayList<>();

            JSONObject onj = null;
            Gson gson = new Gson();
            if (sharedPreferenceUtil.getData(SharedPrefKeys.FAVOURITIES_ADDED, "").length() > 0) {
                try {
                    onj = new JSONObject(sharedPreferenceUtil.getData(SharedPrefKeys.FAVOURITIES_ADDED, ""));
                } catch (JSONException e) {
                }
                if (onj != null) {
                    FavouritesModel favouritesList = gson.fromJson(onj.toString(), FavouritesModel.class);
                    if (favouritesList != null && favouritesList.getCoupons() != null) {
                        for (int i = 0; i < favouritesList.getCoupons().size(); i++) {
                            favoritesArray.add(favouritesList.getCoupons().get(i));
                            listOfStores.add(favouritesList.getListOfStores().get(i));
                            favoritesArray.get(i).setDistanceForSort(calculateDistanceForCoupon(favoritesArray.get(i)).replace(",","."));
                        }
                    }
                }
            }
            if (favoritesArray != null && favoritesArray.size() > 0) {
                Collections.sort(favoritesArray, new SortDataTableByDistanceFormatter());
            }
        }catch (Exception e){

         }
    }

    private String calculateDistanceForCoupon(ListOfCoupons couponData) {
        ResponseGetCoupons responseGetCoupons = ((HomeScreenActivity)mActivity).getCouponsData;
        if( responseGetCoupons!= null
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
    public void isEditEnabledPassToFragment(boolean isEnabled) {
        mFavoritiesRecyclerViewAdapter.isEditEnabled = isEnabled;
        mFavoritiesRecyclerViewAdapter.notifyData();
        if(!isEnabled){
            FavouritesModel favouritesModel = new FavouritesModel();
            favouritesModel.setCoupons(mFavoritiesRecyclerViewAdapter.getCouponsList());
            favouritesModel.setListOfStores(mFavoritiesRecyclerViewAdapter.getStoresList());
            sharedPreferenceUtil.saveData(SharedPrefKeys.FAVOURITIES_ADDED,new Gson().toJson(favouritesModel,FavouritesModel.class));
        }
    }

    @Override
    public void isMapSelectedToFragment(int mapSelectedIndex) {
        if(mapSelectedIndex == 3){
            Intent intent=new Intent(getActivity(), MapsActivity.class);
            intent.putExtra("fromWhere",SharedPrefKeys.GET_FAVORITES);
            intent.putExtra("filter","");
            startActivity(intent);
        }
    }
}
