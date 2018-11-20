package com.sagra.stylemaker_v1.data;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CodiData implements Serializable, Comparable<CodiData> {
    private  String mUid = null;
    private String mNumber = null;
    private String mSeason = null;
    private String mType = null;
    private String mTop = null;
    private String mBottom = null;
    private String mShoes = null;
    private String mOuter = null;
    private String mBag = null;
    private String mAccessories = null;
    private String mTag = null;

    public CodiData(String aUid, String aNumber, String aSeason, String aType, String aTop, String aBottom, String aShoes, String aOuter, String aBag, String aAccessories, String aTag){
        this.mUid = aUid;
        this.mNumber = aNumber;
        this.mSeason = aSeason;
        this.mType = aType;
        this.mTop = aTop;
        this.mBottom = aBottom;
        this.mShoes = aShoes;
        this.mOuter = aOuter;
        this.mBag = aBag;
        this.mAccessories = aAccessories;
        this.mTag = aTag;
    }

    public String getUid(){ return this.mUid; }
    public String getNumber(){ return this.mNumber; }
    public String getSeason(){ return this.mSeason; }
    public String getType(){ return this.mType; }
    public String getTop(){ return this.mTop; }
    public String getBottom(){ return this.mBottom; }
    public String getShoes(){ return this.mShoes; }
    public String getOuter(){ return this.mOuter; }
    public String getBag(){ return this.mBag; }
    public String getAccessories(){ return this.mAccessories; }
    public String getTag(){ return this.mTag; }

    public String[] getCDataArray(){
        String[] cData = {
                this.mUid,
                this.mNumber,
                this.mSeason,
                this.mType,
                this.mTop,
                this.mBottom,
                this.mShoes,
                this.mOuter,
                this.mBag,
                this.mAccessories,
                this.mTag
        };
        return cData;
    }


    @Override
    public int compareTo(@NonNull CodiData codiData) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(codiData.mNumber);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = new SimpleDateFormat("yyyy-MM-dd").parse(this.mNumber);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date.getTime()>date2.getTime()) {
            return -1;
        } else if (date.getTime()>date2.getTime()) {
            return 0;
        } else {
            return 1;

        }
    }
}


