package com.shephertz.cumbari.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rohit on 4/25/16.
 */
public class ResponseGetBrandedCoupons implements Serializable
{
    private ArrayList<ListOfCoupons> ListOfCoupons;
	private ArrayList<ListOfStores> ListOfStores;
	private boolean MaxNumberReached;
   


	public ArrayList<ListOfCoupons> getListOfCoupons() {
		return ListOfCoupons;
	}

	public void setListOfCoupons(ArrayList<ListOfCoupons> listOfCoupons) {
		this.ListOfCoupons = listOfCoupons;
	}

	public ArrayList<ListOfStores> getListOfStores() {
		return ListOfStores;
	}

	public void setListOfStores(ArrayList<ListOfStores> listOfStores) {
		this.ListOfStores = listOfStores;
	}

	
	public boolean isMaxNumberReached() {
		return MaxNumberReached;
	}

	public void setMaxNumberReached(boolean maxNumberReached) {
		MaxNumberReached = maxNumberReached;
	}

	
}
