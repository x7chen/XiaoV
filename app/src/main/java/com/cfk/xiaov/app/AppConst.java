package com.cfk.xiaov.app;


import com.cfk.xiaov.util.FileUtils;
import com.cfk.xiaov.util.LogUtils;

/**
 * @创建者 Sean
 * @描述 全局常量类
 */
public class AppConst {

    public static final String TAG = "xiaov";
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;//日志输出级别

    public static final String REGION = "86";



    public static final class Action {
        public static final String FETCH_COMPLETE = "fetch_complete";
        public static final String CHANGE_INFO_FOR_ME = "change_info_for_me";
        public static final String CHANGE_INFO_FOR_CHANGE_NAME = "change_info_for_change_name";
        public static final String UPDATE_CONVERSATIONS = "update_conversations";
        public static final String NEW_COMING_CALL = "new_coming_call";
        public static final String HANG_UP_CALL = "hang_up_call";
        public static final String AGREE_MY_INVITE = "agree_my_invite";
        public static final String NET_STATUS = "net_status";
        public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    }

    //push方法
    public static class PUSH_METHOD {

        public static final String CALL = "call";
        public static final String HANG_UP = "hang_up";
        public static final String INVITE = "invite";
        public static final String DENY = "deny";
        public static final String IGNORE = "ignore";
        public static final String AGREE = "agree";
    }


    public static final class User {
        public static final String ID = "id";
        public static final String PHONE = "phone";
        //public static final String ACCOUNT = "account";
        public static final String TOKEN = "token";
    }

    public static final class Bond {
        public static final String BOND_ID = "bond_id";
        public static final String CONTACTS = "[]";
    }

    public static final class Account {
        public static final String NAME = "name";
        public static final String ACCOUNT = "account";
        public static final String PASSWORD = "password";
        public static final String USER_SIG = "user_sig";
    }

    public static final class MyInfo {
        public static final String NICK_NAME = "name";
        public static final String ACCOUNT = "account";
        public static final String AVATAR_URI = "avatarUri";
    }

    public static final class MyUrl {
        public static final String HELP_FEED_BACK = "http://www.runoob.com/";
    }

    public static final class QrCodeCommon {
        public static final String ADD = "add:";//加好友
        public static final String JOIN = "join:";//入群
        public static final String BOND = "bond:";//绑定
    }

    //视频存放位置
    public static final String VIDEO_SAVE_DIR = FileUtils.getDir("video");
    //照片存放位置
    public static final String PHOTO_SAVE_DIR = FileUtils.getDir("photo");
    //头像保存位置
    public static final String HEADER_SAVE_DIR = FileUtils.getDir("header");
}
