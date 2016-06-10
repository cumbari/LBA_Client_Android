package com.moblyo.market.interfaces;

/**
 * Created by Komal on 07/05/16.
 */
public interface SortCouponsByDistanceCallback {
    /** @param refresh  true: Only sort the data
     *                  false: sync new data */
    public void refreshData(boolean refresh);
}
