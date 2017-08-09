package com.cfk.xiaov.api;

import com.cfk.xiaov.api.base.BaseApiRetrofit;
import com.cfk.xiaov.model.request.AddGroupMemberRequest;
import com.cfk.xiaov.model.request.CreateGroupRequest;
import com.cfk.xiaov.model.request.DeleteFriendRequest;
import com.cfk.xiaov.model.request.DeleteGroupMemberRequest;
import com.cfk.xiaov.model.request.FriendInvitationRequest;
import com.cfk.xiaov.model.request.RegisterRequest;
import com.cfk.xiaov.model.request.RemoveFromBlacklistRequest;
import com.cfk.xiaov.model.request.RestPasswordRequest;
import com.cfk.xiaov.model.request.SendCodeRequest;
import com.cfk.xiaov.model.request.SetGroupDisplayNameRequest;
import com.cfk.xiaov.model.request.SetGroupPortraitRequest;
import com.cfk.xiaov.model.request.SetNameRequest;
import com.cfk.xiaov.model.response.AgreeFriendsResponse;
import com.cfk.xiaov.model.response.ChangePasswordResponse;
import com.cfk.xiaov.model.response.DefaultConversationResponse;
import com.cfk.xiaov.model.response.GetGroupInfoResponse;
import com.cfk.xiaov.model.response.GetGroupResponse;
import com.cfk.xiaov.model.response.GetTokenResponse;
import com.cfk.xiaov.model.response.GetUserInfoByPhoneResponse;
import com.cfk.xiaov.model.response.QuitGroupResponse;
import com.cfk.xiaov.model.response.RestPasswordResponse;
import com.cfk.xiaov.model.response.SendCodeResponse;
import com.cfk.xiaov.model.response.SetPortraitResponse;
import com.cfk.xiaov.model.response.VerifyCodeResponse;
import com.cfk.xiaov.model.response.VersionResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cfk.xiaov.model.request.AddToBlackListRequest;
import com.cfk.xiaov.model.request.AgreeFriendsRequest;
import com.cfk.xiaov.model.request.ChangePasswordRequest;
import com.cfk.xiaov.model.request.CheckPhoneRequest;
import com.cfk.xiaov.model.request.DismissGroupRequest;
import com.cfk.xiaov.model.request.JoinGroupRequest;
import com.cfk.xiaov.model.request.LoginRequest;
import com.cfk.xiaov.model.request.QuitGroupRequest;
import com.cfk.xiaov.model.request.SetFriendDisplayNameRequest;
import com.cfk.xiaov.model.request.SetGroupNameRequest;
import com.cfk.xiaov.model.request.SetPortraitRequest;
import com.cfk.xiaov.model.request.VerifyCodeRequest;
import com.cfk.xiaov.model.response.AddGroupMemberResponse;
import com.cfk.xiaov.model.response.AddToBlackListResponse;
import com.cfk.xiaov.model.response.CheckPhoneResponse;
import com.cfk.xiaov.model.response.CreateGroupResponse;
import com.cfk.xiaov.model.response.DeleteFriendResponse;
import com.cfk.xiaov.model.response.DeleteGroupMemberResponse;
import com.cfk.xiaov.model.response.FriendInvitationResponse;
import com.cfk.xiaov.model.response.GetBlackListResponse;
import com.cfk.xiaov.model.response.GetFriendInfoByIDResponse;
import com.cfk.xiaov.model.response.GetGroupMemberResponse;
import com.cfk.xiaov.model.response.GetUserInfoByIdResponse;
import com.cfk.xiaov.model.response.GetUserInfosResponse;
import com.cfk.xiaov.model.response.JoinGroupResponse;
import com.cfk.xiaov.model.response.LoginResponse;
import com.cfk.xiaov.model.response.QiNiuTokenResponse;
import com.cfk.xiaov.model.response.RegisterResponse;
import com.cfk.xiaov.model.response.RemoveFromBlackListResponse;
import com.cfk.xiaov.model.response.SetFriendDisplayNameResponse;
import com.cfk.xiaov.model.response.SetGroupDisplayNameResponse;
import com.cfk.xiaov.model.response.SetGroupNameResponse;
import com.cfk.xiaov.model.response.SetGroupPortraitResponse;
import com.cfk.xiaov.model.response.SetNameResponse;
import com.cfk.xiaov.model.response.SyncTotalDataResponse;
import com.cfk.xiaov.model.response.UserRelationshipResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
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
        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),route);
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), route);
        return body;
    }

    //登录
    public Observable<LoginResponse> login(String region, String phone, String password) {
        return mApi.login(getRequestBody(new LoginRequest(region, phone, password)));
    }

    //注册
    public Observable<CheckPhoneResponse> checkPhoneAvailable(String region, String phone) {
        return mApi.checkPhoneAvailable(getRequestBody(new CheckPhoneRequest(phone, region)));
    }

    public Observable<SendCodeResponse> sendCode(String region, String phone) {
        return mApi.sendCode(getRequestBody(new SendCodeRequest(region, phone)));
    }

    public Observable<VerifyCodeResponse> verifyCode(String region, String phone, String code) {
        return mApi.verifyCode(getRequestBody(new VerifyCodeRequest(region, phone, code)));
    }

    public Observable<RegisterResponse> register(String nickname, String password, String verification_token) {
        return mApi.register(getRequestBody(new RegisterRequest(nickname, password, verification_token)));
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

    public Observable<ChangePasswordResponse> changePassword(String oldPassword, String newPassword) {
        return mApi.changePassword(getRequestBody(new ChangePasswordRequest(oldPassword, newPassword)));
    }

    /**
     * @param password           密码，6 到 20 个字节，不能包含空格
     * @param verification_token 调用 /user/verify_code 成功后返回的 activation_token
     */
    public Observable<RestPasswordResponse> restPassword(String password, String verification_token) {
        return mApi.restPassword(getRequestBody(new RestPasswordRequest(password, verification_token)));
    }

    //查询
    public Observable<GetUserInfoByIdResponse> getUserInfoById(String userid) {
        return mApi.getUserInfoById(userid);
    }

    public Observable<GetUserInfoByPhoneResponse> getUserInfoFromPhone(String region, String phone) {
        return mApi.getUserInfoFromPhone(region, phone);
    }

    //好友
    public Observable<FriendInvitationResponse> sendFriendInvitation(String userid, String addFriendMessage) {
        return mApi.sendFriendInvitation(getRequestBody(new FriendInvitationRequest(userid, addFriendMessage)));
    }

    public Observable<UserRelationshipResponse> getAllUserRelationship() {
        return mApi.getAllUserRelationship();
    }

    public Observable<GetFriendInfoByIDResponse> getFriendInfoByID(String userid) {
        return mApi.getFriendInfoByID(userid);
    }

    public Observable<AgreeFriendsResponse> agreeFriends(String friendId) {
        return mApi.agreeFriends(getRequestBody(new AgreeFriendsRequest(friendId)));
    }

    public Observable<DeleteFriendResponse> deleteFriend(String friendId) {
        return mApi.deleteFriend(getRequestBody(new DeleteFriendRequest(friendId)));
    }

    public Observable<SetFriendDisplayNameResponse> setFriendDisplayName(String friendId, String displayName) {
        return mApi.setFriendDisplayName(getRequestBody(new SetFriendDisplayNameRequest(friendId, displayName)));
    }

    public Observable<GetBlackListResponse> getBlackList() {
        return mApi.getBlackList();
    }

    public Observable<AddToBlackListResponse> addToBlackList(String friendId) {
        return mApi.addToBlackList(getRequestBody(new AddToBlackListRequest(friendId)));
    }

    public Observable<RemoveFromBlackListResponse> removeFromBlackList(String friendId) {
        return mApi.removeFromBlackList(getRequestBody(new RemoveFromBlacklistRequest(friendId)));
    }


    //群组
    public Observable<CreateGroupResponse> createGroup(String name, List<String> memberIds) {
        return mApi.createGroup(getRequestBody(new CreateGroupRequest(name, memberIds)));
    }

    public Observable<SetGroupPortraitResponse> setGroupPortrait(String groupId, String portraitUri) {
        return mApi.setGroupPortrait(getRequestBody(new SetGroupPortraitRequest(groupId, portraitUri)));
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

    public Observable<AddGroupMemberResponse> addGroupMember(String groupId, List<String> memberIds) {
        return mApi.addGroupMember(getRequestBody(new AddGroupMemberRequest(groupId, memberIds)));
    }

    public Observable<DeleteGroupMemberResponse> deleGroupMember(String groupId, List<String> memberIds) {
        return mApi.deleGroupMember(getRequestBody(new DeleteGroupMemberRequest(groupId, memberIds)));
    }

    public Observable<SetGroupNameResponse> setGroupName(String groupId, String name) {
        return mApi.setGroupName(getRequestBody(new SetGroupNameRequest(groupId, name)));
    }

    public Observable<QuitGroupResponse> quitGroup(String groupId) {
        return mApi.quitGroup(getRequestBody(new QuitGroupRequest(groupId)));
    }

    public Observable<QuitGroupResponse> dissmissGroup(String groupId) {
        return mApi.dissmissGroup(getRequestBody(new DismissGroupRequest(groupId)));
    }
//    public Observable<DismissGroupResponse> dissmissGroup(String groupId) {
//        return mApi.dissmissGroup(getRequestBody(new DismissGroupRequest(groupId)));
//    }

    public Observable<SetGroupDisplayNameResponse> setGroupDisplayName(String groupId, String displayName) {
        return mApi.setGroupDisplayName(getRequestBody(new SetGroupDisplayNameRequest(groupId, displayName)));
    }

    public Observable<JoinGroupResponse> JoinGroup(String groupId) {
        return mApi.JoinGroup(getRequestBody(new JoinGroupRequest(groupId)));
    }

    public Observable<DefaultConversationResponse> getDefaultConversation() {
        return mApi.getDefaultConversation();
    }

    public Observable<GetUserInfosResponse> getUserInfos(List<String> ids) {
        StringBuilder sb = new StringBuilder();
        for (String s : ids) {
            sb.append("id=");
            sb.append(s);
            sb.append("&");
        }
        String stringRequest = sb.substring(0, sb.length() - 1);
        return mApi.getUserInfos(stringRequest);
    }

    //其他
    public Observable<QiNiuTokenResponse> getQiNiuToken() {
        return mApi.getQiNiuToken();
    }

    public Observable<VersionResponse> getClientVersion() {
        return mApi.getClientVersion();
    }

    public Observable<SyncTotalDataResponse> syncTotalData(String version) {
        return mApi.syncTotalData(version);
    }

}
