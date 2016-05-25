/*
 * Added by Amit Sharma.
 */
package com.moblyo.market.utils;

import com.moblyo.market.location.Coordinate;
import com.moblyo.market.model.ListOfCoupons;

import java.util.Comparator;

/**
 * The Class SortDataTableByDistanceFormatter.
 */
public class SortDataTableByDistanceFormatter implements
		Comparator<ListOfCoupons> {


	@Override
	public int compare(ListOfCoupons object1,
					   ListOfCoupons object2) {

		if(object1.getDistanceForSort().length() >0 && object2.getDistanceForSort().length() > 0) {
			float DistanceForSort1 = 0;
			float DistanceForSort2 = 0;

			object1.setDistanceForSort(object1.getDistanceForSort().replace(",","."));
			object2.setDistanceForSort(object2.getDistanceForSort().replace(",","."));

			try {
				DistanceForSort1 = Float.parseFloat(object1.getDistanceForSort().split(" ")[0]);
				DistanceForSort2 = Float.parseFloat(object2.getDistanceForSort().split(" ")[0]);

				String unit1 = object1.getDistanceForSort().split(" ")[1];
				String unit2 = object2.getDistanceForSort().split(" ")[1];

				if (unit1.equals("m") || unit1.equals("km")) {
					if (unit1.equals("km")) {
						DistanceForSort1 *= 1000;
					}
					if (unit2.equals("km")) {
						DistanceForSort2 *= 1000;
					}
				} else {
					if (unit1.equals("ft")) {
						DistanceForSort1 /= 5280;
					}
					if (unit2.equals("ft")) {
						DistanceForSort2 /= 5280;
					}
				}
			}catch (Exception e){

			}

			return Float.compare(DistanceForSort1, DistanceForSort2);
		}else{
			return 0;
		}
	}

}
