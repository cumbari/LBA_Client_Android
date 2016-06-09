package com.moblyo.market;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.moblyo.market.interfaces.OnLoadMoreListener;
import com.moblyo.market.location.Coordinate;
import com.moblyo.market.model.ActivatedDealCouponList;
import com.moblyo.market.model.CouponViewStatistic;
import com.moblyo.market.model.FavouritesModel;
import com.moblyo.market.model.LimitPeriodList;
import com.moblyo.market.model.ListOfCoupons;
import com.moblyo.market.model.ListOfStores;
import com.moblyo.market.model.ResponseGetCouponDetails;
import com.moblyo.market.model.ResponseGetCoupons;
import com.moblyo.market.model.ResponseUseCoupon;
import com.moblyo.market.sync.CustomAsyncTask;
import com.moblyo.market.utils.SharedPrefKeys;
import com.moblyo.market.utils.SwipeButton;
import com.moblyo.market.utils.SwipeButtonCustomItems;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetailedCouponActivity extends BaseActivity
{

    private RelativeLayout detail_coupon_main_holder;
    private ScrollView detail_coupon_info_holder;
    private RelativeLayout detail_coupon_facebook_holder;
    private RelativeLayout detail_coupon_bottom_rl;

    private ImageView map_icon;
    private ImageView edit_icon;
    private ImageView detail_coupon_facebook;
    private TextView detail_coupon_more_info,detail_coupon_fav,detail_coupon_more_deals;

    //facebook Holder
    private ImageView facebook_coupon_image;
    private Button logout_button;
    private Button facebook_post_deal;
    private TextView facebook_coupon_title;
    private TextView facebook_coupon_slogan;

    //Main Holder
    private ImageView detail_coupon_image;
    private Button detail_coupon_distance;
    private TextView detail_coupon_title;
    private TextView detail_coupon_slogan;
    private TextView detail_coupon_address;
    private TextView detail_coupon_validity;


    //Info Holder
    private TextView more_info_title;
    private TextView more_info_coupon_title;
    private TextView more_info_coupon_slogan;
    private TextView more_info_coupon_productinfo_link;
    private TextView more_info_coupon_store_label;
    private TextView more_info_coupon_store_info;

    private TextView more_info_coupon_address_label;
    private TextView more_info_coupon_address_info;
    private TextView more_info_coupon_phone_label;
    private TextView more_info_coupon_phone_info;
    private TextView more_info_coupon_email_label;
    private TextView more_info_coupon_email_info;

    private TextView more_info_coupon_company_home_label;
    private TextView more_info_coupon_company_home_info;


    public ResponseGetCouponDetails getCouponsDetail;
    public ListOfCoupons couponData;
    public ListOfStores storeData;

    private String couponID;
    private String fromWhere;
    private String filter;
    private boolean callBackHomeScreen = false;

    private String validDay = "";
    private String timeString = "";
    private int startTime = 0;
    private int endTime = 0;
    private int distanceToSendForUseCoupon = 0;

    private boolean useDeal = false;


    private ArrayList<LimitPeriodList> limitPeriodLists;
    private Dialog addToFavoriteDialog = null;
    private Dialog activateDealDialog = null;

    private Gson gson = null;
    private JSONObject onj = null;
    private FavouritesModel favouritesList = null;
    private ActivatedDealCouponList activatedDealCouponList = null;

    private int counterMin = 0;
    private int counterSec = 0;

    private Handler handler;
    private Runnable runnable1;
    private boolean isTimerRunning=false;

    private Handler handlerForUpdatingDistance;
    private Runnable runnableForUpdatingDistance;

    public ResponseGetCoupons getCouponsData;
    public ResponseGetCoupons getCouponsFilterData;
    public ResponseGetCoupons getCategoriesCouponsData;
    public ResponseGetCoupons getBrandedCouponsData;
    private String pinCode = "";
    private String distanceToShow = "";

    private Dialog facebookAlert;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    //For viewOpt
    private boolean shouldShowTimer;
    private Handler handlerForToggling;
    private Runnable runnableForToggling;

    private Handler handlerValidUntil;
    private Runnable runnableValidUntil;
    private long days,hours, minutes, seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_coupon);
        getDataFromBundle();
        facebookSDKInitialize();
        initialiseResources();
        extractDataFromLocal();
        generateAddToFavoriteDialog();
        generateFacebookDialog();
        new CustomAsyncTask(DetailedCouponActivity.this, couponID,true, new OnLoadMoreListener<String>() {
            @Override
            public void onLoadMore(String resultModel) {
                System.out.println(resultModel);
                getCouponsDetail = extractDataFromResponse(resultModel);
                if(getCouponsDetail!=null) {
                    couponData = getCouponsDetail.getCoupon();
                    storeData = getCouponsDetail.getStoreInfo();
                    if(couponData != null && storeData != null) {
                        setDetailedData();
                        generateActivateDealDialog();

                        //check if this coupon is already used
                        //offerType:= "ONCE"|"MANY"|"ADVERTISE"
                        if (checkIfCouponIsAlreayUsed()) {
                            if (couponData.getOfferType().equals("ONCE")) {
                                detail_coupon_distance.setText(getResources().getString(R.string.coupon_already_used));
                                detail_coupon_distance.setEnabled(false);
                                detail_coupon_distance.setBackgroundResource(R.drawable.cancel_rounded_bg);
                            } else if (couponData.getOfferType().equals("MANY")) {
                                //set It's expiry Date
                                detail_coupon_distance.setText(getResources().getString(R.string.coupon_expired_in) + " " + timeString);
                                detail_coupon_distance.setEnabled(false);
                                detail_coupon_distance.setBackgroundResource(R.drawable.cancel_rounded_bg);
                            }
                        }

                        //Check if this coupon is Advertised coupon or not
                        String viewOpt = couponData.getViewOpt();//"CD";//testing
                        if (!couponData.getOfferType().equals("ADVERTISE") && viewOpt.equals("CD") && distanceToSendForUseCoupon>=300) {
                            shouldShowTimer = true;
                            showCDTimer();
                            startTimerToToggleForViewOpt();
                        }

                    }else{
                        alertMessage.alertBoxForNoDetailing(DetailedCouponActivity.this);
                    }
                }else{
                    alertMessage.alertBoxForNoDetailing(DetailedCouponActivity.this);
                }
            }
        }).execute();
    }

    private void showCDTimer(){
        detail_coupon_validity.setText(timeString +"\n"+getResources().getString(R.string.offer_expires_in));
        //Time left from End of Publishing from now
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date endDateForTimer = sdf.parse(couponData.getEndOfPublishing());
            seconds = endDateForTimer.getTime()/1000 - System.currentTimeMillis()/1000;;
           // seconds =  1474569000000l/1000 - System.currentTimeMillis()/1000; //testing
              startValidUntilTimer();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void startValidUntilTimer() {
        if (handlerValidUntil != null) {
            handlerValidUntil.removeCallbacks(runnableValidUntil);
            handlerValidUntil = null;
        }

        runnableValidUntil = new Runnable(){
            public void run() {
                try{
                    if(seconds > 0 ) {
                        long diffSeconds = seconds % 60;
                        minutes = seconds / 60  % 60;
                        hours = seconds / (60 * 60 ) % 24;
                        days = seconds / (24 * 60 * 60 );
                        seconds -- ;
                        if(shouldShowTimer) {
                            detail_coupon_distance.setText(appUtility.getTimerCountDownValueForViewOpt(days, hours, minutes, diffSeconds));
                        }
                    }else{
                        //Coupon is expired
                        if (handlerValidUntil != null) {
                            handlerValidUntil.removeCallbacks(runnableValidUntil);
                            handlerValidUntil = null;
                        }
                        detail_coupon_distance.setText(calculateDistanceForCoupon().replace(",","."));
                        Toast.makeText(DetailedCouponActivity.this,getResources().getString(R.string.coupon_expired_message),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (handlerValidUntil != null)
                    handlerValidUntil.postDelayed(this, 1000);
            }
        };
        handlerValidUntil = new Handler();
        handlerValidUntil.postDelayed(runnableValidUntil,1000);
    }

    private void startTimerToToggleForViewOpt() {
        if (handlerForToggling != null) {
            handlerForToggling.removeCallbacks(runnableForToggling);
            handlerForToggling = null;
        }

        runnableForToggling = new Runnable(){
            public void run() {
                try{

                    if (distanceToSendForUseCoupon<300) {
                        if (handlerForToggling != null) {
                            handlerForToggling.removeCallbacks(runnableForToggling);
                            handlerForToggling = null;
                        }
                        shouldShowTimer = false;
                    }
                    else
                        shouldShowTimer = !shouldShowTimer;

                    if(!shouldShowTimer) {
                        detail_coupon_distance.setText(calculateDistanceForCoupon().replace(",","."));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (handlerForToggling != null)
                    handlerForToggling.postDelayed(this, 20*1000);
            }
        };
        handlerForToggling = new Handler();
        handlerForToggling.postDelayed(runnableForToggling,20*1000);
    }

    private void generateFacebookDialog() {

        shareDialog = new ShareDialog(this);

        facebookAlert = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_facebook_login, null);
        facebookAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        facebookAlert.setCanceledOnTouchOutside(false);
        facebookAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            detail_coupon_bottom_rl.setVisibility(View.VISIBLE);
        }
     });
        TextView login_title = (TextView) view.findViewById(R.id.login_title);
        typeFaceClass.setTypefaceBold(login_title);

        final LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email,publish_actions");
        typeFaceClass.setTypefaceBold(loginButton);

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getLoginDetails(loginButton);
                facebookAlert.dismiss();
            }
        });

        facebookAlert.setContentView(view);
    }

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void showPostDealLayout(){
        map_icon.setVisibility(View.INVISIBLE);
        detail_coupon_bottom_rl.setVisibility(View.GONE);
        detail_coupon_facebook_holder.setVisibility(View.VISIBLE);
        if (detail_coupon_more_info.isSelected()) {
            detail_coupon_info_holder.setVisibility(View.INVISIBLE);
        } else {
            detail_coupon_main_holder.setVisibility(View.INVISIBLE);
        }
    }
    protected void getLoginDetails(LoginButton login_button){

        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                getUserInfo(login_result);
            }

            @Override
            public void onCancel() {
                Toast.makeText(DetailedCouponActivity.this,getResources().getString(R.string.not_logged_in),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(DetailedCouponActivity.this,getResources().getString(R.string.not_logged_in),Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void getUserInfo(LoginResult login_result){

        GraphRequest data_request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        if(detail_coupon_facebook_holder.getVisibility() != View.VISIBLE) {
                            showPostDealLayout();
                        }
                   }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null)
        {
            handler.removeCallbacks(runnable1);
            handler = null;
            runnable1=null;
        }

        if (handlerForUpdatingDistance != null) {
            handlerForUpdatingDistance.removeCallbacks(runnableForUpdatingDistance);
            handlerForUpdatingDistance = null;
            runnableForUpdatingDistance = null;
        }
    }

    private void extractDataFromLocal() {
        Gson gson = new Gson();
        JSONObject onj = null;
        String getCouponString;

        if(fromWhere.equals(SharedPrefKeys.GET_COUPONS)){
            if (filter.length() > 0) {
                getCouponString= sharedPreferenceUtil.getData(SharedPrefKeys.GET_COUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG") + filter, "");
                if(getCouponString!=null)
                {
                    try{
                        onj = new JSONObject(getCouponString);
                    }catch (JSONException e) {
                    }
                    if(onj != null) {
                        getCouponsFilterData = gson.fromJson(onj.toString(), ResponseGetCoupons.class);
                    }
                }
                if(getCouponsFilterData == null){
                    getCouponsFilterData = new ResponseGetCoupons();
                }

                if(getCouponsFilterData.getListOfCoupons() == null){
                    getCouponsFilterData.setListOfCoupons(new ArrayList<ListOfCoupons>());
                }

            }else{
                getCouponString = sharedPreferenceUtil.getData(SharedPrefKeys.GET_COUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG"), "");
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
                if(getCouponsData == null){
                    getCouponsData = new ResponseGetCoupons();
                }

                if(getCouponsData.getListOfCoupons() == null){
                    getCouponsData.setListOfCoupons(new ArrayList<ListOfCoupons>());
                }
           }
        }else  if(fromWhere.equals(SharedPrefKeys.GET_CATEGORIES)){
            String getCategoriesString= sharedPreferenceUtil.getData(SharedPrefKeys.GET_CATEGORIES + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG") + filter, "");
            if(getCategoriesString!=null)
            {
                try{
                    onj = new JSONObject(getCategoriesString);
                }catch (JSONException e) {
                }
                if(onj != null) {
                    getCategoriesCouponsData = gson.fromJson(onj.toString(), ResponseGetCoupons.class);
                }
            }
            if(getCategoriesCouponsData == null){
                getCategoriesCouponsData = new ResponseGetCoupons();
            }

            if(getCategoriesCouponsData.getListOfCoupons() == null){
                getCategoriesCouponsData.setListOfCoupons(new ArrayList<ListOfCoupons>());
            }
        } else  if(fromWhere.equals(SharedPrefKeys.GET_BRANDEDCOUPONS)){
            String getBrandedCouponsString= sharedPreferenceUtil.getData(SharedPrefKeys.GET_BRANDEDCOUPONS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG") + filter, "");
            if(getBrandedCouponsString!=null)
            {
                try{
                    onj = new JSONObject(getBrandedCouponsString);
                }catch (JSONException e) {
                }

                if(onj != null) {
                    getBrandedCouponsData = gson.fromJson(onj.toString(), ResponseGetCoupons.class);
                }
            }
            if(getBrandedCouponsData == null){
                getBrandedCouponsData = new ResponseGetCoupons();
            }

            if(getBrandedCouponsData.getListOfCoupons() == null){
                getBrandedCouponsData.setListOfCoupons(new ArrayList<ListOfCoupons>());
            }
        }
    }

    private boolean checkIfCouponIsAlreayUsed() {
        for(int i=0;i<activatedDealCouponList.getCoupons().size();i++){
            if(couponID.equals(activatedDealCouponList.getCoupons().get(i).getCouponId())){
                return true;
            }
        }
        return false;
    }

    private void setDetailedData() {

        limitPeriodLists = new ArrayList<>();
        limitPeriodLists = couponData.getLimitPeriodList();
        if (limitPeriodLists !=null && limitPeriodLists.size() == 0) {
            validDay = "";
        }

        String endOfPublishing = couponData.getEndOfPublishing().substring(0,10);
        if(limitPeriodLists !=null) {
            for (int loop = 0; loop < limitPeriodLists.size(); loop++) {
                LimitPeriodList dictForLimitPeriodList = limitPeriodLists.get(loop);
                validDay = dictForLimitPeriodList.getValidDay();

                String validDayInLanguage = validDay;
                if (validDay.equals(getResources().getString(R.string.monday_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.monday_s);
                } else if (validDay.equals(getResources().getString(R.string.tuesday_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.tuesday_s);
                } else if (validDay.equals(getResources().getString(R.string.wednesday_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.wednesday_s);
                } else if (validDay.equals(getResources().getString(R.string.thursday_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.thursday_s);
                } else if (validDay.equals(getResources().getString(R.string.friday_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.friday_s);
                } else if (validDay.equals(getResources().getString(R.string.saturday_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.saturday_s);
                } else if (validDay.equals(getResources().getString(R.string.sunday_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.sunday_s);
                } else if (validDay.equals(getResources().getString(R.string.monday_friday_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.monday_friday_s);
                } else if (validDay.equals(getResources().getString(R.string.all_week_c))) {
                    validDayInLanguage = getResources().getString(R.string.valid) + getResources().getString(R.string.all_week_s);
                }

                startTime = dictForLimitPeriodList.getStartTime();
                endTime = dictForLimitPeriodList.getEndTime();
                timeString = validDayInLanguage +"\n";
                timeString =  startTime + "-" + endTime;
            }
        }
        timeString += getResources().getString(R.string.valid_until) +" " + endOfPublishing;

        if (couponData.getOfferType().equals("ADVERTISE")) {
            detail_coupon_distance.setVisibility(View.INVISIBLE);
        }else{
            detail_coupon_distance.setVisibility(View.VISIBLE);
        }

        detail_coupon_validity.setText(timeString);
        detail_coupon_title.setText(couponData.getOfferTitle());
        more_info_coupon_title.setText(couponData.getOfferTitle());
        facebook_coupon_title.setText(couponData.getOfferTitle());
        facebook_coupon_slogan.setText(couponData.getOfferSlogan());
        detail_coupon_slogan.setText(couponData.getOfferSlogan());
        more_info_coupon_slogan.setText(couponData.getOfferSlogan());
        detail_coupon_address.setText(storeData.getStoreName()+ "\n" +storeData.getStreet());

        Ion.with(this).load(couponData.getLargeImage())
                .withBitmap()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .intoImageView(detail_coupon_image);

        Ion.with(this).load(couponData.getLargeImage())
                .withBitmap()
                .placeholder(R.drawable.no_image)
                .error(R.drawable.no_image)
                .intoImageView(facebook_coupon_image);

        more_info_coupon_productinfo_link.setText(couponData.getProductInfoLink());
        more_info_coupon_store_info.setText(storeData.getStoreName()+ "\n" +storeData.getStreet());
        more_info_coupon_address_info.setText(storeData.getStreet());
        more_info_coupon_phone_info.setText(storeData.getPhone());
        more_info_coupon_email_info.setText(storeData.getEmail());
        more_info_coupon_company_home_info.setText(storeData.getHomePage());

        detail_coupon_distance.setText(calculateDistanceForCoupon().replace(",","."));
        startTimerForCouponDistanceDisplay();
        sendStatisticalDataToServer();
    }

    private void sendStatisticalDataToServer() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = sdf.format(c.getTime());

        CouponViewStatistic couponViewStatistic = new CouponViewStatistic();
        couponViewStatistic.setEventTime(strDate);
        couponViewStatistic.setCouponId(couponID);
        couponViewStatistic.setStoreId(storeData.getStoreId());
        try {
            couponViewStatistic.setDistanceToStore(distanceToSendForUseCoupon);
        }catch (Exception e){
            couponViewStatistic.setDistanceToStore(couponData.getDistanceToStore());
        }

        new CustomAsyncTask(DetailedCouponActivity.this, couponViewStatistic, new OnLoadMoreListener() {
            @Override
            public void onLoadMore(Object resultModel) {
                System.out.println("Response: "+resultModel);
            }
        }).execute();
    }

    private void startTimerForCouponDistanceDisplay() {
        if (handlerForUpdatingDistance != null) {
            handlerForUpdatingDistance.removeCallbacks(runnableForUpdatingDistance);
            handlerForUpdatingDistance = null;
        }

        runnableForUpdatingDistance = new Runnable(){
            public void run() {
                try{
                    detail_coupon_distance.setText(calculateDistanceForCoupon().replace(",","."));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (handlerForUpdatingDistance != null)
                    handlerForUpdatingDistance.postDelayed(this, 20000);
            }
        };
        handlerForUpdatingDistance = new Handler();
        handlerForUpdatingDistance.postDelayed(runnableForUpdatingDistance,20000);
    }

    private ResponseGetCouponDetails extractDataFromResponse(String getCouponString) {
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
                return gson.fromJson(onj.toString(), ResponseGetCouponDetails.class);
            }else{
                return null;
            }

        }else{
            return null;
        }
    }

    private String calculateDistanceForCoupon() {

        double lat1 = (double) (storeData.getLatitude());
        double lng1 = (double) (storeData.getLongitude());
        double currentLat = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LATITUDE, 0f);
        double currentLng = sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LONGITUDE, 0f);

        double distance = new Coordinate().distance(
                lat1,
                lng1,
                currentLat,
                currentLng,
                Coordinate.Unit.Meter);

        distanceToSendForUseCoupon = (int)distance;
        String dataToSet = setDistanceButtonConditions(distance);
        if(dataToSet != null){
            return dataToSet;
        }else{
            if(sharedPreferenceUtil.getData(SharedPrefKeys.DISTANCE_UNIT, Coordinate.Unit.Meter.getValue()) == Coordinate.Unit.Meter.getValue()){
                if(distance >1000.0) {
                    return String.format("%.2f",(distance / 1000.0))+" km";
                }else {
                    try {
                        couponData.setDistanceToStore(Integer.parseInt(String.format("%.2f", distance)));
                    }catch (Exception e){

                    }
                    return String.format("%.2f", distance) +" m";
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
        return "";
    }

    private String setDistanceButtonConditions(double distance) {
        try {
            if (distance < 300) {
                useDeal = true;
                detail_coupon_distance.setBackgroundResource(R.drawable.green_button);
            } else {
                useDeal = false;
                detail_coupon_distance.setBackgroundResource(R.drawable.detailed_normal);
            }
        }catch (OutOfMemoryError ofm){

        }catch (Exception e){

        }

        if(distance <= 100){
            if(couponData.getOfferType().equals("ADVERTISE")){
                return null;
            }else{
                return getResources().getString(R.string.use_deal);
            }
        }

        return null;
    }

    private void getDataFromBundle() {
        couponID = getIntent().getStringExtra("couponID");
        fromWhere = getIntent().getStringExtra("fromWhere");
        distanceToShow = getIntent().getStringExtra("distanceToShow");
        filter = getIntent().getStringExtra("filter");
        callBackHomeScreen = getIntent().getBooleanExtra("callBackHomeScreen",false);
        if(filter == null){
            filter = "";
        }
    }

    private void initialiseResources() {

        gson = new Gson();
        useDeal = false;
        getActivatedDealsList();

        map_icon = (ImageView) findViewById(R.id.header_map_icon);
        edit_icon = (ImageView) findViewById(R.id.header_edit_icon);
        edit_icon.setVisibility(View.GONE);

        detail_coupon_main_holder  = (RelativeLayout) findViewById(R.id.detail_coupon_main_holder);
        detail_coupon_info_holder  = (ScrollView) findViewById(R.id.detail_coupon_info_holder);
        detail_coupon_facebook_holder  = (RelativeLayout) findViewById(R.id.detail_coupon_facebook_holder);
        detail_coupon_main_holder.setVisibility(View.VISIBLE);
        detail_coupon_info_holder.setVisibility(View.INVISIBLE);
        detail_coupon_facebook_holder.setVisibility(View.INVISIBLE);

        detail_coupon_bottom_rl = (RelativeLayout) findViewById(R.id.detail_coupon_bottom_rl);
        detail_coupon_facebook  = (ImageView) findViewById(R.id.detail_coupon_facebook);

        detail_coupon_more_info  = (TextView) findViewById(R.id.detail_coupon_more_info);
        detail_coupon_fav  = (TextView) findViewById(R.id.detail_coupon_fav);
        detail_coupon_more_deals  = (TextView) findViewById(R.id.detail_coupon_more_deals);

        typeFaceClass.setTypefaceBold(detail_coupon_more_info);
        typeFaceClass.setTypefaceBold(detail_coupon_fav);
        typeFaceClass.setTypefaceBold(detail_coupon_more_deals);

        initFacebookHolderResources();
        initMainHolderResources();
        initInfoHolderResources();

        setOnClickListenersForBottom();
    }

    private void initFacebookHolderResources() {
        facebook_coupon_image = (ImageView) findViewById(R.id.facebook_coupon_image);
        facebook_post_deal = (Button) findViewById(R.id.facebook_post_deal);
        facebook_coupon_title = (TextView) findViewById(R.id.facebook_coupon_title);
        facebook_coupon_slogan = (TextView) findViewById(R.id.facebook_coupon_slogan);

        logout_button = (Button) findViewById(R.id.logout_button);

        typeFaceClass.setTypefaceBold(facebook_coupon_title);
        typeFaceClass.setTypefaceNormal(facebook_coupon_slogan);
        typeFaceClass.setTypefaceBold(logout_button);
        typeFaceClass.setTypefaceBold(facebook_post_deal);

    }

    private void initMainHolderResources() {
        detail_coupon_image = (ImageView) findViewById(R.id.detail_coupon_image);
        detail_coupon_distance = (Button) findViewById(R.id.detail_coupon_distance);
        detail_coupon_title = (TextView) findViewById(R.id.detail_coupon_title);
        detail_coupon_slogan = (TextView) findViewById(R.id.detail_coupon_slogan);
        detail_coupon_address = (TextView) findViewById(R.id.detail_coupon_address);
        detail_coupon_validity = (TextView) findViewById(R.id.detail_coupon_validity);

        typeFaceClass.setTypefaceBold(detail_coupon_title);
        typeFaceClass.setTypefaceBold(detail_coupon_distance);

        typeFaceClass.setTypefaceNormal(detail_coupon_slogan);
        typeFaceClass.setTypefaceNormal(detail_coupon_address);
        typeFaceClass.setTypefaceNormal(detail_coupon_validity);
    }

    private void initInfoHolderResources() {
        //Info Holder
        more_info_title = (TextView) findViewById(R.id.more_info_title);
        more_info_coupon_title = (TextView) findViewById(R.id.detail_coupon_title);
        more_info_coupon_slogan = (TextView) findViewById(R.id.more_info_coupon_title);
        more_info_coupon_productinfo_link = (TextView) findViewById(R.id.more_info_coupon_productinfo_link);
        more_info_coupon_store_label = (TextView) findViewById(R.id.more_info_coupon_store_label);
        more_info_coupon_store_info = (TextView) findViewById(R.id.more_info_coupon_store_info);

        more_info_coupon_address_label = (TextView) findViewById(R.id.more_info_coupon_address_label);
        more_info_coupon_address_info = (TextView) findViewById(R.id.more_info_coupon_address_info);
        more_info_coupon_phone_label = (TextView) findViewById(R.id.more_info_coupon_phone_label);
        more_info_coupon_phone_info = (TextView) findViewById(R.id.more_info_coupon_phone_info);
        more_info_coupon_email_label = (TextView) findViewById(R.id.more_info_coupon_email_label);
        more_info_coupon_email_info = (TextView) findViewById(R.id.more_info_coupon_email_info);

        more_info_coupon_company_home_label = (TextView) findViewById(R.id.more_info_coupon_company_home_label);
        more_info_coupon_company_home_info = (TextView) findViewById(R.id.more_info_coupon_company_home_info);

        typeFaceClass.setTypefaceBold(more_info_title);
        typeFaceClass.setTypefaceBold(more_info_coupon_title);
        typeFaceClass.setTypefaceMed(more_info_coupon_store_label);
        typeFaceClass.setTypefaceMed(more_info_coupon_address_label);
        typeFaceClass.setTypefaceMed(more_info_coupon_phone_label);
        typeFaceClass.setTypefaceMed(more_info_coupon_email_label);
        typeFaceClass.setTypefaceMed(more_info_coupon_company_home_label);

        typeFaceClass.setTypefaceNormal(more_info_coupon_slogan);
        typeFaceClass.setTypefaceNormal(more_info_coupon_productinfo_link);
        typeFaceClass.setTypefaceNormal(more_info_coupon_store_info);
        typeFaceClass.setTypefaceNormal(more_info_coupon_address_info);
        typeFaceClass.setTypefaceNormal(more_info_coupon_phone_info);
        typeFaceClass.setTypefaceNormal(more_info_coupon_email_info);
        typeFaceClass.setTypefaceNormal(more_info_coupon_company_home_info);
    }


    private void setOnClickListenersForBottom() {
        detail_coupon_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!useDeal){
                    alertMessage.alertBoxForTooFarAwayFromStore();
                }else{
                    android.text.format.DateFormat df = new android.text.format.DateFormat();
                    Date current = new Date();
                    String day = df.format("HH EEEE", current).toString();
                    int theHour = Integer.parseInt(day.split(" ")[0]);
                    String theWeek = day.split(" ")[1];
                    String message = getResources().getString(R.string.valid) + validDay+ " \n"+startTime+" - "+endTime;

                    if (validDay.equals(getResources().getString(R.string.monday_c)) ||
                            validDay.equals(getResources().getString(R.string.tuesday_c)) ||
                            validDay.equals(getResources().getString(R.string.wednesday_c)) ||
                            validDay.equals(getResources().getString(R.string.thursday_c)) ||
                            validDay.equals(getResources().getString(R.string.friday_c)) ||
                            validDay.equals(getResources().getString(R.string.saturday_c)) ||
                            validDay.equals(getResources().getString(R.string.sunday_c)) ||
                            validDay.equals(getResources().getString(R.string.all_week_c))
                            ) {
                        if (((theHour >= startTime) && (theHour <endTime)) || (startTime == 0 && endTime ==0)) {
                            activateDealDialog.show();
                        }else {
                            alertMessage.alertBoxForValidityCase(message);
                        }
                    } else if (validDay.equals(getResources().getString(R.string.monday_friday_c))) {
                        if(theWeek.equalsIgnoreCase(getResources().getString(R.string.saturday_validity)) || theWeek.equalsIgnoreCase(getResources().getString(R.string.sunday_validity))){
                            alertMessage.alertBoxForValidityCase(getResources().getString(R.string.you_can_use_this_coupon)+" "+validDay+" "+getResources().getString(R.string.only));
                        }else if (((theHour >= startTime) && (theHour <endTime)) || (startTime == 0 && endTime ==0)){
                            activateDealDialog.show();
                        }else {
                            alertMessage.alertBoxForValidityCase(message);
                        }
                    } else if (validDay.length() == 0) {
                        activateDealDialog.show();
                    }else {
                        alertMessage.alertBoxForValidityCase(message);
                    }
                }
            }
        });

        detail_coupon_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeData != null && storeData.getStoreId()!= null) {
                    if(appUtility.isNetworkAvailable()) {
                        if(isLoggedIn()){
                            showPostDealLayout();
                        }else {
                            facebookAlert.show();
                        }
                    }else{
                        alertMessage.alertBoxForNoConnection(null);
                    }
                }else{
                    alertMessage.alertBoxForNoDetailing(DetailedCouponActivity.this);
                }
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AccessToken.setCurrentAccessToken(null);
                hideFacebookPostionLayout();
            }
        });

        facebook_post_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(storeData != null && storeData.getStoreId()!= null) {
                    if(appUtility.isNetworkAvailable()) {
                        if(isLoggedIn()) {
                            if (ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentTitle(couponData.getOfferTitle())
                                        .setImageUrl(Uri.parse(couponData.getLargeImage()))
                                        .setContentDescription(couponData.getOfferSlogan())
                                        .setContentUrl(Uri.parse("http://www.cumbari.com"))
                                        .build();
                                shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);  // Show facebook ShareDialog
                            }
                        }else{
                            Toast.makeText(DetailedCouponActivity.this,getResources().getString(R.string.not_logged_in),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        alertMessage.alertBoxForNoConnection(null);
                    }
                }else{
                    alertMessage.alertBoxForNoDetailing(DetailedCouponActivity.this);
                }
            }
        });

        detail_coupon_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detail_coupon_more_info.setSelected(true);
                map_icon.setVisibility(View.INVISIBLE);
                detail_coupon_main_holder.setVisibility(View.INVISIBLE);
                detail_coupon_info_holder.setVisibility(View.VISIBLE);
            }
        });

        detail_coupon_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeData != null && couponData != null) {
                    if(couponData.getSmallImage()!=null && couponData.getSmallImage().equals("http://www.cumbari.com/images/category/Google.png")){
                        alertMessage.alertBoxCannotAddGoogleCoupon();
                    }else if(!checkIfCouponIsAlreadyAdded()){
                        detail_coupon_fav.setSelected(true);
                        addToFavoriteDialog.show();
                    }else{
                        alertMessage.alertBoxForFavoriteAlreadyAdded();
                    }
                }else{
                    alertMessage.alertBoxForNoDetailing(DetailedCouponActivity.this);
                }
            }
        });

        detail_coupon_more_deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(storeData != null && storeData.getStoreId()!= null && storeData.getStoreId().length() > 0) {
                    Intent moveToDetailing = new Intent(DetailedCouponActivity.this, MoreDealsActivity.class);
                    moveToDetailing.putExtra("fromWhere", fromWhere);
                    moveToDetailing.putExtra("storeId", storeData.getStoreId());
                    startActivity(moveToDetailing);
                }else{
                    alertMessage.alertBoxForNoDetailing(DetailedCouponActivity.this);
                }
            }
        });

        map_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(distanceToSendForUseCoupon<10000){
                    Intent intent=new Intent(DetailedCouponActivity.this, MapsActivity.class);
                    intent.putExtra("fromWhere",fromWhere);
                    intent.putExtra("filter","");
                    if(storeData!=null) {
                        intent.putExtra("title", storeData.getStoreName());
                        intent.putExtra("snippet", storeData.getStreet() + "," + storeData.getCity());
                        intent.putExtra("latitude", storeData.getLatitude());
                        intent.putExtra("longitude", storeData.getLongitude());
                    }
                    startActivity(intent);
                }else{
                    alertMessage.alertBoxForDistanceBeyondLimit();
                }
            }
        });
    }

    private void generateAddToFavoriteDialog()
    {
        addToFavoriteDialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_to_favorites, null);
        addToFavoriteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addToFavoriteDialog.setCanceledOnTouchOutside(false);

        addToFavoriteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                detail_coupon_fav.setSelected(false);
            }
        });

        TextView add_to_fav_message = (TextView) view.findViewById(R.id.add_to_fav_message);
        typeFaceClass.setTypefaceBold(add_to_fav_message);

        Button add_to_fav_done = (Button) view.findViewById(R.id.add_to_fav_done);
        typeFaceClass.setTypefaceBold(add_to_fav_done);

        Button add_to_fav_cancel = (Button) view.findViewById(R.id.add_to_fav_cancel);
        typeFaceClass.setTypefaceBold(add_to_fav_cancel);

        add_to_fav_done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(favouritesList == null){
                    favouritesList = new FavouritesModel();
                }

                if(favouritesList.getCoupons() == null){
                    favouritesList.setCoupons(new ArrayList<ListOfCoupons>());
                    favouritesList.setListOfStores(new ArrayList<ListOfStores>());
                }

                favouritesList.getCoupons().add(couponData);
                favouritesList.getListOfStores().add(storeData);
                sharedPreferenceUtil.saveData(SharedPrefKeys.FAVOURITIES_ADDED,gson.toJson(favouritesList,FavouritesModel.class));
                addToFavoriteDialog.dismiss();
            }
        });

        add_to_fav_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                addToFavoriteDialog.dismiss();
            }
        });
        addToFavoriteDialog.setContentView(view);
    }

    private void generateActivateDealDialog()
    {
        activateDealDialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_activate_deal, null);
        activateDealDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activateDealDialog.setCanceledOnTouchOutside(false);

        TextView activate_deal_message = (TextView) view.findViewById(R.id.activate_deal_message);
        typeFaceClass.setTypefaceBold(activate_deal_message);

        Button activate_deal_done = (Button) view.findViewById(R.id.activate_deal_done);
        typeFaceClass.setTypefaceBold(activate_deal_done);
        activate_deal_done.setVisibility(View.VISIBLE);

        Button activate_deal_cancel = (Button) view.findViewById(R.id.activate_deal_cancel);
        typeFaceClass.setTypefaceBold(activate_deal_cancel);

        if(couponData.getCouponDeliveryType().equals("TIME_LIMIT")){
            activate_deal_message.setText(getResources().getString(R.string.confirm_activate_message));
        }else if (couponData.getCouponDeliveryType().equals("MANUAL_SWIPE")){
            activate_deal_message.setText(getResources().getString(R.string.ask_the_staff_message));
            activate_deal_done.setVisibility(View.INVISIBLE);
            SwipeButton my_swipe_button = (SwipeButton) view.findViewById(R.id.my_swipe_button);
            typeFaceClass.setTypefaceBold(my_swipe_button);
            my_swipe_button.setBackgroundResource(R.drawable.cancel_background);
            my_swipe_button.setText(getResources().getString(R.string.swipe_to_confirm));
            my_swipe_button.setVisibility(View.VISIBLE);
            SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
                @Override
                public void onSwipeConfirm() {
                    activateDealDialog.dismiss();
                    activateDealUsingManualSwipe();
                }
            };

            swipeButtonSettings
                    .setButtonPressText(getResources().getString(R.string.swipe_to_confirm))
                    .setGradientColor1(0xFF888888)
                    .setGradientColor2(0xFF666666)
                    .setGradientColor2Width(60)
                    .setGradientColor3(0xFF333333)
                    .setPostConfirmationColor(0xFF888888)
                    .setActionConfirmDistanceFraction(0.7)
                    .setActionConfirmText(getResources().getString(R.string.use_deal));

            if (my_swipe_button != null) {
                my_swipe_button.setSwipeButtonCustomItems(swipeButtonSettings);
            }
        }else{
            activate_deal_message.setText(getResources().getString(R.string.ask_the_staff_message));
        }


        activate_deal_done.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(couponData.getCouponDeliveryType().equals("TIME_LIMIT")){
                    activateDealUsingTimeLimit();
                }else{
                    activateDealUsingManualSwipe();
                }
                activateDealDialog.dismiss();
            }
        });

        activate_deal_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                activateDealDialog.dismiss();
            }
        });
        activateDealDialog.setContentView(view);
    }

    private void moveToEndScreen(){
        Intent moveToEndScreen = new Intent(DetailedCouponActivity.this,EndScreenActivity.class);
        startActivity(moveToEndScreen);
    }

    private void moveToPinCodeScreen(){
        Intent moveToPincodeScreen = new Intent(DetailedCouponActivity.this,PinCodeScreenActivity.class);
        moveToPincodeScreen.putExtra("couponTitle", couponData.getOfferTitle());
        moveToPincodeScreen.putExtra("Street", storeData.getStoreName() + storeData.getStreet());
        moveToPincodeScreen.putExtra("pin", pinCode);
        startActivity(moveToPincodeScreen);
    }

    private void activateDealUsingManualSwipe() {
        //call webservice to use Coupon
        new CustomAsyncTask(DetailedCouponActivity.this, couponID,storeData.getStoreId(),distanceToSendForUseCoupon+"",true, new OnLoadMoreListener<String>() {
            @Override
            public void onLoadMore(String resultModel) {
                ResponseUseCoupon useCouponResponse = getPinCode(resultModel);
                if(useCouponResponse.isAccept()){
                    removeCouponFromFavouriteAndMainList();
                    pinCode = useCouponResponse.getCode();
                    if(couponData.getCouponDeliveryType().equals("MANUAL_SWIPE")){
                        //Manual Swipe end screen
                        moveToEndScreen();
                    }else if (couponData.getCouponDeliveryType().equals("PINCODE")){
                        //Pin code screen
                        moveToPinCodeScreen();
                    }else{
                        moveToEndScreen();
                    }
                }else{
                    alertMessage.alertBoxForUseCouponError();
                }
            }
        }).execute();
    }

    private void activateDealUsingTimeLimit() {
        //call webservice to use Coupon
        //start timer if coupon is used successfully
        new CustomAsyncTask(DetailedCouponActivity.this, couponID,storeData.getStoreId(),distanceToSendForUseCoupon+"",true, new OnLoadMoreListener<String>() {
            @Override
            public void onLoadMore(String resultModel) {
                ResponseUseCoupon useCouponResponse = getPinCode(resultModel);
                if(useCouponResponse.isAccept()){
                    removeCouponFromFavouriteAndMainList();
                    detail_coupon_distance.setEnabled(false);
                    detail_coupon_distance.setBackgroundResource(R.drawable.cancel_rounded_bg);
                    counterMin = 10;
                    counterSec = 0;
                    detail_coupon_distance.setText(appUtility.getTimerCountDownValue(DetailedCouponActivity.this,counterMin,counterSec));
                    startTimerToUseDeal();
                }else{
                    alertMessage.alertBoxForUseCouponError();
                }
            }
        }).execute();
    }

    private void startTimerToUseDeal() {
        if (handler != null) {
            handler.removeCallbacks(runnable1);
            handler = null;
        }

        runnable1 = new Runnable(){
            public void run() {
                try{
                    if(counterSec == 0 && counterMin == 0){
                        if (handler != null) {
                            handler.removeCallbacks(runnable1);
                            handler = null;
                        }
                        moveToEndScreen();
                    }else if(counterSec == 0){
                        counterSec = 59;
                        --counterMin;
                    }else{
                        --counterSec;
                    }

                    detail_coupon_distance.setText(appUtility.getTimerCountDownValue(DetailedCouponActivity.this,counterMin,counterSec));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (handler != null)
                    handler.postDelayed(this, 1000);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable1,1000);
    }


    private void getActivatedDealsList(){
        if(sharedPreferenceUtil.getData(SharedPrefKeys.ACTIVATED_DEALS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG"),"").length()>0) {
            try {
                onj = new JSONObject(sharedPreferenceUtil.getData(SharedPrefKeys.ACTIVATED_DEALS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG"),""));
            } catch (JSONException e) {
            }
            if(onj != null) {
                activatedDealCouponList = gson.fromJson(onj.toString(), ActivatedDealCouponList.class);
            }
        }
        if(activatedDealCouponList == null) {
            activatedDealCouponList = new ActivatedDealCouponList();
        }
        if(activatedDealCouponList.getCoupons() == null) {
            activatedDealCouponList.setCoupons(new ArrayList<ListOfCoupons>());
        }
    }

    private ResponseUseCoupon getPinCode(String data) {
        Gson gson = new Gson();
        JSONObject onj = null;
        if(data!=null)
        {
            try{
                onj = new JSONObject(data);
            }catch (JSONException e) {
                return null;
            }

            if(onj != null) {
                return gson.fromJson(onj.toString(), ResponseUseCoupon.class);
            }else{
                return null;
            }

        }else{
            return null;
        }
    }

    private void removeCouponFromFavouriteAndMainList() {

        activatedDealCouponList.getCoupons().add(couponData);
        sharedPreferenceUtil.saveData(SharedPrefKeys.ACTIVATED_DEALS + sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG")
                , gson.toJson(activatedDealCouponList, ActivatedDealCouponList.class));


        if (checkIfCouponIsAlreadyAdded()) {
            for (int i = 0; i < favouritesList.getCoupons().size(); i++) {
                if (favouritesList.getCoupons().get(i).getCouponId().equals(couponID)) {
                    favouritesList.getCoupons().remove(i);
                    break;
                }
            }

            for (int i = 0; i < favouritesList.getListOfStores().size(); i++) {
                if (favouritesList.getListOfStores().get(i).getStoreId().equals(storeData.getStoreId())) {
                    favouritesList.getListOfStores().remove(i);
                }
            }
            sharedPreferenceUtil.saveData(SharedPrefKeys.FAVOURITIES_ADDED, gson.toJson(favouritesList, FavouritesModel.class));
        }

        if(fromWhere.equals(SharedPrefKeys.GET_COUPONS)){
            //save these lists again
            //delete from lists if this coupon exist
            if (filter.length() > 0) {
                for (int i = 0; i < getCouponsFilterData.getListOfCoupons().size(); i++) {
                    if (getCouponsFilterData.getListOfCoupons().get(i).getCouponId().equals(couponID)) {
                        getCouponsFilterData.getListOfCoupons().remove(i);
                        break;
                    }
                }
                sharedPreferenceUtil.saveData(SharedPrefKeys.GET_COUPONS +
                        sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + filter, gson.toJson(getCouponsFilterData, ResponseGetCoupons.class));
            }else{
                for (int i = 0; i < getCouponsData.getListOfCoupons().size(); i++) {
                    if (getCouponsData.getListOfCoupons().get(i).getCouponId().equals(couponID)) {
                        getCouponsData.getListOfCoupons().remove(i);
                        break;
                    }
                }
                sharedPreferenceUtil.saveData(SharedPrefKeys.GET_COUPONS +
                        sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG"), gson.toJson(getCouponsData, ResponseGetCoupons.class));
            }
        }else  if(fromWhere.equals(SharedPrefKeys.GET_CATEGORIES)){
            for (int i = 0; i < getCategoriesCouponsData.getListOfCoupons().size(); i++) {
                if (getCategoriesCouponsData.getListOfCoupons().get(i).getCouponId().equals(couponID)) {
                    getCategoriesCouponsData.getListOfCoupons().remove(i);
                    break;
                }
            }
            sharedPreferenceUtil.saveData(SharedPrefKeys.GET_CATEGORIES +
                    sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + filter, gson.toJson(getCategoriesCouponsData, ResponseGetCoupons.class));
        } else  if(fromWhere.equals(SharedPrefKeys.GET_BRANDEDCOUPONS)){
            for (int i = 0; i < getBrandedCouponsData.getListOfCoupons().size(); i++) {
                if (getBrandedCouponsData.getListOfCoupons().get(i).getCouponId().equals(couponID)) {
                    getBrandedCouponsData.getListOfCoupons().remove(i);
                    break;
                }
            }
            sharedPreferenceUtil.saveData(SharedPrefKeys.GET_BRANDEDCOUPONS +
                    sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE, "ENG") + filter, gson.toJson(getBrandedCouponsData, ResponseGetCoupons.class));
        }
    }

    private boolean checkIfCouponIsAlreadyAdded() {
        boolean isAdded = false;
        favouritesList = null;
        if(sharedPreferenceUtil.getData(SharedPrefKeys.FAVOURITIES_ADDED,"").length()>0) {
            try {
                onj = new JSONObject(sharedPreferenceUtil.getData(SharedPrefKeys.FAVOURITIES_ADDED,""));
            } catch (JSONException e) {
            }
            if(onj != null) {
                favouritesList = gson.fromJson(onj.toString(), FavouritesModel.class);
            }

            if(favouritesList != null && favouritesList.getCoupons() != null) {
                for (int i = 0; i < favouritesList.getCoupons().size();i++){
                    if(favouritesList.getCoupons().get(i).getCouponId().equals(couponID)){
                        isAdded = true;
                        break;
                    }
                }
            }
        }
        return isAdded;
    }

    @Override
    public void onBackPressed() {
        if(detail_coupon_facebook_holder.getVisibility() == View.VISIBLE){
            hideFacebookPostionLayout();
        }else if(detail_coupon_more_info.isSelected()){
            detail_coupon_more_info.setSelected(false);
            detail_coupon_main_holder.setVisibility(View.VISIBLE);
            detail_coupon_info_holder.setVisibility(View.INVISIBLE);
            map_icon.setVisibility(View.VISIBLE);
        }else{
            if (handler != null){
                handler.removeCallbacks(runnable1);
                handler = null;
                runnable1=null;
            }

            if (handlerForUpdatingDistance != null) {
                handlerForUpdatingDistance.removeCallbacks(runnableForUpdatingDistance);
                handlerForUpdatingDistance = null;
                runnableForUpdatingDistance = null;
            }
            if (handlerForToggling != null) {
                handlerForToggling.removeCallbacks(runnableForToggling);
                handlerForToggling = null;
            }
            if (handlerValidUntil != null) {
                handlerValidUntil.removeCallbacks(runnableValidUntil);
                handlerValidUntil = null;
            }
            if(!callBackHomeScreen) {
                super.onBackPressed();
            }else{
                Intent moveToHomeScreen = new Intent(DetailedCouponActivity.this,HomeScreenActivity.class);
                startActivity(moveToHomeScreen);
                finish();
            }
        }
    }

    private void hideFacebookPostionLayout() {
        detail_coupon_bottom_rl.setVisibility(View.VISIBLE);
        detail_coupon_facebook_holder.setVisibility(View.INVISIBLE);
        if (detail_coupon_more_info.isSelected()) {
            detail_coupon_info_holder.setVisibility(View.VISIBLE);
        } else {
            detail_coupon_main_holder.setVisibility(View.VISIBLE);
            map_icon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
