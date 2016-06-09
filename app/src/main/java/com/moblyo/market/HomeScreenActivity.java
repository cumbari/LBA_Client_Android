package com.moblyo.market;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.moblyo.market.fragments.BrandsFragment;
import com.moblyo.market.fragments.CategoriesFragment;
import com.moblyo.market.fragments.FavoritesFragment;
import com.moblyo.market.fragments.HotDealsFragment;
import com.moblyo.market.fragments.MoreFragment;
import com.moblyo.market.interfaces.OnFragmentAttachedListener;
import com.moblyo.market.interfaces.OnPassValueToFragmentListener;
import com.moblyo.market.interfaces.SortCouponsByDistanceCallback;
import com.moblyo.market.location.GetLocationFromGoogleClient;
import com.moblyo.market.model.ResponseGetBrandedCoupons;
import com.moblyo.market.model.ResponseGetCategories;
import com.moblyo.market.model.ResponseGetCoupons;
import com.moblyo.market.utils.SharedPrefKeys;
import com.moblyo.market.utils.TCLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeScreenActivity extends BaseActivity implements OnFragmentAttachedListener
{

    private FrameLayout data_frame_layout;
    private TextView bottom_hot_deals_title,bottom_categories_title,bottom_brands_title,bottom_favorities_title,bottom_more_title;
    private ImageView bottom_hot_deals_icon,bottom_categories_icon,bottom_brands_icon,bottom_favorities_icon,bottom_more_icon;
    private LinearLayout bottom_hot_deals_layout,bottom_categories_layout,bottom_brands_layout,bottom_favorities_layout,bottom_more_layout;

    private ImageView map_icon;
    private ImageView edit_icon;

    private ArrayList<TextView> titleArray;
    private ArrayList<ImageView> iconArray;

    private int selectedTab;
    private boolean isEditTapped;
    private int isSyncDataCounter;//Location change is called 2 times


    private OnFragmentAttachedListener mOnFragmentAttachedListener;
    private OnPassValueToFragmentListener mOnPassValueToFragmentListener;

    public ResponseGetCoupons getCouponsData;
    public ResponseGetCategories getCategoriesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isSplash = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        try {
            initialiseResources();
            extractDataFromLocal();
            edit_icon.setVisibility(View.GONE);
            if(getIntent()!=null && getIntent().getIntExtra("tabIndex",0) == 4){
                selectedBottomTabChangeResources(4);
                map_icon.setVisibility(View.GONE);
                switchingFragments(new MoreFragment());
            }else if(getIntent()!=null && getIntent().getIntExtra("tabIndex",0) == 2){
                selectedBottomTabChangeResources(2);
                map_icon.setVisibility(View.GONE);
                BrandsFragment brandsFragment = new BrandsFragment();
                mOnPassValueToFragmentListener = brandsFragment;
                switchingFragments(brandsFragment);
            }else{
                selectedBottomTabChangeResources(0);//Hot Deals is selected by default
                HotDealsFragment hotDealsFragment = new HotDealsFragment();
                mOnPassValueToFragmentListener = hotDealsFragment;
                switchingFragments(hotDealsFragment);
            }
        }catch (Exception e){

        }

        getLocationFromGoogleClient = new GetLocationFromGoogleClient(HomeScreenActivity.this, null, new SortCouponsByDistanceCallback() {
            @Override
            public void refreshData(boolean refresh) {
                ++isSyncDataCounter;
                if(mOnPassValueToFragmentListener != null && isSyncDataCounter > 1) {
                    mOnFragmentAttachedListener.isDataSort(refresh);
                }
            }
        });
        getLocationFromGoogleClient.checkLocationSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            extractDataFromLocal();
        }catch (Exception e){

        }
    }

    public void extractDataFromLocal() {
        Gson gson = new Gson();
        JSONObject onj = null;

        String getCouponString= sharedPreferenceUtil.getData(SharedPrefKeys.GET_COUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG"), "");
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

        String getCategoriesString= sharedPreferenceUtil.getData(SharedPrefKeys.GET_CATEGORIES + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG"), "");
        if(getCategoriesString!=null)
        {
            try{
                onj = new JSONObject(getCategoriesString);
            }catch (JSONException e) {
            }
            if(onj != null) {
                getCategoriesData = gson.fromJson(onj.toString(), ResponseGetCategories.class);
            }
        }
    }

    private void initialiseResources() {

        isSyncDataCounter = 0;
        isEditTapped = false;
        mOnFragmentAttachedListener = HomeScreenActivity.this;

        data_frame_layout = (FrameLayout) findViewById(R.id.data_frame_layout);
        map_icon = (ImageView) findViewById(R.id.header_map_icon);
        edit_icon = (ImageView) findViewById(R.id.header_edit_icon);


        bottom_hot_deals_layout = (LinearLayout) findViewById(R.id.bottom_hot_deals_layout);
        bottom_categories_layout = (LinearLayout) findViewById(R.id.bottom_categories_layout);
        bottom_brands_layout = (LinearLayout) findViewById(R.id.bottom_brands_layout);
        bottom_favorities_layout = (LinearLayout) findViewById(R.id.bottom_favorities_layout);
        bottom_more_layout = (LinearLayout) findViewById(R.id.bottom_more_layout);

        bottom_hot_deals_title  = (TextView) findViewById(R.id.bottom_hot_deals_title);
        typeFaceClass.setTypefaceNormal(bottom_hot_deals_title);

        bottom_categories_title = (TextView) findViewById(R.id.bottom_categories_title);
        typeFaceClass.setTypefaceNormal(bottom_categories_title);

        bottom_brands_title = (TextView) findViewById(R.id.bottom_brands_title);
        typeFaceClass.setTypefaceNormal(bottom_brands_title);

        bottom_favorities_title = (TextView) findViewById(R.id.bottom_favorities_title);
        typeFaceClass.setTypefaceNormal(bottom_favorities_title);

        bottom_more_title = (TextView) findViewById(R.id.bottom_more_title);
        typeFaceClass.setTypefaceNormal(bottom_more_title);


        bottom_hot_deals_icon = (ImageView) findViewById(R.id.bottom_hot_deals_icon);
        bottom_categories_icon = (ImageView) findViewById(R.id.bottom_categories_icon);
        bottom_brands_icon = (ImageView) findViewById(R.id.bottom_brands_icon);
        bottom_favorities_icon = (ImageView) findViewById(R.id.bottom_favorities_icon);
        bottom_more_icon = (ImageView) findViewById(R.id.bottom_more_icon);

        titleArray = new ArrayList<>();
        iconArray = new ArrayList<>();

        titleArray.add(bottom_hot_deals_title);
        titleArray.add(bottom_categories_title);
        titleArray.add(bottom_brands_title);
        titleArray.add(bottom_favorities_title);
        titleArray.add(bottom_more_title);

        iconArray.add(bottom_hot_deals_icon);
        iconArray.add(bottom_categories_icon);
        iconArray.add(bottom_brands_icon);
        iconArray.add(bottom_favorities_icon);
        iconArray.add(bottom_more_icon);

        setOnClickListenersForBottom();
    }

    private void setOnClickListenersForBottom() {
        bottom_hot_deals_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTab != 0) {
                    selectedBottomTabChangeResources(0);
                    map_icon.setVisibility(View.VISIBLE);
                    edit_icon.setVisibility(View.GONE);
                    HotDealsFragment hotDealsFragment = new HotDealsFragment();
                    mOnPassValueToFragmentListener = hotDealsFragment;
                    switchingFragments(hotDealsFragment);
                }
            }
        });

        bottom_categories_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTab != 1) {
                    selectedBottomTabChangeResources(1);
                    map_icon.setVisibility(View.GONE);
                    edit_icon.setVisibility(View.GONE);
                    switchingFragments(new CategoriesFragment());
                }
            }
        });

        bottom_brands_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTab != 2) {
                    selectedBottomTabChangeResources(2);
                    map_icon.setVisibility(View.GONE);
                    edit_icon.setVisibility(View.GONE);
                    BrandsFragment brandsFragment = new BrandsFragment();
                    mOnPassValueToFragmentListener = brandsFragment;
                    switchingFragments(brandsFragment);
                }
            }
        });

        bottom_favorities_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTab != 3) {
                    selectedBottomTabChangeResources(3);
                    map_icon.setVisibility(View.VISIBLE);
                    edit_icon.setVisibility(View.VISIBLE);
                    isEditTapped = false;
                    edit_icon.setSelected(isEditTapped);
                    FavoritesFragment favoritesFragment = new FavoritesFragment();
                    mOnPassValueToFragmentListener = favoritesFragment;
                    switchingFragments(favoritesFragment);
                }
            }
        });

        bottom_more_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTab != 4) {
                    selectedBottomTabChangeResources(4);
                    map_icon.setVisibility(View.GONE);
                    edit_icon.setVisibility(View.GONE);
                    switchingFragments(new MoreFragment());
                }
            }
        });

        edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditTapped = !isEditTapped;
                edit_icon.setSelected(isEditTapped);
                mOnFragmentAttachedListener.isEditEnabled(isEditTapped);
                if(isEditTapped){
                    Toast.makeText(HomeScreenActivity.this,getResources().getString(R.string.edit_favorities_toast),Toast.LENGTH_SHORT).show();
                }
            }
        });

        map_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 mOnFragmentAttachedListener.isMapTapped(selectedTab);
            }
        });
    }


    private void selectedBottomTabChangeResources(int tabSelected) {
        appUtility.hideKeyBoard(HomeScreenActivity.this);
        selectedTab = tabSelected;
        for(int i=0;i<titleArray.size();i++){
            if(i == tabSelected){
                titleArray.get(i).setSelected(true);
                iconArray.get(i).setSelected(true);
            }else{
                titleArray.get(i).setSelected(false);
                iconArray.get(i).setSelected(false);
            }
        }
    }

    protected void switchingFragments(android.support.v4.app.Fragment toFragment)
    {
		/*if (getFragmentManager().getBackStackEntryCount() > 0) {
			   getFragmentManager().popBackStack();
			}*/
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.data_frame_layout, toFragment, "");
        fragmentTransaction.commit();
        //  getFragmentManager().executePendingTransactions();      // <----- This is the key
    }


    @Override
    public void onSuccess() {

    }

    @Override
    public void isEditEnabled(boolean isEnabled) {
        if(mOnPassValueToFragmentListener!=null){
            mOnPassValueToFragmentListener.isEditEnabledPassToFragment(isEnabled);
        }
    }

    @Override
    public void isMapTapped(int mapIndex) {
        if(mOnPassValueToFragmentListener!=null){
            mOnPassValueToFragmentListener.isMapSelectedToFragment(mapIndex);
        }
    }

    @Override
    public void isDataSort(boolean sort) {
        if(mOnPassValueToFragmentListener!=null){
            mOnPassValueToFragmentListener.sortData(sort);
        }
    }
}
