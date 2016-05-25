package com.moblyo.market;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.moblyo.market.location.GetLocationFromGoogleClient;
import com.moblyo.market.utils.APPConstants;
import com.moblyo.market.utils.AppUtility;
import com.moblyo.market.utils.SharedPreferenceUtil;
import com.moblyo.market.utils.TypeFaceClass;

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

    public static class AlertMessage
    {
        /** The Constant GPS_CODE_FLAG. */
        private  final int GPS_CODE_FLAG = 3;

        private Context mContext;
        public AlertMessage(Context mContext)
        {
            this.mContext = mContext;
        }
        public void alertBox(String message,String title) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForNoConnection(final Activity activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.alert_title))
                    .setMessage(mContext.getResources().getString(R.string.message_no_connection))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if(activity!=null) {
                                activity.finish();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForNoDetailing(final Activity activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.alert_title))
                    .setMessage(mContext.getResources().getString(R.string.no_data))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if(activity!=null) {
                                activity.finish();
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForSelectedCategorgyError() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.alert_title_error))
                    .setMessage(mContext.getResources().getString(R.string.message_category_filter))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForSelectedBrandsError() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.alert_title_error))
                    .setMessage(mContext.getResources().getString(R.string.message_brand_filter))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForUseCouponError() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.use_coupon_error_title))
                    .setMessage(mContext.getResources().getString(R.string.use_coupon_error_message))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


        public void alertBoxForFavoriteAlreadyAdded() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.already_added_title))
                    .setMessage(mContext.getResources().getString(R.string.already_added_message))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForTooFarAwayFromStore() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.too_far_title))
                    .setMessage(mContext.getResources().getString(R.string.too_far_message))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxCannotAddGoogleCoupon() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.no_add_google_coupon_title))
                    .setMessage(mContext.getResources().getString(R.string.no_add_google_coupon_message))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForDistanceBeyondLimit() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.distance_beyond_limit_title))
                    .setMessage(mContext.getResources().getString(R.string.distance_beyond_limit_message))
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForValidityCase(String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(mContext.getResources().getString(R.string.offer_cannot_be_used))
                    .setMessage(message)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        public void alertBoxForNoLocation(String message, String title, final Activity activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int id) {
                              dialog.cancel();
                              activity.finish();
                         }
                     });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }
}
