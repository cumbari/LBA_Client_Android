package com.shephertz.cumbari.model;

import com.moblyo.market.model.ListOfCoupons;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Komal on 5/6/16.
 */
public class ActivatedDealCouponList implements Serializable {
    private ArrayList<ListOfCoupons> coupons;


    public ArrayList<ListOfCoupons> getCoupons() {
        return coupons;
    }

    public void setCoupons(ArrayList<ListOfCoupons> coupons) {
        this.coupons = coupons;
    }
}
