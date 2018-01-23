package com.cfk.xiaov.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.business.AccountMgr;
import com.cfk.xiaov.app.base.BaseApp;
import com.cfk.xiaov.storage.sp.AccountCache;
import com.cfk.xiaov.storage.db.BondDeviceDao;
import com.cfk.xiaov.storage.db.DaoMaster;
import com.cfk.xiaov.storage.db.DaoSession;
import com.cfk.xiaov.ui.activity.SplashActivity;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.loader.ImageLoader;
import com.lqr.imagepicker.view.CropImageView;
import com.mob.MobSDK;
import com.tencent.ilivesdk.ILiveSDK;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;


public class MyApp extends BaseApp {
    String TAG = getClass().getSimpleName();
    public static Context ApplicationContext;
    public static boolean isLogin = false;
    private static SplashActivity splashActivity;
    public static AccountMgr mAccountMgr;

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext = this;
        initImagePicker();
        initGreenDao();
        initILVLive();
        initMobSMS();
        initJPush();
        mAccountMgr = new AccountMgr();
    }

    void initMobSMS() {
        MobSDK.init(this, "220f487e81dea", "740a5362635c39d421ee8a510b0ceec3");
        // 如果希望在读取通信录的时候提示用户，可以添加下面的代码，并且必须在其他代码调用之前，否则不起作用；如果没这个需求，可以不加这行代码
        SMSSDK.setAskPermisionOnReadContact(false);
    }

    void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        if (!TextUtils.isEmpty(AccountCache.getAccount())) {
            Set<String> tags = new HashSet<>(1);
            tags.add(AccountCache.getAccount());

            JPushInterface.setTags(this, 0, tags);
        }

    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                Glide.with(getContext()).load(Uri.parse("file://" + path).toString()).centerCrop().into(imageView);
            }

            @Override
            public void clearMemoryCache() {

            }
        });   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    private static BondDeviceDao bondDeviceDao;

    void initGreenDao() {

        DaoMaster.DevOpenHelper helper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;

        // do this once, for example in your Application class
        helper = new DaoMaster.DevOpenHelper(this, "bond-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
// do this in your activities/fragments to get hold of a DAO
        bondDeviceDao = daoSession.getBondDeviceDao();

    }

    public static BondDeviceDao getBondDeviceDao() {
        return bondDeviceDao;
    }

    public static void setSplashActivity(SplashActivity splashActivity) {
        MyApp.splashActivity = splashActivity;
    }

    public static SplashActivity getSplashActivity() {
        return splashActivity;
    }

    public void initILVLive() {
        ILiveSDK.getInstance().initSdk(getApplicationContext(), 1400036822, 14464);
        Log.i(TAG, "init iLive!");
    }

}
