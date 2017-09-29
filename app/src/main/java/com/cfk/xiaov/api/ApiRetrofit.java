package com.cfk.xiaov.api;

import com.cfk.xiaov.api.base.BaseApiRetrofit;
import com.cfk.xiaov.model.request.CheckPhoneRequest;
import com.cfk.xiaov.model.request.LoginRequest;
import com.cfk.xiaov.model.request.RegisterRequest;
import com.cfk.xiaov.model.request.SetNameRequest;
import com.cfk.xiaov.model.request.SetPortraitRequest;
import com.cfk.xiaov.model.response.CheckPhoneResponse;
import com.cfk.xiaov.model.response.GetGroupInfoResponse;
import com.cfk.xiaov.model.response.GetGroupMemberResponse;
import com.cfk.xiaov.model.response.GetGroupResponse;
import com.cfk.xiaov.model.response.GetTokenResponse;
import com.cfk.xiaov.model.response.GetUserInfoByIdResponse;
import com.cfk.xiaov.model.response.LoginResponse;
import com.cfk.xiaov.model.response.QiNiuDownloadResponse;
import com.cfk.xiaov.model.response.QiNiuTokenResponse;
import com.cfk.xiaov.model.response.RegisterResponse;
import com.cfk.xiaov.model.response.SetNameResponse;
import com.cfk.xiaov.model.response.SetPortraitResponse;
import com.cfk.xiaov.model.response.UserRelationshipResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @创建者 CSDN_LQR
 * @描述 使用Retrofit对网络请求进行配置
 */
public class ApiRetrofit extends BaseApiRetrofit {

    public MyApi mApi;
    public static ApiRetrofit mInstance;

    private ApiRetrofit() {
        super();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //在构造方法中完成对Retrofit接口的初始化
        mApi = new Retrofit.Builder()
                .baseUrl(MyApi.BASE_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(MyApi.class);
    }

    public static ApiRetrofit getInstance() {
        if (mInstance == null) {
            synchronized (ApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new ApiRetrofit();
                }
            }
        }
        return mInstance;
    }

    private RequestBody getRequestBody(Object obj) {
        String route = new Gson().toJson(obj);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), route);
        return body;
    }

    //登录
    public Observable<LoginResponse> login(String region, String userid, String password) {
        return mApi.login(getRequestBody(new LoginRequest(region, userid, password)));
    }

    //注册
    public Observable<CheckPhoneResponse> checkAvailable(String region, String phone) {
        return mApi.checkAvailable(getRequestBody(new CheckPhoneRequest(phone, region)));
    }

    public Observable<RegisterResponse> register(String region,String nickname, String userid, String password) {
        return mApi.register(getRequestBody(new RegisterRequest(region,nickname, userid, password)));
    }

    public Observable<GetTokenResponse> getToken() {
        return mApi.getToken();
    }

    //个人信息
    public Observable<SetNameResponse> setName(String nickName) {
        return mApi.setName(getRequestBody(new SetNameRequest(nickName)));
    }

    public Observable<SetPortraitResponse> setPortrait(String portraitUri) {
        return mApi.setPortrait(getRequestBody(new SetPortraitRequest(portraitUri)));
    }

    //查询
    public Observable<GetUserInfoByIdResponse> getUserInfoById(String userid) {
        return mApi.getUserInfoById(userid);
    }


    public Observable<UserRelationshipResponse> getAllUserRelationship() {
        return mApi.getAllUserRelationship();
    }

    public Observable<GetGroupResponse> getGroups() {
        return mApi.getGroups();
    }

    public Observable<GetGroupInfoResponse> getGroupInfo(String groupId) {
        return mApi.getGroupInfo(groupId);
    }

    public Observable<GetGroupMemberResponse> getGroupMember(String groupId) {
        return mApi.getGroupMember(groupId);
    }

    //其他
    public Observable<QiNiuTokenResponse> getQiNiuToken() {
        return mApi.getQiNiuToken();
    }
    public Observable<QiNiuDownloadResponse> getQiNiuDownloadUrl(String key) {
        return mApi.getQiNiuDownloadUrl(key);
    }


}
