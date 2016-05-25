package com.moblyo.market.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rohit on 4/25/16.
 */
public class ListOfCategoryHits implements Serializable
{
    private String categoryId;
    private int numberOfCoupons;
    


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getNumberOfCoupons() {
        return numberOfCoupons;
    }

    public void setNumberOfCoupons(int numberOfCoupons) {
        this.numberOfCoupons = numberOfCoupons;
    }

   

}
