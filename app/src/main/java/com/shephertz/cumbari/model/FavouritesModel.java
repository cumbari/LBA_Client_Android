package com.shephertz.cumbari.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohit on 4/25/16.
 */
 
public class FavouritesModel implements Serializable
{
    private ArrayList<ListOfCoupons> coupons;
    private ArrayList<ListOfStores> ListOfStores;

    public ArrayList<ListOfCoupons> getCoupons() {
        return coupons;
    }

    public void setCoupons(ArrayList<ListOfCoupons> coupons) {
        this.coupons = coupons;
    }

    public ArrayList<com.shephertz.cumbari.model.ListOfStores> getListOfStores() {
        return ListOfStores;
    }

    public void setListOfStores(ArrayList<com.shephertz.cumbari.model.ListOfStores> listOfStores) {
        ListOfStores = listOfStores;
    }
}
