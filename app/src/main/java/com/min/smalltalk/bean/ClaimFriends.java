package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Min on 2017/1/5.
 */

public class ClaimFriends implements Parcelable {
    private String userId;
    private String nickName;
    private String portraitUri;
    private String question;
    private String answer;
    private int status;

    protected ClaimFriends(Parcel in) {
        userId = in.readString();
        nickName = in.readString();
        portraitUri = in.readString();
        question = in.readString();
        answer = in.readString();
        status = in.readInt();
    }

    public static final Creator<ClaimFriends> CREATOR = new Creator<ClaimFriends>() {
        @Override
        public ClaimFriends createFromParcel(Parcel in) {
            return new ClaimFriends(in);
        }

        @Override
        public ClaimFriends[] newArray(int size) {
            return new ClaimFriends[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(nickName);
        parcel.writeString(portraitUri);
        parcel.writeString(question);
        parcel.writeString(answer);
        parcel.writeInt(status);
    }
}
