package com.sagra.stylemaker_v1.data;

public class UserData {
    // mInfo는 옷이 저장된 path, mDetail1은 set3의 정보
    private String mUid = null;
    private String mUserid = null;
    private String mName = null;
    private String mEmail = null;
    private String mGender = null;
    private String mCreated = null;
    private String mProfile = null;
    public UserData(String aUid, String aUserid, String aName, String aEmail, String aGender, String aCreated, String aProfile){
        this.mUid = aUid;
        this.mUserid = aUserid;
        this.mName = aName;
        this.mEmail = aEmail;
        this.mGender = aGender;
        this.mCreated = aCreated;
        this.mProfile = aProfile;
    }

    public String getUid(){ return this.mUid; }
    public String getUserid(){ return this.mUserid; }
    public String getName(){return this.mName; }
    public String getEmail(){return this.mEmail; }
    public String getGender(){return this.mGender; }
    public String getCreated(){return this.mCreated; }
    public String getProfile(){return this.mProfile; }

    public String[] getcDataArray(){
        String[] cData = {
                this.mUid,
                this.mUserid,
                this.mName,
                this.mEmail,
                this.mGender,
                this.mCreated,
                this.mProfile
        };
        return cData;
    }
}
