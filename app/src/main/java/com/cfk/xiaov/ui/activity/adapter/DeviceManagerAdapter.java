package com.cfk.xiaov.ui.activity.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.model.cache.BondCache;
import com.cfk.xiaov.model.data.ContactData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by cfk on 2017/9/20.
 */

public class DeviceManagerAdapter extends RecyclerView.Adapter<DeviceManagerAdapter.DeviceHolder> {
    String TAG = getClass().getSimpleName();
    Context mContext;
    LayoutInflater mLayoutInflater;

    ArrayList<ContactData> mContactDataList;
    public DeviceManagerAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    public void setAdapterData(ArrayList<ContactData> contactDatas){
        mContactDataList=contactDatas;
    }
    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceHolder(mLayoutInflater.inflate(R.layout.item_device_manager,parent,false));
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, int position) {
        holder.devideName.setText(mContactDataList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mContactDataList == null ? 0 : mContactDataList.size();
    }
    class DeviceHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.device_name)
        TextView devideName;
        @Bind(R.id.cv_dm_item)
        CardView cardView;

        public DeviceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(mContext);
                alertDialogBuilder.setMessage("删除？");
                alertDialogBuilder.setNegativeButton("确定", (dialog, which) -> {
                    mContactDataList.remove(getAdapterPosition());
                    BondCache.saveContacts(mContactDataList);
                    DeviceManagerAdapter.this.setAdapterData(BondCache.getContactList());
                    DeviceManagerAdapter.this.notifyDataSetChanged();
                });
                alertDialogBuilder.setPositiveButton("取消", (dialog, which) -> {


                });
                alertDialogBuilder.create().show();
                return false;
            });
        }
    }

}
