package com.shephertz.cumbari.model;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Rohit on 4/25/16.
 */
public class ResponseFindCoupons implements Serializable
{
    private ArrayList<ListOfCoupons> listOfCoupons;
	private ArrayList<ListOfStores> listOfStores;
	private ArrayList<ListOfCategoryHits> listOfCategoryHits;
	private ArrayList<ListOfBrandHits> listOfBrandHits;
	private boolean MaxNumberReached;
    private int NumberOfCouponsInArea;


	public ArrayList<ListOfCoupons> getListOfCoupons() {
		return listOfCoupons;
	}

	public void setListOfCoupons(ArrayList<ListOfCoupons> listOfCoupons) {
		this.listOfCoupons = listOfCoupons;
	}

	public ArrayList<ListOfStores> getListOfStores() {
		return listOfStores;
	}

	public void setListOfStores(ArrayList<ListOfStores> listOfStores) {
		this.listOfStores = listOfStores;
	}

	public ArrayList<ListOfCategoryHits> getListOfCategoryHits() {
		return listOfCategoryHits;
	}

	public void setListOfCategoryHits(ArrayList<ListOfCategoryHits> listOfCategoryHits) {
		this.listOfCategoryHits = listOfCategoryHits;
	}

	public ArrayList<ListOfBrandHits> getListOfBrandHits() {
		return listOfBrandHits;
	}

	public void setListOfBrandHits(ArrayList<ListOfBrandHits> listOfBrandHits) {
		this.listOfBrandHits = listOfBrandHits;
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
