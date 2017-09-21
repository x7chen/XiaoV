package com.cfk.xiaov.model.data;

/**
 * Created by cfk on 2017/9/20.
 */

public class ContactData {
    String Name;
    String Id;
    String AvatarUrl;

    public ContactData(){

    }

    public ContactData(String name,String id,String avatar){
        this.Name = name;
        this.Id = id;
        this.AvatarUrl = avatar;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getAvatarUrl() {
        return AvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        AvatarUrl = avatarUrl;
    }
}
