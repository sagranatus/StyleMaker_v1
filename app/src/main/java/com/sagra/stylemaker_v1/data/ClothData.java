package com.sagra.stylemaker_v1.data;

import java.io.Serializable;

public class ClothData implements Serializable {
    // mInfo는 옷이 저장된 path, mDetail1은 set3의 정보
    private  String mUid = null;
    private String mSeason = null;
    private String mType = null;
    private String mInfo = null;
    private String mDetail1 = null;
    private String mDetail2 = null;

    public ClothData(String aUid, String aSeason, String aType, String aInfo, String aDetail1, String aDetail2){
        this.mUid = aUid;
        this.mSeason = aSeason;
        this.mType = aType;
        this.mInfo = aInfo;
        this.mDetail1 = aDetail1;
        this.mDetail2 = aDetail2;
    }

    public String getUid(){ return this.mUid; }
    public String getSeason(){ return this.mSeason; }
    public String getType(){ return this.mType; }
    public String getInfo(){return this.mInfo; }
    public String getDetail1(){return this.mDetail1; }
    public String getDetail2(){return this.mDetail2; }

    public String[] getcDataArray(){
        String[] cData = {
                this.mUid,
                this.mSeason,
                this.mType,
                this.mInfo,
                this.mDetail1,
                this.mDetail2
        };
        return cData;
    }
}
