package com.moblyo.market.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rohit on 4/25/16.
 */
 
public class ResponseGetCategories implements Serializable
{

    private int version;
    private ArrayList<ListOfCategories> listOfCategories;


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ArrayList<ListOfCategories> getListOfCategories() {
        return listOfCategories;
    }

    public void setListOfCategories(ArrayList<ListOfCategories> listOfCategories) {
        this.listOfCategories = listOfCategories;
    }




}
