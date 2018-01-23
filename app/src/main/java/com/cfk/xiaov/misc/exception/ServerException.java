package com.cfk.xiaov.misc.exception;

import com.cfk.xiaov.util.UIUtils;

/**
 * @创建者 Sean
 * @描述 服务器异常
 */
public class ServerException extends Exception {

    public ServerException(int errorCode) {
        this(UIUtils.getString(com.cfk.xiaov.R.string.error_code) + errorCode);
    }

    public ServerException(String message) {
        super(message);
    }

}
