package com.shephertz.cumbari.model;

import java.io.Serializable;

/**
 * Created by Rohit on 4/25/16.
 */
public class LimitPeriodList implements Serializable
{
    private int endTime;
    private int startTime;
    private String validDay;

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getValidDay() {
        return validDay;
    }

    public void setValidDay(String validDay) {
        this.validDay = validDay;
    }
}
