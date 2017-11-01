package com.cfk.xiaov.db.model;

/**
 * Created by cfk on 2017/9/29.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {
    private String id;
    private String name;
    private String portraitUri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public static Creator<UserInfo> getCREATOR() {
        return CREATOR;
    }

    public String getUserId() {
        return id;
    }

    public void setUserId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.portraitUri);
    }

    public UserInfo() {
    }

    public UserInfo(String id, String name, String portraitUri) {
        this.id = id;
        this.name = name;
        this.portraitUri = portraitUri;
    }

    protected UserInfo(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.portraitUri = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}