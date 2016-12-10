package com.min.smalltalk.bean;

/**
 * Created by Min on 2016/11/30.
 */

public class Groups{
    private String groupId;
    private String groupName;
    private String groupPortraitUri;
    private String displayName;
    private String role;  //角色---群主或成员
    private String bulletin;  //公告
    private String timestamp;
    private String nameSpelling;

    public Groups() {
    }

    public Groups(String groupId, String groupName, String groupPortraitUri) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPortraitUri = groupPortraitUri;
    }
    public Groups(String groupId, String groupName, String groupPortraitUri,String role) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPortraitUri = groupPortraitUri;
        this.role=role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPortraitUri() {
        return groupPortraitUri;
    }

    public void setGroupPortraitUri(String groupPortraitUri) {
        this.groupPortraitUri = groupPortraitUri;
    }
}
