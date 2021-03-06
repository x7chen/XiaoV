package com.cfk.xiaov.rest.model.request;


/**
 * Created by AMing on 15/12/24.
 * Company RongCloud
 */
public class LoginRequest {


    private String region;
    private String account;
    private String password;
    private String phone;

    public LoginRequest(String region, String account, String password,String phone) {
        this.region = region;
        this.account = account;
        this.password = password;
        this.phone =phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
