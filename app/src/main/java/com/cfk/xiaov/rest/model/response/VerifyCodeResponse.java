package com.cfk.xiaov.rest.model.response;


/**
 * Created by AMing on 15/12/22.
 * Company RongCloud
 */
public class VerifyCodeResponse {

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
        private int status;
        private String error;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
