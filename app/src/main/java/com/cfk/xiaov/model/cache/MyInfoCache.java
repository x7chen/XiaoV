package com.cfk.xiaov.model.cache;

import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.util.SPUtils;
import com.cfk.xiaov.util.UIUtils;

/**
 * Created by cfk on 2017/10/31.
 */

public class MyInfoCache {
    public static String getAccount() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConstants.MyInfo.ACCOUNT, "");
    }

    public static String getNickName() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConstants.MyInfo.NICK_NAME, "");
    }

    public static String getAvatarUri() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConstants.MyInfo.AVATAR_URI, "");
    }
    public static void setAccount(String account) {
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.MyInfo.ACCOUNT, account);
    }

    public static void setNickName(String nick_name) {
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.MyInfo.NICK_NAME, nick_name);

    }

    public static void setAvatarUri(String avatar_uri) {
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.MyInfo.AVATAR_URI, avatar_uri);
    }
    public static void save(String account, String nick_name, String avatar_uri) {

        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.MyInfo.ACCOUNT, account);
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.MyInfo.NICK_NAME, nick_name);
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.MyInfo.AVATAR_URI, avatar_uri);
    }

    public static void clear() {
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConstants.MyInfo.ACCOUNT);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConstants.MyInfo.NICK_NAME);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConstants.MyInfo.AVATAR_URI);
    }
}
