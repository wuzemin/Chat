package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Min on 2016/11/25.
 */

public class GroupMember implements Parcelable{
    /** Not-null value. */
    private String groupId;
    /** Not-null value. */
    private String userId;
    private String name;
    private String portraitUri;
    private String displayName;
    private String nameSpelling;
    private String displayNameSpelling;
    private String groupName;
    private String groupNameSpelling;
    private String groupPortraitUri;
    private String phone;
    private String email;

    public GroupMember() {
    }

    public GroupMember(String userId, String name, String portraitUri) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
    }



    public GroupMember(String groupId, String userId, String name, String portraitUri, String displayName, String nameSpelling, String displayNameSpelling, String groupName, String groupNameSpelling, String groupPortraitUri) {
        this.groupId = groupId;
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
        this.nameSpelling = nameSpelling;
        this.displayNameSpelling = displayNameSpelling;
        this.groupName = groupName;
        this.groupNameSpelling = groupNameSpelling;
        this.groupPortraitUri = groupPortraitUri;
    }

    public GroupMember(String groupId, String userId, String name, String portraitUri, String displayName, String nameSpelling, String displayNameSpelling, String groupName, String groupNameSpelling) {
        this.groupId = groupId;
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
        this.nameSpelling = nameSpelling;
        this.displayNameSpelling = displayNameSpelling;
        this.groupName = groupName;
        this.groupNameSpelling = groupNameSpelling;
    }

    public GroupMember(String groupId, String userId, String name, String portraitUri, String displayName) {
        this.groupId = groupId;
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
    }

    protected GroupMember(Parcel in) {
        groupId = in.readString();
        userId = in.readString();
        name = in.readString();
        portraitUri = in.readString();
        displayName = in.readString();
        nameSpelling = in.readString();
        displayNameSpelling = in.readString();
        groupName = in.readString();
        groupNameSpelling = in.readString();
        groupPortraitUri = in.readString();
        phone = in.readString();
        email = in.readString();
    }

    public static final Creator<GroupMember> CREATOR = new Creator<GroupMember>() {
        @Override
        public GroupMember createFromParcel(Parcel in) {
            return new GroupMember(in);
        }

        @Override
        public GroupMember[] newArray(int size) {
            return new GroupMember[size];
        }
    };

    /** Not-null value. */
    public String getGroupId() {
        return groupId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /** Not-null value. */
    public String getUserId() {
        return userId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }

    public String getDisplayNameSpelling() {
        return displayNameSpelling;
    }

    public void setDisplayNameSpelling(String displayNameSpelling) {
        this.displayNameSpelling = displayNameSpelling;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNameSpelling() {
        return groupNameSpelling;
    }

    public void setGroupNameSpelling(String groupNameSpelling) {
        this.groupNameSpelling = groupNameSpelling;
    }

    public String getGroupPortraitUri() {
        return groupPortraitUri;
    }

    public void setGroupPortraitUri(String groupPortraitUri) {
        this.groupPortraitUri = groupPortraitUri;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupId);
        parcel.writeString(userId);
        parcel.writeString(name);
        parcel.writeString(portraitUri);
        parcel.writeString(displayName);
        parcel.writeString(nameSpelling);
        parcel.writeString(displayNameSpelling);
        parcel.writeString(groupName);
        parcel.writeString(groupNameSpelling);
        parcel.writeString(groupPortraitUri);
        parcel.writeString(phone);
        parcel.writeString(email);
    }
}
