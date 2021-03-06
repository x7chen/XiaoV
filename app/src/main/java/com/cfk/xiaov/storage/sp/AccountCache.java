package com.cfk.xiaov.storage.sp;

import com.cfk.xiaov.app.AppConstants;
import com.cfk.xiaov.util.SPUtils;
import com.cfk.xiaov.util.UIUtils;

/**
 * @创建者 Sean
 * @描述 用户缓存
 */
public class AccountCache {

    public static String getAccount() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConstants.Account.ACCOUNT, "");
    }
    public static String getPassword() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConstants.Account.PASSWORD, "");
    }
    public static String getUserSig() {
            return SPUtils.getInstance(UIUtils.getContext()).getString(AppConstants.Account.USER_SIG, "");
    }
//    public static String getName(){
//        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConstants.Account.NAME, "");
//    }
    public static void save(String account,String user_sig,String pass_wd) {
//        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.Account.NAME, name);
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.Account.ACCOUNT, account);
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.Account.USER_SIG, user_sig);
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConstants.Account.PASSWORD, pass_wd);
    }

    public static void clear() {
//        SPUtils.getInstance(UIUtils.getContext()).remove(AppConstants.Account.NAME);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConstants.Account.ACCOUNT);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConstants.Account.USER_SIG);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConstants.Account.PASSWORD);
    }

}
