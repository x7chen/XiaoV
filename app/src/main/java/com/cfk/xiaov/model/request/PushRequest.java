package com.cfk.xiaov.model.request;

/**
 * Created by AMing on 16/1/18.
 * Company RongCloud
 */
public class PushRequest {

    private String from;
    private String to;
    private String method;
    private String channel;

    public PushRequest(String from, String to, String method, String channel) {
        this.from = from;
        this.to = to;
        this.method = method;
        this.channel = channel;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
