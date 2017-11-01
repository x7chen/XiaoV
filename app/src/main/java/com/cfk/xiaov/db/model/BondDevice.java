package com.cfk.xiaov.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by cfk on 2017/10/31.
 */
@Entity(indexes = {
        @Index(value = "account DESC", unique = true)
})
public class BondDevice {

    /**
     * Entity mapped to table "BondDevice".
     */
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String account;
    private String nickname;
    private String avatarUri;


    public BondDevice(Long id) {
        this.id = id;
    }

    public BondDevice(@NotNull String account, String nickname, String avatarUri) {
        this.account = account;
        this.nickname = nickname;
        this.avatarUri = avatarUri;
    }

    @Generated(hash = 1767825062)
    public BondDevice() {
    }

    @Generated(hash = 850596940)
    public BondDevice(Long id, @NotNull String account, String nickname,
            String avatarUri) {
        this.id = id;
        this.account = account;
        this.nickname = nickname;
        this.avatarUri = avatarUri;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

}