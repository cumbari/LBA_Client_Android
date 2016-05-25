package com.moblyo.market.model;

import java.io.Serializable;

/**
 * Created by Rohit on 4/25/16.
 */
public class ResponseGetHostURL implements Serializable
{

    private String url;
    private int versionOfCategories;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersionOfCategories() {
        return versionOfCategories;
    }

    public void setVersionOfCategories(int versionOfCategories) {
        this.versionOfCategories = versionOfCategories;
    }

   


}
