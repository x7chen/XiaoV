package com.cfk.xiaov.api;


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
 * @创建者 CSDN_LQR
 * @描述 server端api
 */

public interface MyApi {

    public static final String BASE_URL = "http://www.abc-workflow.com/";

    //检查ID是否被注册
    @POST("users/check_available")
    Observable<CheckPhoneResponse> checkAvailable(@Body RequestBody body);

    //注册
    @POST("users/register")
    Observable<RegisterResponse> register(@Body RequestBody body);

    //登录
    @POST("users/login")
    Observable<LoginResponse> login(@Body RequestBody body);

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
    Observable<GetUserInfoByIdResponse> getUserInfoById(@Path("userid") String userid);


    //获取发生过用户关系的列表
    @GET("friendship/all")
    Observable<UserRelationshipResponse> getAllUserRelationship();


    //获取当前用户所属群组列表
    @GET("user/groups")
    Observable<GetGroupResponse> getGroups();

    //根据 群组id 查询该群组信息   403 群组成员才能看
    @GET("group/{groupId}")
    Observable<GetGroupInfoResponse> getGroupInfo(@Path("groupId") String groupId);

    //根据群id获取群组成员
    @GET("group/{groupId}/members")
    Observable<GetGroupMemberResponse> getGroupMember(@Path("groupId") String groupId);

//http://www.abc-workflow.com/qiniu/requst_upload_token
    //得到七牛的token
    @GET("qiniu/request_upload_token")
    Observable<QiNiuTokenResponse> getQiNiuToken();

    @GET("qiniu/request_download_url")
    Observable<QiNiuDownloadResponse> getQiNiuDownloadUrl(@Query("key") String key);

    //下载图片
    @GET
    Observable<ResponseBody> downloadPic(@Url String url);

}
