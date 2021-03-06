package com.cfk.xiaov.rest.api;


import com.cfk.xiaov.rest.model.response.CheckPhoneResponse;
import com.cfk.xiaov.rest.model.response.GetTokenResponse;
import com.cfk.xiaov.rest.model.response.GetUserInfoResponse;
import com.cfk.xiaov.rest.model.response.LoginResponse;
import com.cfk.xiaov.rest.model.response.PushResponse;
import com.cfk.xiaov.rest.model.response.QiNiuDownloadResponse;
import com.cfk.xiaov.rest.model.response.QiNiuTokenResponse;
import com.cfk.xiaov.rest.model.response.RegisterResponse;
import com.cfk.xiaov.rest.model.response.SetNameResponse;
import com.cfk.xiaov.rest.model.response.SetPortraitResponse;
import com.cfk.xiaov.rest.model.response.UserRelationshipResponse;
import com.cfk.xiaov.rest.model.response.VerifyCodeResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @创建者 Sean
 * @描述 server端api
 */

public interface MyApi {

    String BASE_URL = "http://www.abc-workflow.com/";

    //检查ID是否被注册
    @POST("users/check_available")
    Observable<CheckPhoneResponse> checkAvailable(@Body RequestBody body);

    //注册
    @POST("users/register")
    Observable<RegisterResponse> register(@Body RequestBody body);

    //登录
    @POST("users/login")
    Observable<LoginResponse> login(@Body RequestBody body);

    //注册
    @POST("users/register_by_phone")
    Observable<RegisterResponse> register_by_phone(@Body RequestBody body);

    //登录
    @POST("users/login_by_phone")
    Observable<LoginResponse> login_by_phone(@Body RequestBody body);

    //获取 token 前置条件需要登录   502 坏的网关 测试环境用户已达上限
    @GET("users/get_token")
    Observable<GetTokenResponse> getToken();

    //设置自己的昵称
    @POST("users/set_nickname")
    Observable<SetNameResponse> setName(@Body RequestBody body);

    //设置用户头像
    @POST("users/set_portrait_uri")
    Observable<SetPortraitResponse> setPortrait(@Body RequestBody body);


    //根据 id 去服务端查询用户信息
    @GET("users/{userid}")
    Observable<GetUserInfoResponse> getUserInfoById(@Path("userid") String userid);

    @GET("users/by_phone")
    Observable<GetUserInfoResponse> getUserInfoByPhone(@Query("phone") String phone);

    //获取发生过用户关系的列表
    @GET("friendship/all")
    Observable<UserRelationshipResponse> getAllUserRelationship();

    //得到七牛的token
    @GET("qiniu/request_upload_token")
    Observable<QiNiuTokenResponse> getQiNiuToken();

    @GET("qiniu/request_download_url")
    Observable<QiNiuDownloadResponse> getQiNiuDownloadUrl(@Query("key") String key);

    @GET("users/send_verify_code")
    Observable<VerifyCodeResponse> sendVerifyCode(@Query("phone") String phone,
                                                  @Query("verify_code") String verify_code);

    @POST("push")
    Observable<PushResponse> push(@Body RequestBody body);
    //下载图片
    @GET
    Observable<ResponseBody> downloadPic(@Url String url);

}
