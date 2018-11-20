package com.sagra.stylemaker_v1.data;

import java.io.Serializable;

public class StyleData  implements Serializable {
        private  String mUid = null;
        private String mNumber = null;
        private String mCodinumber = null;
        private String mSeason = null;
        private String mCoditag = null;
        private String mClothtag = null;



        public StyleData(String aUid, String aNumber,String aCodinumber, String aSeason, String aCoditag, String aClothtag){
            this.mUid = aUid;
            this.mNumber = aNumber;
            this.mCodinumber = aCodinumber;
            this.mSeason = aSeason;
            this.mCoditag = aCoditag;
            this.mClothtag = aClothtag;
        }
        public String getUid(){ return this.mUid; }
        public String getNumber(){ return this.mNumber; }
        public String getCodinumber(){ return this.mCodinumber; }
        public String getSeason(){ return this.mSeason; }
        public String getCoditag(){ return this.mCoditag; }
        public String getClothtag(){ return this.mClothtag; }

        public String[] getCDataArray(){
            String[] cData = {
                    this.mUid,
                    this.mNumber,
                    this.mCodinumber,
                    this.mSeason,
                    this.mCoditag,
                    this.mClothtag
            };
            return cData;
        }
}


