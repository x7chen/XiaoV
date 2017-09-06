package com.cfk.xiaov.model.request;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class RegisterRequest {


    private String nickname;

    private String account;

    private String password;

    public RegisterRequest(String nickname, String account , String password) {
        this.nickname = nickname;
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
