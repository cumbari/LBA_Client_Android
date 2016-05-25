package com.moblyo.market.model;


import java.io.Serializable;

/**
 * Created by Rohit on 4/25/16.
 */
public class ResponseUseCoupon implements Serializable
{
	
     private boolean accept;
    private String couponId;
    private int usesLeft;
    private String code;
    private int value;
    private String storeId;
    private String storeText;

    public boolean isAccept() {
        return accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public int getUsesLeft() {
        return usesLeft;
    }

    public void setUsesLeft(int usesLeft) {
        this.usesLeft = usesLeft;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreText() {
        return storeText;
    }

    public void setStoreText(String storeText) {
        this.storeText = storeText;
    }
   

}
