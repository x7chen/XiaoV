package com.cfk.xiaov.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.GroupMember;
import com.cfk.xiaov.model.cache.UserCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.recyclerview.LQRRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.rong.imlib.model.UserInfo;

/**
 * @创建者 CSDN_LQR
 * @描述 移出群成员界面(该界面简单就不使用mvp了)
 */

public class RemoveGroupMemberActivity extends BaseActivity {

    private String mGroupId;
    private List<GroupMember> mData = new ArrayList<>();
    private List<GroupMember> mSelectedData = new ArrayList<>();

    @Bind(com.cfk.xiaov.R.id.btnToolbarSend)
    Button mBtnToolbarSend;
    @Bind(com.cfk.xiaov.R.id.rvMember)
    LQRRecyclerView mRvMember;
    private LQRAdapterForRecyclerView<GroupMember> mAdapter;

    @Override
    public void init() {
        mGroupId = getIntent().getStringExtra("sessionId");
    }

    @Override
    public void initView() {
        if (TextUtils.isEmpty(mGroupId)) {
            finish();
            return;
        }

        mBtnToolbarSend.setText(UIUtils.getString(com.cfk.xiaov.R.string.delete));
        mBtnToolbarSend.setVisibility(View.VISIBLE);
        mBtnToolbarSend.setBackgroundResource(com.cfk.xiaov.R.drawable.shape_btn_delete);
        mBtnToolbarSend.setEnabled(false);
    }

    @Override
    public void initData() {
        List<GroupMember> groupMembers = DBManager.getInstance().getGroupMembers(mGroupId);
        if (groupMembers != null && groupMembers.size() > 0) {
            for (int i = 0; i < groupMembers.size(); i++) {
                GroupMember groupMember = groupMembers.get(i);
                if (groupMember.getUserId().equals(UserCache.getId())) {
                    groupMembers.remove(i);
                    break;
                }
            }
            mData.clear();
            mData.addAll(groupMembers);
        }
        setAdapter();
    }

    @Override
    public void initListener() {
        mBtnToolbarSend.setOnClickListener(v -> {
            ArrayList<String> selectedIds = new ArrayList<>(mSelectedData.size());
            for (int i = 0; i < mSelectedData.size(); i++) {
                GroupMember groupMember = mSelectedData.get(i);
                selectedIds.add(groupMember.getUserId());
            }
            Intent data = new Intent();
            data.putStringArrayListExtra("selectedIds", selectedIds);
            setResult(Activity.RESULT_OK, data);
            finish();
        });
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<GroupMember>(this, mData, com.cfk.xiaov.R.layout.item_contact) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, GroupMember item, int position) {
                    helper.setText(com.cfk.xiaov.R.id.tvName, item.getName()).setViewVisibility(com.cfk.xiaov.R.id.cb, View.VISIBLE);
                    ImageView ivHeader = helper.getView(com.cfk.xiaov.R.id.ivHeader);
                    Glide.with(RemoveGroupMemberActivity.this).load(item.getPortraitUri()).centerCrop().into(ivHeader);

                    CheckBox cb = helper.getView(com.cfk.xiaov.R.id.cb);
                    cb.setClickable(true);
                    cb.setChecked(mSelectedData.contains(item) ? true : false);
                    cb.setOnClickListener(v -> {
                        if (cb.isChecked()) {
                            mSelectedData.add(item);
                        } else {
                            mSelectedData.remove(item);
                        }
                        if (mSelectedData.size() > 0) {
                            mBtnToolbarSend.setEnabled(true);
                            mBtnToolbarSend.setText(UIUtils.getString(com.cfk.xiaov.R.string.delete) + "(" + mSelectedData.size() + ")");
                        } else {
                            mBtnToolbarSend.setEnabled(false);
                            mBtnToolbarSend.setText(UIUtils.getString(com.cfk.xiaov.R.string.delete));
                        }
                    });
                }
            };
            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
                UserInfo userInfo = DBManager.getInstance().getUserInfo(mData.get(position).getUserId());
                if (userInfo != null) {
                    Intent intent = new Intent(RemoveGroupMemberActivity.this, UserInfoActivity.class);
                    intent.putExtra("userInfo", userInfo);
                    jumpToActivity(intent);
                }
            });
            mRvMember.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChangedWrapper();
        }
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_remove_group_member;
    }

}
