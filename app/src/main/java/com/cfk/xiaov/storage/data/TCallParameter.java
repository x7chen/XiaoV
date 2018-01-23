package com.cfk.xiaov.storage.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by cfk on 2018/1/17.
 */

public class TCallParameter {

    private String HostId;
    private int CallId;
    private int CallType;
    private String CallUserId;
    private String CallNumber;

    public TCallParameter(String hostId, int callId, int callType, String callUserId, String callNumber) {
        HostId = hostId;
        CallId = callId;
        CallType = callType;
        CallUserId = callUserId;
        CallNumber = callNumber;
    }

    public String getHostId() {
        return HostId;
    }

    public void setHostId(String hostId) {
        HostId = hostId;
    }

    public int getCallId() {
        return CallId;
    }

    public void setCallId(int callId) {
        CallId = callId;
    }

    public int getCallType() {
        return CallType;
    }

    public void setCallType(int callType) {
        CallType = callType;
    }

    public String getCallUserId() {
        return CallUserId;
    }

    public void setCallUserId(String callUserId) {
        CallUserId = callUserId;
    }

    public String getCallNumber() {
        return CallNumber;
    }

    public void setCallNumber(String callNumber) {
        CallNumber = callNumber;
    }

    public String toJson() {
        Type mType = new TypeToken<TCallParameter>() {
        }.getType();
        Gson gson = new Gson();
        return gson.toJson(this, mType);
    }

    public static TCallParameter parserFromJson(String json) {
        Type mType = new TypeToken<TCallParameter>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, mType);
    }

}
