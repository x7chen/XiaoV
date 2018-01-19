package com.cfk.xiaov.model.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Sean on 17/11/18.
 * Company cfk
 */
public class PushRequest {

    private String from;
    private String to;
    private String method;
    private Extra extra;
    public PushRequest(String from, String to, String method, Extra extra) {
        this.from = from;
        this.to = to;
        this.method = method;
        this.extra = extra;
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

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public static class Extra{
        private String url;
        private String channel;
        private String message;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public String toJson(){
        Type mType = new TypeToken<PushRequest>() {
        }.getType();
        Gson gson = new Gson();
        return gson.toJson(this,mType);
    }
    public static PushRequest parserFromJson(String json){
        Type mType = new TypeToken<PushRequest>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(json, mType);
    }
}

