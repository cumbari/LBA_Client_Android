package com.moblyo.market;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.moblyo.market.interfaces.SyncAllDataCallback;
import com.moblyo.market.location.GetLocationFromGoogleClient;
import com.moblyo.market.model.ResponseGetHostURL;
import com.moblyo.market.sync.SyncApplicationData;
import com.moblyo.market.utils.SharedPrefKeys;
import com.moblyo.market.utils.SharedPreferenceUtil;
import com.moblyo.market.utils.TCLogger;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

public class SplashActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isSplash = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setAllDataFromDefaults();
        checkConnection();
        getDeviceID();
    }

    private void setAllDataFromDefaults() {
        sharedPreferenceUtil.saveData(SharedPrefKeys.PREVIOUS_LATITUDE,sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LATITUDE,0f));
        sharedPreferenceUtil.saveData(SharedPrefKeys.PREVIOUS_LONGITUDE,sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LONGITUDE,0f));
        //change language settings here
        try {
            if(!sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE_SET_FOR_FIRST_TIME,false)) {
                sharedPreferenceUtil.saveData(SharedPrefKeys.LANGUAGE_SET_FOR_FIRST_TIME, true);
                Locale current = getResources().getConfiguration().locale;
                if (current.equals(new Locale("sv", "SE"))) {
                    sharedPreferenceUtil.saveData(SharedPrefKeys.LANGUAGE, "SWE");
                } else if (current.equals(Locale.GERMAN)) {
                    sharedPreferenceUtil.saveData(SharedPrefKeys.LANGUAGE, "GER");
                }
            }
            appUtility.setLocale();
        }catch (Exception e){
        }
    }

    private void checkConnection() {
        if(!appUtility.isNetworkAvailable()) {
            alertMessage.alertBoxForNoConnection(SplashActivity.this);
        }else{

            getLocationFromGoogleClient = new GetLocationFromGoogleClient(SplashActivity.this, new SyncAllDataCallback() {
                @Override
                public void onSuccess(boolean isSuccess) {
                    //*********** Start syncing your data now **********************
                    if(sharedPreferenceUtil.getData(SharedPrefKeys.IS_CURRENT_POSITION, true) || sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LATITUDE,0f) == 0) {
                        sharedPreferenceUtil.saveData(SharedPrefKeys.WEBSERVICE_LATITUDE,sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LATITUDE,0f));
                        sharedPreferenceUtil.saveData(SharedPrefKeys.WEBSERVICE_LONGITUDE,sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LONGITUDE,0f));
                    }
                    //For 300m location change issue
                    sharedPreferenceUtil.saveData(SharedPrefKeys.PREVIOUS_LATITUDE,sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LATITUDE,0f));
                    sharedPreferenceUtil.saveData(SharedPrefKeys.PREVIOUS_LONGITUDE,sharedPreferenceUtil.getData(SharedPrefKeys.CURRENT_LONGITUDE,0f));

                    DownloadDatabase task = new DownloadDatabase();
                    task.execute("");
                }
            },null);
            getLocationFromGoogleClient.checkLocationSettings();
        }

    }

    private void getDeviceID() {
        //Saving the ClientID ONCE during the client application lifetime.
        if(sharedPreferenceUtil.getData(SharedPrefKeys.DEVICE_ID,"").length() == 0) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            sharedPreferenceUtil.saveData(SharedPrefKeys.DEVICE_ID, telephonyManager.getDeviceId());
        }
    }

    /**
     * The Class DownloadDatabase.
     */
    private class DownloadDatabase extends AsyncTask<String, Void, String> {

        @Override
        public void onPreExecute() {
            progressDialog = ProgressDialog.show(SplashActivity.this, getResources().getString(R.string.progress_title), getResources().getString(R.string.progress_message), false);
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onCancelled(String result) {
            super.onCancelled(result);
            System.out.println("cancel asynctask");
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "Completed";

            try {
                if(appUtility.isNetworkAvailable()){
                    //call syncing functions
                    ResponseGetHostURL getHostURLResponse = new SyncApplicationData(sharedPreferenceUtil).getHostURLData();
                    if(getHostURLResponse == null || getHostURLResponse.getUrl() == null){
                        return null;
                    }
                    new SyncApplicationData(sharedPreferenceUtil).syncData();
                }else{
                    return null;
                }
            } catch (Exception e){
                e.printStackTrace();
                response = "Failed";
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (progressDialog != null){
                progressDialog.dismiss();
            }

            if(result == null){
                alertMessage.alertBoxForNoConnection(SplashActivity.this);
            }else {
                moveToHomeScreen();
            }
        }
    }

    private void moveToHomeScreen(){
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data == null){
            Intent moveToHomeScreen = new Intent(SplashActivity.this,HomeScreenActivity.class);
            startActivity(moveToHomeScreen);
            finish();
         }else {
            try {
                String url1 = data.toString();
                String[] arr = url1.split("&");
                String[] array = arr[0].split("\\?");
                String[] arrayForServiceName = array[1].split("=");
                String serviceName = arrayForServiceName[1];

                if (serviceName.equals("getCoupon")) {
                    String couponIDWithName = arr[1];
                    String[] arr1 = couponIDWithName.split("=");
                    String valueOfCouponId = arr1[1];

                    Intent moveToDetailing = new Intent(SplashActivity.this, DetailedCouponActivity.class);
                    moveToDetailing.putExtra("fromWhere", SharedPrefKeys.GET_COUPONS);
                    moveToDetailing.putExtra("couponID", valueOfCouponId);
                    moveToDetailing.putExtra("filter", "");
                    moveToDetailing.putExtra("distanceToShow", "");
                    moveToDetailing.putExtra("callBackHomeScreen", true);
                    startActivity(moveToDetailing);
                    finish();

                }else if (serviceName.equals("getBrandedCoupons")) {
                    String couponIDWithName = arr[1];
                    String[] arr1 = couponIDWithName.split("=");
                    String brandsFilter = arr1[1];
                    Intent moveToCategorization = new Intent(SplashActivity.this,CouponsInSelectedBrandsActivity.class);
                    moveToCategorization.putExtra("brandName",brandsFilter);
                    moveToCategorization.putExtra("callBackHomeScreen", true);
                    startActivity(moveToCategorization);
                    finish();
                }else {
                    Intent moveToHomeScreen = new Intent(SplashActivity.this,HomeScreenActivity.class);
                    startActivity(moveToHomeScreen);
                    finish();
                }
            }catch (Exception e){
                Intent moveToHomeScreen = new Intent(SplashActivity.this,HomeScreenActivity.class);
                startActivity(moveToHomeScreen);
                finish();
            }
        }
    }

   /******************  Generating key hash for FacebookApp ID   ******************
   private void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.moblyo.market", PackageManager.GET_SIGNATURES); //Your package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("KeyHash:"+Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }
    *******************  Generating key hash for FacebookApp ID   ******************/
}
