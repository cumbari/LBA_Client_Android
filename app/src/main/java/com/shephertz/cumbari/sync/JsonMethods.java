package com.shephertz.cumbari.sync;
import com.shephertz.cumbari.model.CouponViewStatistic;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Rohit on 4/25/16.
 */
public class JsonMethods
{


    public String getHostURL(float longitude,float latitude)
    {
        JSONParser parser = new JSONParser();
		String url=ApiUrls.GETHOSTURL + "latitude="+latitude+"&longitude="+longitude;
        return parser.getJSONFromUrlGetMethod(url);

    }

    public String getCategories(int categoriesVersion,String lang)
    {
       JSONParser parser = new JSONParser();
       String url=ApiUrls.GETCATEGORIES + "categoriesVersion="+categoriesVersion+"&lang="+lang;
       return parser.getJSONFromUrlGetMethod(url);
    }

    public String getCoupons(float latitude,float longitude,String clientId,String lang,int radiusInMeter,int maxNo,int batchNo,String partnerId,String partnerRef,String categoriesFilter)
    {
        if(categoriesFilter!= null && categoriesFilter.length() > 0){
            categoriesFilter = categoriesFilter.replace(" ","%20");
            categoriesFilter = categoriesFilter.replace("&","%26");
        }
        JSONParser parser = new JSONParser();
        String url=ApiUrls.GETCOUPONS + "latitude="+latitude+"&longitude="+longitude+"&clientId="+clientId+"&lang="+lang +"&radiousInMeter="+radiusInMeter+"&maxNo="+maxNo+"&batchNo="+batchNo+"&categoriesFilter="+categoriesFilter;

       return parser.getJSONFromUrlGetMethod(url);
    }

  public String getBrandedCoupon(float latitude,float longitude,String clientId,String lang,int radiusInMeter,int maxNo,int batchNo,String partnerId,String partnerRef,String categoriesFilter)
    {
        if(categoriesFilter!= null && categoriesFilter.length() > 0){
            categoriesFilter = categoriesFilter.replace(" ","%20");
            categoriesFilter = categoriesFilter.replace("&","%26");
        }
        JSONParser parser = new JSONParser();
        String url=ApiUrls.GETBRANDEDCOUPONS + "latitude="+latitude+"&longitude="+longitude+"&clientId="+clientId+"&lang="+lang +"&radiousInMeter="+radiusInMeter+"&maxNo="+maxNo+"&batchNo="+batchNo+"&brandsFilter="+categoriesFilter;
       return parser.getJSONFromUrlGetMethod(url);
    }


    public String findCoupons(float latitude,float longitude,String clientId,String lang,int radiusInMeter,int maxNo,int batchNo,String partnerId,String partnerRef,String searchWords)
    {
      JSONParser parser = new JSONParser();
        String url=ApiUrls.FINDCOUPONS + "latitude="+latitude+"&longitude="+longitude+"&clientId="+clientId+"&lang="+lang +"&radiousInMeter="+radiusInMeter+"&maxNo="+maxNo+"&batchNo="+batchNo+"&searchWords="+searchWords;
       return parser.getJSONFromUrlGetMethod(url);
    }


    public String useCoupon(String couponId,String storeId,String clientId,String distanceToStore,String partnerId,String partnerRef)
    {
		JSONParser parser = new JSONParser();
        String url;
        if(partnerId != null && partnerId.length() > 0) {
            url = ApiUrls.USECOUPON + "couponId=" + couponId + "&storeId=" + storeId + "&clientId=" + clientId + "&distanceToStore=" + distanceToStore + "&partnerId=" + partnerId + "&partnerRef=" + partnerRef;
        }else{
            url = ApiUrls.USECOUPON + "couponId=" + couponId + "&storeId=" + storeId + "&clientId=" + clientId + "&distanceToStore=" + distanceToStore;
        }
        return parser.getJSONFromUrlGetMethod(url);
    }

    public String getCoupon(String couponId,String lang)
    {
        JSONParser parser = new JSONParser();
        String url=ApiUrls.GETCOUPON + "couponId="+couponId+"&lang="+lang;
       return parser.getJSONFromUrlGetMethod(url);
    }


    public String storeViewStatistic(String clientId,CouponViewStatistic couponViewStatistic)
    {
        //couponViewStatisticList=[{eventTime:2016-05-12 16:38,couponId:bdd3eaea-e4e2-a165-73c6-accdd77c893d,storeId:dd3b9658-9459-d13b-db8e-5f003fbd98e7,distanceToStore:7}]
        //couponViewStatisticList=[{eventTime:"2016-05-12 16:15",couponId:"bdd3eaea-e4e2-a165-73c6-accdd77c893d",storeId:"dd3b9658-9459-d13b-db8e-5f003fbd98e7",distanceToStore:7}]&token=cumba4ever
         JSONParser parser = new JSONParser();

        String query = null;
        try {
            query = URLEncoder.encode("[{eventTime:\""+couponViewStatistic.getEventTime()+
                    "\",couponId:\""+couponViewStatistic.getCouponId()+
                    "\",storeId:\""+couponViewStatistic.getStoreId()+
                    "\",distanceToStore:"+Float.parseFloat(couponViewStatistic.getDistanceToStore()+"")+"}]","UTF-8");
            String url=ApiUrls.STOREVIEWSTATISTIC+"clientId="+clientId+"&couponViewStatisticList=" +query;
            return parser.getJSONFromUrlGetMethod(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
       return  null;
     }
}
//https://market.cumbari.com/CouponServer/clientapi/storeViewStatistic?apiVersion=1&token=cumba4ever&clientId%3D352840060859827%26couponViewStatisticList%3D%5B%7BeventTime%3A%222016-05-13+12%3A01%22%2CcouponId%3A%227308aefd-5acd-018f-eb3a-eafee05734d5%22%2CstoreId%3A%225409f672-ff7c-b1b8-02b8-42d49ff8f76a%22%2CdistanceToStore%3A2413.0%7D%5D