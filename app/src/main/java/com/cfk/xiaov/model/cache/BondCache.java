package com.cfk.xiaov.model.cache;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.util.SPUtils;
import com.cfk.xiaov.util.UIUtils;

/**
 * @创建者 CSDN_LQR
 * @描述 用户缓存
 */
public class BondCache {

    public static String getBondId() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConst.Bond.BOND_ID, "");
    }

    public static void save(String id) {
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.Bond.BOND_ID, id);

    }

    public static void clear() {
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConst.Bond.BOND_ID);
        //DBManager.getInstance().deleteAllUserInfo();
    }

}
