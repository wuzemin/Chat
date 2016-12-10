package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Min on 2016/12/9.
 */

public class LoginBean implements Parcelable{
    private String userid;
    private String token;
    private String nickname;
    private String portrait;


    public LoginBean(String userid, String token, String nickname, String portrait) {
        this.userid = userid;
        this.token = token;
        this.nickname = nickname;
        this.portrait = portrait;
    }

    protected LoginBean(Parcel in) {
        userid = in.readString();
        token = in.readString();
        nickname = in.readString();
        portrait = in.readString();
    }

    public static final Creator<LoginBean> CREATOR = new Creator<LoginBean>() {
        @Override
        public LoginBean createFromParcel(Parcel in) {
            return new LoginBean(in);
        }

        @Override
        public LoginBean[] newArray(int size) {
            return new LoginBean[size];
        }
    };

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userid);
        parcel.writeString(token);
        parcel.writeString(nickname);
        parcel.writeString(portrait);
    }
}
