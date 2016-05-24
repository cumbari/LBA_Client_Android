package com.shephertz.cumbari.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.shephertz.cumbari.interfaces.OnLoadMoreListener;
import com.shephertz.cumbari.model.CouponViewStatistic;
import com.shephertz.cumbari.model.ResponseGetCoupons;
import com.shephertz.cumbari.utils.APPConstants;
import com.shephertz.cumbari.utils.AlertMessage;
import com.shephertz.cumbari.utils.AppUtility;
import com.shephertz.cumbari.utils.SharedPrefKeys;
import com.shephertz.cumbari.utils.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Rohit on 4/25/16.
 */
public class CustomAsyncTask extends AsyncTask<Void, Void, Integer>
{
    private ProgressDialog progressDialog = null;
    private String errorMessage;
    private Context mContext;
    private AppUtility appUtility;
    private JsonMethods jsonMetods;
    private OnLoadMoreListener  callback;
    private final String titleProgress="", msgProgress="\nPlease wait...";
    private SharedPreferenceUtil sharedPreferenceUtil;

    private float latitude ;
    private float longitude ;
    private String clientId ;
    private String lang ;
    private int radiusInMeter;
    private int maxNo;
    private int batchNo;
    private String partnerId = "";
    private String partnerRef = "";
    private String categoriesFilter = "";
    private int categoriesVersion = 1;

    private int currentWebServiceTobeUsed;
   // private ResponseGetCoupons resultCoupons;
    private String responseCoupon;

    private String couponID = "";
    private String storeID = "";
    private String distanceToStore = "";

    private boolean showProgress;
    private CouponViewStatistic mCouponViewStatistic;

    public CustomAsyncTask(Context context,int currentWebservice,String filter,int batchNo,boolean showProgress,OnLoadMoreListener onLoadMoreListener)
    {
        mContext=context;
        sharedPreferenceUtil=SharedPreferenceUtil.getInstance(context);
        currentWebServiceTobeUsed=currentWebservice;
        latitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LATITUDE, 0f);
        longitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LONGITUDE, 0f);
        clientId = sharedPreferenceUtil.getData(SharedPrefKeys.DEVICE_ID, "");
        lang = sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG");
        radiusInMeter = sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,10000);
        maxNo = sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,10);
        this.batchNo = batchNo;
        this.callback=onLoadMoreListener;
        this.categoriesFilter = filter;
        this.showProgress = showProgress;
    }

    public CustomAsyncTask(Context context,String couponID,boolean showProgress,OnLoadMoreListener onLoadMoreListener)
    {
        mContext=context;
        sharedPreferenceUtil=SharedPreferenceUtil.getInstance(context);
        currentWebServiceTobeUsed=APPConstants.GetCouponDetailWebservice;
        latitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LATITUDE, 0f);
        longitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LONGITUDE, 0f);
        clientId = sharedPreferenceUtil.getData(SharedPrefKeys.DEVICE_ID, "");
        lang = sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG");
        radiusInMeter = sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,10000);
        maxNo = sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,10);
        this.couponID = couponID;
        this.callback=onLoadMoreListener;
        this.showProgress = showProgress;
    }

    public CustomAsyncTask(Context context,String couponID,String storeID,String distanceToStore,boolean showProgress,OnLoadMoreListener onLoadMoreListener)
    {
        mContext=context;
        sharedPreferenceUtil=SharedPreferenceUtil.getInstance(context);
        currentWebServiceTobeUsed=APPConstants.UseCouponService;
        latitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LATITUDE, 0f);
        longitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LONGITUDE, 0f);
        clientId = sharedPreferenceUtil.getData(SharedPrefKeys.DEVICE_ID, "");
        lang = sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG");
        radiusInMeter = sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,10000);
        maxNo = sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,10);
        this.couponID = couponID;
        this.storeID = storeID;
        this.distanceToStore = distanceToStore;
        this.callback=onLoadMoreListener;
        this.showProgress = showProgress;
    }

    public CustomAsyncTask(Context context, CouponViewStatistic couponViewStatistic, OnLoadMoreListener onLoadMoreListener)
    {
        mContext=context;
        sharedPreferenceUtil=SharedPreferenceUtil.getInstance(context);
        currentWebServiceTobeUsed=APPConstants.StoreViewStatistics;
        clientId = sharedPreferenceUtil.getData(SharedPrefKeys.DEVICE_ID, "");
        this.mCouponViewStatistic = couponViewStatistic;
        this.callback=onLoadMoreListener;
        this.showProgress = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showProgress){
            progressDialog = ProgressDialog.show(mContext, titleProgress, msgProgress, false);
            progressDialog.setCancelable(false);
        }

        jsonMetods=new JsonMethods();
        appUtility = new AppUtility(mContext);
    }

    @Override
    protected Integer doInBackground(Void... params)
    {
        int ErrorCode=0;
        try {
            if (appUtility.isNetworkAvailable()) {
                switch (currentWebServiceTobeUsed) {
                    case APPConstants.GetCouponsWebservice:
                        responseCoupon = jsonMetods.getCoupons(latitude, longitude, clientId, lang, radiusInMeter, maxNo, batchNo, partnerId, partnerRef, categoriesFilter);

                        if (responseCoupon != null && responseCoupon.length() > 0) {
                            ErrorCode = 1;
                        } else {
                            ErrorCode = 0;
                        }
                        break;

                    case APPConstants.FindCouponsWebservice:
                        responseCoupon = jsonMetods.findCoupons(latitude, longitude, clientId, lang, radiusInMeter, maxNo, batchNo, partnerId, partnerRef, categoriesFilter);

                        if (responseCoupon != null && responseCoupon.length() > 0) {
                            ErrorCode = 1;
                        } else {
                            ErrorCode = 0;
                        }
                        break;

                    case APPConstants.GetCouponDetailWebservice:
                        responseCoupon = jsonMetods.getCoupon(couponID, lang);

                        if (responseCoupon != null && responseCoupon.length() > 0) {
                            ErrorCode = 1;
                        } else {
                            ErrorCode = 0;
                        }
                        break;

                    case APPConstants.GetSelectedCategoryWebservice:
                        responseCoupon = jsonMetods.getCoupons(latitude, longitude, clientId, lang, radiusInMeter, maxNo, batchNo, partnerId, partnerRef, categoriesFilter);

                        if (responseCoupon != null && responseCoupon.length() > 0) {
                            ErrorCode = 1;
                        } else {
                            ErrorCode = 0;
                        }
                        break;

                    case APPConstants.GetSelectedBrandWebservice:
                        responseCoupon = jsonMetods.getBrandedCoupon(latitude, longitude, clientId, lang, radiusInMeter, maxNo, batchNo, partnerId, partnerRef, categoriesFilter);

                        if (responseCoupon != null && responseCoupon.length() > 0) {
                            ErrorCode = 1;
                        } else {
                            ErrorCode = 0;
                        }
                        break;

                    case APPConstants.UseCouponService:
                        responseCoupon = jsonMetods.useCoupon(couponID, storeID, clientId, distanceToStore, partnerId, partnerRef);
                        if (responseCoupon != null && responseCoupon.length() > 0) {
                            ErrorCode = 1;
                        } else {
                            ErrorCode = 0;
                        }
                        break;

                    case APPConstants.StoreViewStatistics:
                        responseCoupon = jsonMetods.storeViewStatistic(clientId, mCouponViewStatistic);
                        if (responseCoupon != null && responseCoupon.length() > 0) {
                            ErrorCode = 1;
                        } else {
                            ErrorCode = 0;
                        }
                        break;
                }
            }
        }catch (Exception e){

        }
        return ErrorCode;
    }


    @Override
    protected void onPostExecute(Integer result)
    {
        super.onPostExecute(result);
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();

       switch (currentWebServiceTobeUsed)
        {
            case APPConstants.GetCouponsWebservice:
                setResultData(result,/*resultCoupons*/responseCoupon);
                break;
            case APPConstants.FindCouponsWebservice:
                setResultData(result,/*resultCoupons*/responseCoupon);
                break;
            case APPConstants.GetCouponDetailWebservice:
                setResultData(result,/*resultCoupons*/responseCoupon);
                break;
            case APPConstants.GetSelectedCategoryWebservice:
                setResultData(result,/*resultCoupons*/responseCoupon);
                break;
            case APPConstants.GetSelectedBrandWebservice:
                setResultData(result,/*resultCoupons*/responseCoupon);
                break;
            case APPConstants.UseCouponService:
                setResultData(result,/*resultCoupons*/responseCoupon);
                break;
            case APPConstants.StoreViewStatistics:
                setResultData(result,/*resultCoupons*/responseCoupon);
                break;
        }
    }

    private void setResultData(Integer result, Object resultModel)
    {
        if (result == 1){
             callback.onLoadMore(resultModel);
        } else{
            AlertMessage alertMessage = new AlertMessage(mContext);
            switch (currentWebServiceTobeUsed)
            {
                case APPConstants.GetCouponDetailWebservice:
                    alertMessage.alertBoxForNoConnection(null);
                    break;
                case APPConstants.GetSelectedCategoryWebservice:
                    alertMessage.alertBoxForSelectedCategorgyError();
                    break;
                case APPConstants.GetSelectedBrandWebservice:
                    alertMessage.alertBoxForSelectedBrandsError();
                    break;
                case APPConstants.UseCouponService:
                    alertMessage.alertBoxForUseCouponError();
                    break;

            }
        }
    }
}
