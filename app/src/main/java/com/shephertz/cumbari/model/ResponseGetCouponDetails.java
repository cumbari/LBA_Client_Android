package com.moblyo.market.model;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rohit on 4/25/16.
 */
public class ResponseGetCouponDetails implements Serializable
{
    private ListOfCoupons coupon;
	private ListOfStores storeInfo;


	public ListOfCoupons getCoupon() {
		return coupon;
	}

	public void setCoupon(ListOfCoupons coupon) {
		this.coupon = coupon;
	}

	public ListOfStores getStoreInfo() {
		return storeInfo;
	}

	public void setStoreInfo(ListOfStores storeInfo) {
		this.storeInfo = storeInfo;
	}
}
