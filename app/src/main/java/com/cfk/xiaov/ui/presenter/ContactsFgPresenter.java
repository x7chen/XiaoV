package com.cfk.xiaov.ui.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.Friend;
import com.cfk.xiaov.ui.activity.UserInfoActivity;
import com.cfk.xiaov.ui.view.IContactsFgView;
import com.cfk.xiaov.util.SortUtils;
import com.cfk.xiaov.util.UIUtils;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRHeaderAndFooterAdapter;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ContactsFgPresenter extends BasePresenter<IContactsFgView> {

    private List<Friend> mData = new ArrayList<>();
    private LQRHeaderAndFooterAdapter mAdapter;

    public ContactsFgPresenter(BaseActivity context) {
        super(context);
    }

    public void loadContacts() {
        setAdapter();
        loadData();
    }

    private void loadData() {
        Observable.just(DBManager.getInstance().getFriends())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friends -> {
                    if (friends != null && friends.size() > 0) {
                        mData.clear();
                        mData.addAll(friends);
                        getView().getFooterView().setText(UIUtils.getString(com.cfk.xiaov.R.string.count_of_contacts, mData.size()));
                        //整理排序
                        SortUtils.sortContacts(mData);
                        if (mAdapter != null)
                            mAdapter.notifyDataSetChanged();
                    }
                }, this::loadError);
    }

    private void setAdapter() {
        if (mAdapter == null) {
            LQRAdapterForRecyclerView adapter = new LQRAdapterForRecyclerView<Friend>(mContext, mData, com.cfk.xiaov.R.layout.item_contact) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, Friend item, int position) {
                    helper.setText(com.cfk.xiaov.R.id.tvName, item.getDisplayName());
                    ImageView ivHeader = helper.getView(com.cfk.xiaov.R.id.ivHeader);
                    Glide.with(mContext).load(item.getPortraitUri()).centerCrop().into(ivHeader);

                    String str = "";
                    //得到当前字母
                    String currentLetter = item.getDisplayNameSpelling().charAt(0) + "";
                    if (position == 0) {
                        str = currentLetter;
                    } else {
                        //得到上一个字母
                        String preLetter = mData.get(position - 1).getDisplayNameSpelling().charAt(0) + "";
                        //如果和上一个字母的首字母不同则显示字母栏
                        if (!preLetter.equalsIgnoreCase(currentLetter)) {
                            str = currentLetter;
                        }
                    }
                    int nextIndex = position + 1;
                    if (nextIndex < mData.size() - 1) {
                        //得到下一个字母
                        String nextLetter = mData.get(nextIndex).getDisplayNameSpelling().charAt(0) + "";
                        //如果和下一个字母的首字母不同则隐藏下划线
                        if (!nextLetter.equalsIgnoreCase(currentLetter)) {
                            helper.setViewVisibility(com.cfk.xiaov.R.id.vLine, View.INVISIBLE);
                        } else {
                            helper.setViewVisibility(com.cfk.xiaov.R.id.vLine, View.VISIBLE);
                        }
                    } else {
                        helper.setViewVisibility(com.cfk.xiaov.R.id.vLine, View.INVISIBLE);
                    }
                    if (position == mData.size() - 1) {
                        helper.setViewVisibility(com.cfk.xiaov.R.id.vLine, View.GONE);
                    }

                    //根据str是否为空决定字母栏是否显示
                    if (TextUtils.isEmpty(str)) {
                        helper.setViewVisibility(com.cfk.xiaov.R.id.tvIndex, View.GONE);
                    } else {
                        helper.setViewVisibility(com.cfk.xiaov.R.id.tvIndex, View.VISIBLE);
                        helper.setText(com.cfk.xiaov.R.id.tvIndex, str);
                    }
                }
            };
            adapter.addHeaderView(getView().getHeaderView());
            adapter.addFooterView(getView().getFooterView());
            mAdapter = adapter.getHeaderAndFooterAdapter();
            getView().getRvContacts().setAdapter(mAdapter);
        }
        ((LQRAdapterForRecyclerView) mAdapter.getInnerAdapter()).setOnItemClickListener((lqrViewHolder, viewGroup, view, i) -> {
            Intent intent = new Intent(mContext, UserInfoActivity.class);
            intent.putExtra("userInfo", DBManager.getInstance().getUserInfo(mData.get(i - 1).getUserId()));//-1是因为有头部
            mContext.jumpToActivity(intent);
        });
    }

    private void loadError(Throwable throwable) {
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(UIUtils.getString(com.cfk.xiaov.R.string.load_contacts_error));
    }
}
