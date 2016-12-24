package com.min.smalltalk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Min on 2016/12/23.
 */

public class GroupVote implements Parcelable{
    private String vote_id;
    private String vote_title;
    private String add_time;
    private String end_time;
    private int status;
    private List<String> option;
    private String groupId;
    private String userId;

    public GroupVote(String vote_id, String vote_title, String add_time, String end_time) {
        this.vote_id = vote_id;
        this.vote_title = vote_title;
        this.add_time = add_time;
        this.end_time = end_time;
    }

    protected GroupVote(Parcel in) {
        vote_id = in.readString();
        vote_title = in.readString();
        add_time = in.readString();
        end_time = in.readString();
        status = in.readInt();
        option = in.createStringArrayList();
        groupId = in.readString();
        userId = in.readString();
    }

    public static final Creator<GroupVote> CREATOR = new Creator<GroupVote>() {
        @Override
        public GroupVote createFromParcel(Parcel in) {
            return new GroupVote(in);
        }

        @Override
        public GroupVote[] newArray(int size) {
            return new GroupVote[size];
        }
    };

    public String getVote_id() {
        return vote_id;
    }

    public void setVote_id(String vote_id) {
        this.vote_id = vote_id;
    }

    public String getVote_title() {
        return vote_title;
    }

    public void setVote_title(String vote_title) {
        this.vote_title = vote_title;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getOption() {
        return option;
    }

    public void setOption(List<String> option) {
        this.option = option;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vote_id);
        dest.writeString(vote_title);
        dest.writeString(add_time);
        dest.writeString(end_time);
        dest.writeInt(status);
        dest.writeStringList(option);
        dest.writeString(groupId);
        dest.writeString(userId);
    }
}
