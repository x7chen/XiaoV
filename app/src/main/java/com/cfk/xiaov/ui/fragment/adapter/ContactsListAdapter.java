package com.cfk.xiaov.ui.fragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.api.ApiRetrofit;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.db.DBManager;
import com.cfk.xiaov.db.model.Friend;
import com.cfk.xiaov.db.model.UserInfo;
import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.data.ContactData;
import com.cfk.xiaov.ui.activity.adapter.DeviceManagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cfk on 2017/9/20.
 */

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactsHolder> {
    String TAG = getClass().getSimpleName();
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<Friend> mAdapterData;

    public ContactsListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ContactsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactsHolder(mLayoutInflater.inflate(R.layout.item_bond_device,parent,false));
    }

    @Override
    public void onBindViewHolder(ContactsHolder holder, int position) {
//        if(position==0){
//            ViewGroup.LayoutParams layoutParams = holder.call_view.getLayoutParams();
//            layoutParams.width = 900;
//            holder.call_view.setLayoutParams(layoutParams);
//        }

        ImageView mPhoto = holder.imgHeader;
        Friend friend = DBManager.getInstance().getFriendById(mAdapterData.get(position).getUserId());
        holder.friendName.setText(friend.getName());
        ApiRetrofit.getInstance().getQiNiuDownloadUrl(friend.getPortraitUri()+"?imageView2/1/w/200/h/200")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qiNiuDownloadResponse -> {
                    if (qiNiuDownloadResponse != null && qiNiuDownloadResponse.getCode() == 200) {
                        String pic = qiNiuDownloadResponse.getResult().getPrivateDownloadUrl();
                        //Glide.with(mContext).load(pic).centerCrop().into(mPhoto);
                        Glide.with(mContext).load(pic).asBitmap().centerCrop().into(new BitmapImageViewTarget(mPhoto) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                view.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                    }
                });

    }

    @Override
    public int getItemCount() {
        return mAdapterData == null ? 0 : mAdapterData.size();
    }

    public void setAdapterData(List<Friend> contactDatas){
        mAdapterData = contactDatas;
    }

    class ContactsHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.bond_device_name)
        TextView friendName;
        @Bind(R.id.imgHeader)
        ImageView imgHeader;
        @Bind(R.id.video_call_view)
        RelativeLayout call_view;
        public ContactsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(AppConst.MAKE_CALL);
                intent.putExtra("CallId",mAdapterData.get(getAdapterPosition()).getUserId());
                mContext.sendBroadcast(intent);
                Log.i(TAG,"Monitor OnClick");
            });
            Log.i(TAG,"Create ContactsHolder");
            itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(mContext);
                alertDialogBuilder.setMessage("删除？");
                alertDialogBuilder.setNegativeButton("确定", (dialog, which) -> {
                    DBManager.getInstance().deleteFriendById(mAdapterData.get(getAdapterPosition()).getUserId());
                    mAdapterData.remove(getAdapterPosition());
                    ContactsListAdapter.this.notifyDataSetChanged();
                });
                alertDialogBuilder.setPositiveButton("取消", (dialog, which) -> {


                });
                alertDialogBuilder.create().show();
                return false;
            });
        }
    }
}
