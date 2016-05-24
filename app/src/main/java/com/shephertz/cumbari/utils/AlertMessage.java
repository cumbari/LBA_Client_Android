package com.shephertz.cumbari.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.shephertz.cumbari.R;

public class AlertMessage
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
