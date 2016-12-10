package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Min on 2016/12/6.
 */

public class GroupFlexible implements Parcelable{
    private String flexibleId;
    private String flexibleName;
    private String flexiblePortrait;
    private String flexibleStartTime;
    private String flexibleEndTime;
    private String flexiblePlace;
    private String flexibleContent;
    private List<GroupMember> flexibleList;

    public GroupFlexible(String flexibleId, String flexibleName, String flexiblePortrait, String flexibleStartTime, String flexibleEndTime, String flexiblePlace, String flexibleContent) {
        this.flexibleId = flexibleId;
        this.flexibleName = flexibleName;
        this.flexiblePortrait = flexiblePortrait;
        this.flexibleStartTime = flexibleStartTime;
        this.flexibleEndTime = flexibleEndTime;
        this.flexiblePlace = flexiblePlace;
        this.flexibleContent = flexibleContent;
    }

    protected GroupFlexible(Parcel in) {
        flexibleId = in.readString();
        flexibleName = in.readString();
        flexiblePortrait = in.readString();
        flexibleStartTime = in.readString();
        flexibleEndTime = in.readString();
        flexiblePlace = in.readString();
        flexibleContent = in.readString();
        flexibleList = in.createTypedArrayList(GroupMember.CREATOR);
    }

    public static final Creator<GroupFlexible> CREATOR = new Creator<GroupFlexible>() {
        @Override
        public GroupFlexible createFromParcel(Parcel in) {
            return new GroupFlexible(in);
        }

        @Override
        public GroupFlexible[] newArray(int size) {
            return new GroupFlexible[size];
        }
    };

    public String getFlexibleId() {
        return flexibleId;
    }

    public void setFlexibleId(String flexibleId) {
        this.flexibleId = flexibleId;
    }

    public String getFlexibleName() {
        return flexibleName;
    }

    public void setFlexibleName(String flexibleName) {
        this.flexibleName = flexibleName;
    }

    public String getFlexiblePortrait() {
        return flexiblePortrait;
    }

    public void setFlexiblePortrait(String flexiblePortrait) {
        this.flexiblePortrait = flexiblePortrait;
    }

    public String getFlexibleStartTime() {
        return flexibleStartTime;
    }

    public void setFlexibleStartTime(String flexibleStartTime) {
        this.flexibleStartTime = flexibleStartTime;
    }

    public String getFlexibleEndTime() {
        return flexibleEndTime;
    }

    public void setFlexibleEndTime(String flexibleEndTime) {
        this.flexibleEndTime = flexibleEndTime;
    }

    public String getFlexiblePlace() {
        return flexiblePlace;
    }

    public void setFlexiblePlace(String flexiblePlace) {
        this.flexiblePlace = flexiblePlace;
    }

    public String getFlexibleContent() {
        return flexibleContent;
    }

    public void setFlexibleContent(String flexibleContent) {
        this.flexibleContent = flexibleContent;
    }

    public List<GroupMember> getFlexibleList() {
        return flexibleList;
    }

    public void setFlexibleList(List<GroupMember> flexibleList) {
        this.flexibleList = flexibleList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(flexibleId);
        parcel.writeString(flexibleName);
        parcel.writeString(flexiblePortrait);
        parcel.writeString(flexibleStartTime);
        parcel.writeString(flexibleEndTime);
        parcel.writeString(flexiblePlace);
        parcel.writeString(flexibleContent);
        parcel.writeTypedList(flexibleList);
    }
}
