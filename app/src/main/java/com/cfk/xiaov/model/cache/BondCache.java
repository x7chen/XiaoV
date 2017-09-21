package com.cfk.xiaov.model.cache;

import android.util.Log;

import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.model.data.ContactData;
import com.cfk.xiaov.util.SPUtils;
import com.cfk.xiaov.util.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @创建者 CSDN_LQR
 * @描述 用户缓存
 */
public class BondCache {

    static String TAG = "BondCache";
    public static String getBondId() {
        return SPUtils.getInstance(UIUtils.getContext()).getString(AppConst.Bond.BOND_ID, "");
    }

    public static void save(String id) {


    }

    public static void clear() {
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConst.Bond.BOND_ID);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConst.Bond.CONTACTS);
        //DBManager.getInstance().deleteAllUserInfo();
    }

    public static ArrayList<ContactData> getContactList(){
        String src= SPUtils.getInstance(UIUtils.getContext()).getString(AppConst.Bond.CONTACTS, "");
        Type listType = new TypeToken<ArrayList<ContactData>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(src, listType);
    }
    public static void saveContacts(List<ContactData> contactDatas){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<ContactData>>(){}.getType();
        String contacts = gson.toJson(contactDatas,listType);
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.Bond.CONTACTS, contacts);
        Log.i(TAG,contacts);
    }

}
