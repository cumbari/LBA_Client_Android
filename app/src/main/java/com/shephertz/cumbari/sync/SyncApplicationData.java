package com.shephertz.cumbari.sync;

import com.google.gson.Gson;
import com.shephertz.cumbari.model.ResponseGetCoupons;
import com.shephertz.cumbari.model.ResponseGetHostURL;
import com.shephertz.cumbari.utils.SharedPrefKeys;
import com.shephertz.cumbari.utils.SharedPreferenceUtil;
import com.shephertz.cumbari.utils.TimeIgnoringComparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Rohit on 4/26/2016.
 */
public class SyncApplicationData
{
    private SharedPreferenceUtil sharedPreferenceUtil;
    private JsonMethods jsonMetods;

    public SyncApplicationData(SharedPreferenceUtil sharedPreferenceUtil)
    {
        this.sharedPreferenceUtil = sharedPreferenceUtil;
        jsonMetods=new JsonMethods();
    }

    public ResponseGetHostURL getHostURLData(){
        ResponseGetHostURL responseGetHostURL = null;
        float latitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LATITUDE, 0f);
        float longitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LONGITUDE, 0f);
        String getHotURLString = jsonMetods.getHostURL(longitude,latitude);
        JSONObject onj = null;
        Gson gson = new Gson();
        if(getHotURLString!=null)
        {
            try{
                onj = new JSONObject(getHotURLString);
            }catch (JSONException e) {
            }
            if(onj != null) {
                responseGetHostURL = gson.fromJson(onj.toString(), ResponseGetHostURL.class);
            }
        }
        return responseGetHostURL;
    }
    public void syncData()
    {
            //get coupons,getcategories,getbrandedcoupon
            float latitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LATITUDE, 0f);
            float longitude = sharedPreferenceUtil.getData(SharedPrefKeys.WEBSERVICE_LONGITUDE, 0f);
            String clientId = sharedPreferenceUtil.getData(SharedPrefKeys.DEVICE_ID, "");
            String lang = sharedPreferenceUtil.getData(SharedPrefKeys.LANGUAGE,"ENG");
            int radiusInMeter = sharedPreferenceUtil.getData(SharedPrefKeys.RANGE,10000);
            int maxNo = sharedPreferenceUtil.getData(SharedPrefKeys.MAX_NUMBER,10);
            int batchNo = 1;
            String partnerId = "";
            String partnerRef = "";
            String categoriesFilter = "";
            int categoriesVersion = 1;

            String getCoupondata = jsonMetods.getCoupons(latitude, longitude, clientId, lang, radiusInMeter, maxNo, batchNo, partnerId, partnerRef, categoriesFilter);
            sharedPreferenceUtil.saveData(SharedPrefKeys.GET_COUPONS + lang, getCoupondata);
            String getCategoriesdata = jsonMetods.getCategories(categoriesVersion, lang);
            sharedPreferenceUtil.saveData(SharedPrefKeys.GET_CATEGORIES + lang, getCategoriesdata);
    }


}
