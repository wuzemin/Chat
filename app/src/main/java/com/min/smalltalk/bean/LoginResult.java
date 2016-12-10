package com.min.smalltalk.bean;

import java.io.Serializable;

/**
 * Created by Min on 2016/11/23.
 */

public class LoginResult implements Serializable {
    /**
     * code:200
     * result:{"id":"kkkk","token":"fas4df6s4df54sad5f4sd1cs2df5as4df5asdf",nickname:"",portrait:""}
     */
    //
    private int code;
    /**
     * id : kkkk
     * token : fas4df6s4df54sad5f4sd1cs2df5as4df5asdf
     * nickname : 艾佳
     * portrait : http:....
     */
    private ResultEntity msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ResultEntity getMsg() {
        return msg;
    }

    public void setMsg(ResultEntity msg) {
        this.msg = msg;
    }

    public static class ResultEntity{
        private String id;
        private String token;
        private String nickname;
        private String portrait;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
    }

}