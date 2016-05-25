package com.moblyo.market.model;


import java.io.Serializable;

/**
 * Created by Rohit on 4/25/16.
 */
public class ListOfCategories implements Serializable
{
    private String categoryId;
    private String categoryName;
    private String smallImage;
    private int version ;
   

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

  


}
