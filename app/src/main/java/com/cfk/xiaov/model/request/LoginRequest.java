package com.cfk.xiaov.model.request;


/**
 * Created by AMing on 15/12/24.
 * Company RongCloud
 */
public class LoginRequest {


    private String region;
    private String account;
    private String password;

    public LoginRequest(String region, String account, String password) {
        this.region = region;
        this.account = account;
        this.password = password;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
