package com.shephertz.cumbari;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shephertz.cumbari.location.GetLocationFromGoogleClient;
import com.shephertz.cumbari.utils.APPConstants;
import com.shephertz.cumbari.utils.AlertMessage;
import com.shephertz.cumbari.utils.AppUtility;
import com.shephertz.cumbari.utils.SharedPreferenceUtil;
import com.shephertz.cumbari.utils.TypeFaceClass;

public class BaseActivity extends AppCompatActivity{

    public GetLocationFromGoogleClient getLocationFromGoogleClient;

    public SharedPreferenceUtil sharedPreferenceUtil;
    public AlertMessage alertMessage;
    public AppUtility appUtility;
    public TypeFaceClass typeFaceClass;
    public boolean isSplash = false;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initObjects();
    }

    private void initObjects() {
        sharedPreferenceUtil = SharedPreferenceUtil.getInstance(BaseActivity.this);
        alertMessage = new AlertMessage(BaseActivity.this);
        appUtility = new AppUtility(BaseActivity.this);
        typeFaceClass = new TypeFaceClass(BaseActivity.this);
        if(!isSplash){
             getLocationFromGoogleClient = new GetLocationFromGoogleClient(BaseActivity.this,null);
             getLocationFromGoogleClient.checkLocationSettings();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getLocationFromGoogleClient != null) {
            getLocationFromGoogleClient.mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getLocationFromGoogleClient != null && getLocationFromGoogleClient.mGoogleApiClient.isConnected() && getLocationFromGoogleClient.mRequestingLocationUpdates) {
            getLocationFromGoogleClient.startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getLocationFromGoogleClient != null && getLocationFromGoogleClient.mGoogleApiClient.isConnected()) {
            getLocationFromGoogleClient.stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(getLocationFromGoogleClient != null) {
            getLocationFromGoogleClient.mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case APPConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if(getLocationFromGoogleClient != null) {
                            getLocationFromGoogleClient.startLocationUpdates();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        if(isSplash) {
                            finish();
                        }
                        break;
                }
                break;
        }
    }

}
