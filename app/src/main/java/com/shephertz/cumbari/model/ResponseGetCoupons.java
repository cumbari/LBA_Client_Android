package com.shephertz.cumbari.model;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rohit on 4/25/16.
 */
public class ResponseGetCoupons implements Serializable
{
    private ArrayList<ListOfCoupons> ListOfCoupons;
	private ArrayList<ListOfStores> ListOfStores;
	private ArrayList<ListOfCategoryHits> ListOfCategoryHits;
	private ArrayList<ListOfBrandHits> ListOfBrandHits;
	private boolean MaxNumberReached;
    private int NumberOfCouponsInArea;


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

	public ArrayList<ListOfCategoryHits> getListOfCategoryHits() {
		return ListOfCategoryHits;
	}

	public void setListOfCategoryHits(ArrayList<ListOfCategoryHits> listOfCategoryHits) {
		this.ListOfCategoryHits = listOfCategoryHits;
	}

	public ArrayList<ListOfBrandHits> getListOfBrandHits() {
		return ListOfBrandHits;
	}

	public void setListOfBrandHits(ArrayList<ListOfBrandHits> listOfBrandHits) {
		this.ListOfBrandHits = listOfBrandHits;
	}

	public boolean isMaxNumberReached() {
		return MaxNumberReached;
	}

	public void setMaxNumberReached(boolean maxNumberReached) {
		MaxNumberReached = maxNumberReached;
	}

	public int getNumberOfCouponsInArea() {
		return NumberOfCouponsInArea;
	}

	public void setNumberOfCouponsInArea(int numberOfCouponsInArea) {
		NumberOfCouponsInArea = numberOfCouponsInArea;
	}
}
