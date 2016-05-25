package com.moblyo.market.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rohit on 4/25/16.
 */
public class ListOfCoupons implements Serializable
{
     private String brandIcon;
    private int distanceToStore;
    private String brandName;
    private String categoryId;
    private String couponDeliveryType;
    private String couponId;
    private boolean  isSponsored;
    private String endOfPublishing ;
    private String largeImage;
    private String offerSlogan;
    private String offerTitle;
    private String offerType;
    private String productInfoLink;
    private String  smallImage ;
    private String startOfPublishing;
    private String storeId;
    private String validFrom;
    private String  viewOpt ;
    private String  distanceForSort = "";

    private ArrayList<LimitPeriodList> limitPeriodList;

    public String getBrandIcon() {
        return brandIcon;
    }

    public void setBrandIcon(String brandIcon) {
        this.brandIcon = brandIcon;
    }

    public int getDistanceToStore() {
        return distanceToStore;
    }

    public void setDistanceToStore(int distanceToStore) {
        this.distanceToStore = distanceToStore;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCouponDeliveryType() {
        return couponDeliveryType;
    }

    public void setCouponDeliveryType(String couponDeliveryType) {
        this.couponDeliveryType = couponDeliveryType;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getEndOfPublishing() {
        return endOfPublishing;
    }

    public void setEndOfPublishing(String endOfPublishing) {
        this.endOfPublishing = endOfPublishing;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    public String getOfferSlogan() {
        return offerSlogan;
    }

    public void setOfferSlogan(String offerSlogan) {
        this.offerSlogan = offerSlogan;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getProductInfoLink() {
        return productInfoLink;
    }

    public void setProductInfoLink(String productInfoLink) {
        this.productInfoLink = productInfoLink;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getStartOfPublishing() {
        return startOfPublishing;
    }

    public void setStartOfPublishing(String startOfPublishing) {
        this.startOfPublishing = startOfPublishing;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getViewOpt() {
        return viewOpt;
    }

    public void setViewOpt(String viewOpt) {
        this.viewOpt = viewOpt;
    }

    public ArrayList<LimitPeriodList> getLimitPeriodList() {
        return limitPeriodList;
    }

    public void setLimitPeriodList(ArrayList<LimitPeriodList> limitPeriodList) {
        this.limitPeriodList = limitPeriodList;
    }

    public boolean isSponsored() {
        return isSponsored;
    }

    public void setSponsored(boolean sponsored) {
        isSponsored = sponsored;
    }


    public String getDistanceForSort() {
        return distanceForSort;
    }

    public void setDistanceForSort(String distanceForSort) {
        this.distanceForSort = distanceForSort;
    }
}
