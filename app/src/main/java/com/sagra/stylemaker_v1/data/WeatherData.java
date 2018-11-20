package com.sagra.stylemaker_v1.data;

public class WeatherData {
    private String mDate = null;
    private String mMax = null;
    private String mMin= null;
    private String mCode = null;

    public WeatherData(String aDate, String aMax, String aMin, String aCode){

        this.mDate = aDate;
        this.mMax = aMax;
        this.mMin = aMin;
        this.mCode = aCode;
    }

    public String getDate(){
        return mDate;
    }
    public String getMax(){
        return mMax;
    }
    public String getMin(){
        return mMin;
    }
    public String getCode(){
        return mCode;
    }

    public String[] getWDataArray(){
        String[] wData = {
                this.mDate,
                this.mMax,
                this.mMin,
                this.mCode
        };
        return wData;
    }
}
