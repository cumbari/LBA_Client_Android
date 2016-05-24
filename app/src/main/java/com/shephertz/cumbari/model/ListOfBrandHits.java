package com.shephertz.cumbari.model;

import java.io.Serializable;

/**
 * Created by Rohit on 4/25/16.
 */
public class ListOfBrandHits implements Serializable
{
	 private String brandIcon;
    private String brandName;
    private int numberOfCoupons;

    public String getBrandIcon() {
        return brandIcon;
    }

    public void setBrandIcon(String brandIcon) {
        this.brandIcon = brandIcon;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getNumberOfCoupons() {
        return numberOfCoupons;
    }

    public void setNumberOfCoupons(int numberOfCoupons) {
        this.numberOfCoupons = numberOfCoupons;
    }

}
