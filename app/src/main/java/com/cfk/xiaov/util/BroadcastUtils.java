package com.cfk.xiaov.util;

import android.content.Context;
import android.content.Intent;

import com.cfk.xiaov.app.base.BaseApp;

/**
 * Created by cfk on 2017/9/22.
 */

public class BroadcastUtils {
    public static void sendBroadcast(String action,String key,String value){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(key,value);
        getContext().sendBroadcast(intent);
    }
    public static Context getContext() {
        return BaseApp.getContext();
    }

}
