package com.cfk.xiaov.model.response;

/**
 * Created by cfk on 2017/9/29.
 */

public class QiNiuDownloadResponse {

    private int code;
    private ResultEntity result;
    public void setCode(int code) {
        this.code = code;
    }
    public void setResult(ResultEntity result) {
        this.result = result;
    }
    public int getCode() {
        return code;
    }
    public ResultEntity getResult() {
        return result;
    }
    public static class ResultEntity {
        private String key;
        private String privateDownloadUrl;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getPrivateDownloadUrl() {
            return privateDownloadUrl;
        }

        public void setPrivateDownloadUrl(String privateDownloadUrl) {
            this.privateDownloadUrl = privateDownloadUrl;
        }
    }
}
