package com.cfk.xiaov.db;

import com.cfk.xiaov.db.model.Groups;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @创建者 CSDN_LQR
 * @描述 数据库管理器
 */
public class DBManager {
    private static DBManager mInstance;

    private List<Groups> mGroupsList;

    public DBManager() {
    }

    public static DBManager getInstance() {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager();
                }
            }
        }
        return mInstance;
    }
}
