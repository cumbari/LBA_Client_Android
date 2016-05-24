package com.shephertz.cumbari.model;

import java.io.Serializable;

/**
 * Created by Rohit on 4/25/16.
 */
public class CouponViewStatistic implements Serializable
{
   private String eventTime;
    private String couponId;
    private String storeId;
    private int distanceToStore;

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getDistanceToStore() {
        return distanceToStore;
    }

    public void setDistanceToStore(int distanceToStore) {
        this.distanceToStore = distanceToStore;
    }

   
}
