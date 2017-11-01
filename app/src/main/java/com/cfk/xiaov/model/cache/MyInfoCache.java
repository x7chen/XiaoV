package com.cfk.xiaov.model.cache;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.util.SPUtils;
import com.cfk.xiaov.util.UIUtils;

/**
 * Created by cfk on 2017/10/31.
 */

public class MyInfoCache {
    public static String getAccount() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConst.MyInfo.ACCOUNT, "");
    }

    public static String getNickName() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConst.MyInfo.NICK_NAME, "");
    }

    public static String getAvatarUri() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConst.MyInfo.AVATAR_URI, "");
    }
    public static void setAccount(String account) {
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.MyInfo.ACCOUNT, account);
    }

    public static void setNickName(String nick_name) {
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.MyInfo.NICK_NAME, nick_name);

    }

    public static void setAvatarUri(String avatar_uri) {
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.MyInfo.AVATAR_URI, avatar_uri);
    }
    public static void save(String account, String nick_name, String avatar_uri) {

        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.MyInfo.ACCOUNT, account);
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.MyInfo.NICK_NAME, nick_name);
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.MyInfo.AVATAR_URI, avatar_uri);
    }

    public static void clear() {
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConst.MyInfo.ACCOUNT);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConst.MyInfo.NICK_NAME);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConst.MyInfo.AVATAR_URI);
    }
}
