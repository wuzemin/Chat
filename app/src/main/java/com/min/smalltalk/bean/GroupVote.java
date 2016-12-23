package com.min.smalltalk.bean;

import java.util.List;

/**
 * Created by Min on 2016/12/23.
 */

public class GroupVote {
    private String voteId;
    private String voidTitle;
    private String createTime;
    private int status;
    private List<String> contentList;
    private Users createUser;
    private String groupId;

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getVoidTitle() {
        return voidTitle;
    }

    public void setVoidTitle(String voidTitle) {
        this.voidTitle = voidTitle;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }

    public Users getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Users createUser) {
        this.createUser = createUser;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public static class Users{
        private String createId;
        private String createName;
        private String createaPortraitUri;
    }
}
