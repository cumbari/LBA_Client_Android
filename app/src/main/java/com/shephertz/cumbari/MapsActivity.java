package com.moblyo.market;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.moblyo.market.model.FavouritesModel;
import com.moblyo.market.model.ListOfStores;
import com.moblyo.market.model.ResponseGetCoupons;
import com.moblyo.market.utils.SharedPrefKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends BaseActivity
{
    // the Google Map object
    private GoogleMap mMap;
    private String fromWhere;
    private String filter;
    public ResponseGetCoupons getCouponsData;
    private ArrayList<ListOfStores> listOfStores ;
    private TextView mapTypeHybrid,mapTypeSatellite,mapTypeNormal;
    private String title,snippet;
    private float lat,longitude;

    private GoogleMap.OnMapClickListener clickListener=new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(final LatLng pos) {
        }
    };
    private LatLng STARTING_MARKER_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapType();
        getDataFromBundle();
        extractDataFromLocal();
        setDataInList();
        setUpMapIfNeeded();

    }

    private void setUpMapType() {
        mapTypeHybrid= (TextView) findViewById(R.id.hybridmap);
        typeFaceClass.setTypefaceNormal(mapTypeHybrid);
        mapTypeSatellite= (TextView) findViewById(R.id.satellitemap);
        typeFaceClass.setTypefaceNormal(mapTypeSatellite);
        mapTypeNormal= (TextView) findViewById(R.id.normalmap);
        typeFaceClass.setTypefaceNormal(mapTypeNormal);
        mapTypeNormal.setSelected(true);

        mapTypeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapTypeNormal.setSelected(true);
                mapTypeSatellite.setSelected(false);
                mapTypeHybrid.setSelected(false);

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        mapTypeSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapTypeNormal.setSelected(false);
                mapTypeSatellite.setSelected(true);
                mapTypeHybrid.setSelected(false);
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        mapTypeHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapTypeNormal.setSelected(false);
                mapTypeSatellite.setSelected(false);
                mapTypeHybrid.setSelected(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
    }

    private void getDataFromBundle() {
        fromWhere = getIntent().getStringExtra("fromWhere");
        filter = getIntent().getStringExtra("filter");
        lat= getIntent().getFloatExtra("latitude",0f);
        longitude = getIntent().getFloatExtra("longitude",0f);
        title= getIntent().getStringExtra("title");
        snippet = getIntent().getStringExtra("snippet");
        if(filter == null){
            filter = "";
        }
    }

    private void extractDataFromLocal() {
        Gson gson = new Gson();
        JSONObject onj = null;
        String getCouponString = null;
        if(fromWhere.equals(SharedPrefKeys.GET_COUPONS)){
            getCouponString = sharedPreferenceUtil.getData(SharedPrefKeys.GET_COUPONS +
                    sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG") + filter, "");
        }else  if(fromWhere.equals(SharedPrefKeys.GET_CATEGORIES)){
            getCouponString = sharedPreferenceUtil.getData(SharedPrefKeys.GET_CATEGORIES +
                    sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG") + filter, "");
        } else  if(fromWhere.equals(SharedPrefKeys.GET_BRANDEDCOUPONS)){
            getCouponString = sharedPreferenceUtil.getData(SharedPrefKeys.GET_BRANDEDCOUPONS +
                    sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG") + filter, "");
        }else  if(fromWhere.equals(SharedPrefKeys.GET_FAVORITES))
        {
            listOfStores =  new ArrayList<ListOfStores>();

            if(sharedPreferenceUtil.getData(SharedPrefKeys.FAVOURITIES_ADDED,"").length()>0) {
                try {
                    onj = new JSONObject(sharedPreferenceUtil.getData(SharedPrefKeys.FAVOURITIES_ADDED,""));
                } catch (JSONException e) {
                }
                if(onj != null) {
                    FavouritesModel favouritesList = gson.fromJson(onj.toString(), FavouritesModel.class);
                    if(favouritesList != null && favouritesList.getCoupons() != null) {
                        for (int i = 0; i < favouritesList.getCoupons().size();i++)
                        {
                            listOfStores.add(favouritesList.getListOfStores().get(i));
                        }
                    }
                }
            }
        }
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

    private void setDataInList()
    {

        if(getCouponsData != null){
            listOfStores = getCouponsData.getListOfStores();
        }

        if(title!=null)
        {
            listOfStores= new ArrayList<ListOfStores>();
            ListOfStores model=new ListOfStores();
            model.setStoreName(title);
            model.setStreet(snippet);
            model.setLatitude(lat);
            model.setLongitude(longitude);
            listOfStores.add(model);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        float latitude = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LATITUDE, 0f);
        float longitude = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LONGITUDE, 0f);
        STARTING_MARKER_POSITION=new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(STARTING_MARKER_POSITION, 14));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(title!=null) {
            mMap.addMarker(new MarkerOptions()
                    .position(STARTING_MARKER_POSITION)
                    .title("You are here!!")
                    .snippet(latitude + "," + longitude)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }

        if(listOfStores!=null) {
            for (int i = 0; i < listOfStores.size(); i++) {
                drawMarker(new LatLng(listOfStores.get(i).getLatitude(), listOfStores.get(i).getLongitude()), listOfStores.get(i).getStoreName(), listOfStores.get(i).getStreet());
            }
        }
        mMap.setOnMapClickListener(clickListener);
    }

    private void drawMarker(LatLng point, String storeName, String street){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.draggable(false);
        markerOptions.title(storeName);
        markerOptions.snippet(street);
        mMap.addMarker(markerOptions);
    }
}
