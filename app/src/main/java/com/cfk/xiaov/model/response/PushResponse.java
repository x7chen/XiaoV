package com.cfk.xiaov.model.response;


/**
 * Created by AMing on 15/12/22.
 * Company RongCloud
 */
public class PushResponse {

    private int code;

    private ResultEntity result;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity {
        private String method;
        private String from;
        private String to;
        private String channel;

        public ResultEntity(String method, String from, String to, String channel) {
            this.method = method;
            this.from = from;
            this.to = to;
            this.channel = channel;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
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

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }
}
