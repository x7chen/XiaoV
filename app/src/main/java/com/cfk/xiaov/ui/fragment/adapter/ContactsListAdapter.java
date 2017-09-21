package com.cfk.xiaov.ui.fragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cfk.xiaov.R;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.model.data.ContactData;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cfk on 2017/9/20.
 */

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactsHolder> {
    String TAG = getClass().getSimpleName();
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<ContactData> mAdapterData;

    public ContactsListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ContactsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactsHolder(mLayoutInflater.inflate(R.layout.item_contacts,parent,false));
    }

    @Override
    public void onBindViewHolder(ContactsHolder holder, int position) {
//        if(position==0){
//            ViewGroup.LayoutParams layoutParams = holder.call_view.getLayoutParams();
//            layoutParams.width = 900;
//            holder.call_view.setLayoutParams(layoutParams);
//        }
        holder.bondDeviceName.setText(mAdapterData.get(position).getId());

    }

    @Override
    public int getItemCount() {
        return mAdapterData == null ? 0 : mAdapterData.size();
    }

    public void setAdapterData(ArrayList<ContactData> contactDatas){
        mAdapterData = contactDatas;
    }

    class ContactsHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.bond_device_name)
        TextView bondDeviceName;
        @Bind(R.id.btMakeCall)
        ImageButton btMonitor;
        @Bind(R.id.video_call_view)
        RelativeLayout call_view;
        public ContactsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            btMonitor.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(AppConst.MAKE_CALL);
                intent.putExtra("CallId",mAdapterData.get(getAdapterPosition()).getId());
                mContext.sendBroadcast(intent);
            });
            Log.i(TAG,"Create ContactsHolder");
        }
    }
}
