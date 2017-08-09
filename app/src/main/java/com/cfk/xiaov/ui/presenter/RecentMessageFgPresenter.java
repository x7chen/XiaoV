package com.cfk.xiaov.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.model.message.RedPacketMessage;
import com.cfk.xiaov.ui.activity.SessionActivity;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.emoji.MoonUtils;
import com.lqr.ninegridimageview.LQRNineGridImageView;
import com.lqr.ninegridimageview.LQRNineGridImageViewAdapter;
import com.cfk.xiaov.db.model.GroupMember;
import com.cfk.xiaov.db.model.Groups;
import com.cfk.xiaov.manager.JsonMananger;
import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.model.data.GroupNotificationMessageData;
import com.cfk.xiaov.ui.activity.MainActivity;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IRecentMessageFgView;
import com.cfk.xiaov.util.LogUtils;
import com.cfk.xiaov.util.MediaFileUtils;
import com.cfk.xiaov.util.TimeUtils;
import com.cfk.xiaov.util.UIUtils;
import com.cfk.xiaov.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.GroupNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import retrofit2.adapter.rxjava.HttpException;

public class RecentMessageFgPresenter extends BasePresenter<IRecentMessageFgView> {

    private List<Conversation> mData = new ArrayList<>();
    private LQRAdapterForRecyclerView<Conversation> mAdapter;
    private int mUnreadCountTotal = 0;
    private LQRNineGridImageViewAdapter mNgivAdapter = new LQRNineGridImageViewAdapter<GroupMember>() {
        @Override
        protected void onDisplayImage(Context context, ImageView imageView, GroupMember groupMember) {
            Glide.with(context).load(groupMember.getPortraitUri()).centerCrop().into(imageView);
        }
    };
    private CustomDialog mConversationMenuDialog;

    public RecentMessageFgPresenter(BaseActivity context) {
        super(context);
    }

    public void getConversations() {
        loadData();
        setAdapter();
    }

    private void loadData() {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations != null && conversations.size() > 0) {
                    mData.clear();
                    mData.addAll(conversations);
                    filterData(mData);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.e("加载最近会话失败：" + errorCode);
            }
        });
    }

    private void filterData(List<Conversation> conversations) {
        for (int i = 0; i < conversations.size(); i++) {
            Conversation item = conversations.get(i);
            //其他消息会话不显示（比如：系统消息）
            if (!(item.getConversationType() == Conversation.ConversationType.PRIVATE || item.getConversationType() == Conversation.ConversationType.GROUP)) {
                conversations.remove(i);
                i--;
                continue;
            }
            if (item.getConversationType() == Conversation.ConversationType.GROUP) {
                List<GroupMember> groupMembers = DBManager.getInstance().getGroupMembers(item.getTargetId());
                if (groupMembers == null || groupMembers.size() == 0) {
                    DBManager.getInstance().deleteGroupsById(item.getTargetId());//删除没有群成员的群
                    conversations.remove(i);
                    i--;
                }
            } else if (item.getConversationType() == Conversation.ConversationType.PRIVATE) {
                if (!DBManager.getInstance().isMyFriend(item.getTargetId())) {
                    conversations.remove(i);
                    i--;
                }
            }
        }
        mUnreadCountTotal = 0;
        for (Conversation conversation : conversations) {
            mUnreadCountTotal += conversation.getUnreadMessageCount();
        }
        updateTotalUnreadView();
        if (mAdapter != null)
            mAdapter.notifyDataSetChangedWrapper();
    }

    private void updateTotalUnreadView() {
        if (mUnreadCountTotal > 0) {
            ((MainActivity) mContext).getTvMessageCount().setText(mUnreadCountTotal + "");
            ((MainActivity) mContext).getTvMessageCount().setVisibility(View.VISIBLE);
            ((MainActivity) mContext).setToolbarTitle(UIUtils.getString(com.cfk.xiaov.R.string.app_name) + "(" + mUnreadCountTotal + ")");
        } else {
            ((MainActivity) mContext).getTvMessageCount().setVisibility(View.GONE);
            ((MainActivity) mContext).setToolbarTitle(UIUtils.getString(com.cfk.xiaov.R.string.app_name));
        }
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<Conversation>(mContext, mData, com.cfk.xiaov.R.layout.item_recent_message) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, Conversation item, int position) {
                    if (item.getConversationType() == Conversation.ConversationType.PRIVATE) {
                        ImageView ivHeader = helper.getView(com.cfk.xiaov.R.id.ivHeader);
                        UserInfo userInfo = DBManager.getInstance().getUserInfo(item.getTargetId());
                        if (userInfo != null) {
                            Glide.with(mContext).load(userInfo.getPortraitUri()).centerCrop().into(ivHeader);
                            helper.setText(com.cfk.xiaov.R.id.tvDisplayName, userInfo.getName())
                                    .setText(com.cfk.xiaov.R.id.tvTime, TimeUtils.getMsgFormatTime(item.getReceivedTime()))
                                    .setViewVisibility(com.cfk.xiaov.R.id.ngiv, View.GONE)
                                    .setViewVisibility(com.cfk.xiaov.R.id.ivHeader, View.VISIBLE);
                        }
                    } else {
                        Groups groups = DBManager.getInstance().getGroupsById(item.getTargetId());
                        //九宫格头像
                        LQRNineGridImageView ngiv = helper.getView(com.cfk.xiaov.R.id.ngiv);
                        ngiv.setAdapter(mNgivAdapter);
                        ngiv.setImagesData(DBManager.getInstance().getGroupMembers(item.getTargetId()));
                        //群昵称
                        helper.setText(com.cfk.xiaov.R.id.tvDisplayName, groups == null ? "" : groups.getName())
                                .setText(com.cfk.xiaov.R.id.tvTime, TimeUtils.getMsgFormatTime(item.getReceivedTime()))
                                .setViewVisibility(com.cfk.xiaov.R.id.ngiv, View.VISIBLE)
                                .setViewVisibility(com.cfk.xiaov.R.id.ivHeader, View.GONE);
                    }

//                    helper.setBackgroundColor(R.id.flRoot, item.isTop() ? UIUtils.getColor(R.color.gray7) : UIUtils.getColor(android.R.color.white))
                    helper.setBackgroundColor(com.cfk.xiaov.R.id.flRoot, item.isTop() ? com.cfk.xiaov.R.color.gray8 : android.R.color.white)
                            .setText(com.cfk.xiaov.R.id.tvCount, item.getUnreadMessageCount() + "")
                            .setViewVisibility(com.cfk.xiaov.R.id.tvCount, item.getUnreadMessageCount() > 0 ? View.VISIBLE : View.GONE);
                    TextView tvContent = helper.getView(com.cfk.xiaov.R.id.tvContent);
                    if (!TextUtils.isEmpty(item.getDraft())) {
                        MoonUtils.identifyFaceExpression(mContext, tvContent, item.getDraft(), ImageSpan.ALIGN_BOTTOM);
                        helper.setViewVisibility(com.cfk.xiaov.R.id.tvDraft, View.VISIBLE);
                        return;
                    } else {
                        helper.setViewVisibility(com.cfk.xiaov.R.id.tvDraft, View.GONE);
                    }

                    if (item.getLatestMessage() instanceof TextMessage) {
                        MoonUtils.identifyFaceExpression(mContext, tvContent, ((TextMessage) item.getLatestMessage()).getContent(), ImageSpan.ALIGN_BOTTOM);
                    } else if (item.getLatestMessage() instanceof ImageMessage) {
                        tvContent.setText("[" + UIUtils.getString(com.cfk.xiaov.R.string.picture) + "]");
                    } else if (item.getLatestMessage() instanceof VoiceMessage) {
                        tvContent.setText("[" + UIUtils.getString(com.cfk.xiaov.R.string.voice) + "]");
                    } else if (item.getLatestMessage() instanceof FileMessage) {
                        FileMessage fileMessage = (FileMessage) item.getLatestMessage();
                        if (MediaFileUtils.isImageFileType(fileMessage.getName())) {
                            tvContent.setText("[" + UIUtils.getString(com.cfk.xiaov.R.string.sticker) + "]");
                        } else if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
                            tvContent.setText("[" + UIUtils.getString(com.cfk.xiaov.R.string.video) + "]");
                        }
                    } else if (item.getLatestMessage() instanceof LocationMessage) {
                        tvContent.setText("[" + UIUtils.getString(com.cfk.xiaov.R.string.location) + "]");
                    } else if (item.getLatestMessage() instanceof GroupNotificationMessage) {
                        GroupNotificationMessage groupNotificationMessage = (GroupNotificationMessage) item.getLatestMessage();
                        try {
                            UserInfo curUserInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
                            GroupNotificationMessageData data = JsonMananger.jsonToBean(groupNotificationMessage.getData(), GroupNotificationMessageData.class);
                            String operation = groupNotificationMessage.getOperation();
                            String notification = "";
                            String operatorName = data.getOperatorNickname().equals(curUserInfo.getName()) ? UIUtils.getString(com.cfk.xiaov.R.string.you) : data.getOperatorNickname();
                            String targetUserDisplayNames = "";
                            List<String> targetUserDisplayNameList = data.getTargetUserDisplayNames();
                            for (String name : targetUserDisplayNameList) {
                                targetUserDisplayNames += name.equals(curUserInfo.getName()) ? UIUtils.getString(com.cfk.xiaov.R.string.you) : name;
                            }
                            if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_CREATE)) {
                                notification = UIUtils.getString(com.cfk.xiaov.R.string.created_group, operatorName);
                            } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_DISMISS)) {
                                notification = operatorName + UIUtils.getString(com.cfk.xiaov.R.string.dismiss_groups);
                            } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_KICKED)) {
                                if (operatorName.contains(UIUtils.getString(com.cfk.xiaov.R.string.you))) {
                                    notification = UIUtils.getString(com.cfk.xiaov.R.string.remove_group_member, operatorName, targetUserDisplayNames);
                                } else {
                                    notification = UIUtils.getString(com.cfk.xiaov.R.string.remove_self, targetUserDisplayNames, operatorName);
                                }
                            } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_ADD)) {
                                notification = UIUtils.getString(com.cfk.xiaov.R.string.invitation, operatorName, targetUserDisplayNames);
                            } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_QUIT)) {
                                notification = operatorName + UIUtils.getString(com.cfk.xiaov.R.string.quit_groups);
                            } else if (operation.equalsIgnoreCase(GroupNotificationMessage.GROUP_OPERATION_RENAME)) {
                                notification = UIUtils.getString(com.cfk.xiaov.R.string.change_group_name, operatorName, data.getTargetGroupName());
                            }
                            tvContent.setText(notification);
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }
                    } else if (item.getLatestMessage() instanceof RedPacketMessage) {
                        RedPacketMessage redPacketMessage = (RedPacketMessage) item.getLatestMessage();
                        tvContent.setText("[" + UIUtils.getString(com.cfk.xiaov.R.string.wx_red_pack) + "]" + redPacketMessage.getContent());
                    }
                }
            };
            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
                Intent intent = new Intent(mContext, SessionActivity.class);
                Conversation item = mData.get(position);
                intent.putExtra("sessionId", item.getTargetId());
                if (item.getConversationType() == Conversation.ConversationType.PRIVATE) {
                    intent.putExtra("sessionType", SessionActivity.SESSION_TYPE_PRIVATE);
                } else {
                    intent.putExtra("sessionType", SessionActivity.SESSION_TYPE_GROUP);
                }
                mContext.jumpToActivity(intent);
            });
            mAdapter.setOnItemLongClickListener((helper, parent, itemView, position) -> {
                Conversation item = mData.get(position);
                View conversationMenuView = View.inflate(mContext, com.cfk.xiaov.R.layout.dialog_conversation_menu, null);
                mConversationMenuDialog = new CustomDialog(mContext, conversationMenuView, com.cfk.xiaov.R.style.MyDialog);
                TextView tvSetConversationToTop = (TextView) conversationMenuView.findViewById(com.cfk.xiaov.R.id.tvSetConversationToTop);
                tvSetConversationToTop.setText(item.isTop() ? UIUtils.getString(com.cfk.xiaov.R.string.cancel_conversation_to_top) : UIUtils.getString(com.cfk.xiaov.R.string.set_conversation_to_top));
                conversationMenuView.findViewById(com.cfk.xiaov.R.id.tvSetConversationToTop).setOnClickListener(v ->
                        RongIMClient.getInstance().setConversationToTop(item.getConversationType(), item.getTargetId(), !item.isTop(), new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                loadData();
                                mConversationMenuDialog.dismiss();
                                mConversationMenuDialog = null;
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        }));
                conversationMenuView.findViewById(com.cfk.xiaov.R.id.tvDeleteConversation).setOnClickListener(v -> {
                    RongIMClient.getInstance().removeConversation(item.getConversationType(), item.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            loadData();
                            mConversationMenuDialog.dismiss();
                            mConversationMenuDialog = null;
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                });
                mConversationMenuDialog.show();
                return true;
            });
            getView().getRvRecentMessage().setAdapter(mAdapter);
        }
    }
}
