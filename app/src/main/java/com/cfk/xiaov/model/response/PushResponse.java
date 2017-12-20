package com.cfk.xiaov.model.response;


import com.cfk.xiaov.model.request.PushRequest;

/**
 * Created by AMing on 15/12/22.
 * Company RongCloud
 */
public class PushResponse {

    private int code;

    private PushRequest result;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setResult(PushRequest result) {
        this.result = result;
    }

    public PushRequest getResult() {
        return result;
    }

}
