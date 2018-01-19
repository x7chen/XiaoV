package com.cfk.xiaov.ui.fragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cfk.xiaov.R;
import com.cfk.xiaov.app.MyApp;
import com.cfk.xiaov.model.db.BondDevice;
import com.cfk.xiaov.ui.fragment.FragmentFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cfk on 2017/9/20.
 */

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactsHolder> {
    String TAG = getClass().getSimpleName();
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<BondDevice> mAdapterData;

    public ContactsListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ContactsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactsHolder(mLayoutInflater.inflate(R.layout.item_bond_device, parent, false));
    }

    @Override
    public void onBindViewHolder(ContactsHolder holder, int position) {

        ImageView mPhoto = holder.imgHeader;
        BondDevice device = mAdapterData.get(position);
        holder.deviceName.setText(device.getNickname());

        Glide.with(mContext).load(device.getAvatarUri()).asBitmap().centerCrop().into(new BitmapImageViewTarget(mPhoto) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                view.setImageDrawable(circularBitmapDrawable);
            }
        });

/*        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(AppConstants.MAKE_CALL);
            intent.putExtra("CallId", mAdapterData.get(holder.getAdapterPosition()).getAccount());
            mContext.sendBroadcast(intent);
            Log.i(TAG, "Monitor OnClick");
        });
        Log.i(TAG, "Create ContactsHolder");
        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            alertDialogBuilder.setMessage("删除？");
            alertDialogBuilder.setNegativeButton("确定", (dialog, which) -> {
                MyApp.getBondDeviceDao().deleteByKey(mAdapterData.get(holder.getAdapterPosition()).getId());
                mAdapterData.remove(holder.getAdapterPosition());
                FragmentFactory.getInstance().getContactsFragment().updateView();
            });
            alertDialogBuilder.setPositiveButton("取消", (dialog, which) -> {


            });
            alertDialogBuilder.create().show();
            return false;
        });*/
        if (!MyApp.isLogin) {
        }
    }

    @Override
    public int getItemCount() {
        return mAdapterData == null ? 0 : mAdapterData.size();
    }

    public void setAdapterData(List<BondDevice> devices) {
        mAdapterData = devices;
    }
    public List<BondDevice> getAdapterData() {
        return mAdapterData;
    }
    class ContactsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bond_device_name)
        TextView deviceName;
        @BindView(R.id.imgHeader)
        ImageView imgHeader;
        @BindView(R.id.video_call_view)
        RelativeLayout call_view;

        ContactsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            deviceName = (TextView) itemView.findViewById(R.id.bond_device_name);
//            imgHeader= (ImageView) itemView.findViewById(R.id.imgHeader);
//            call_view= (RelativeLayout) itemView.findViewById(R.id.video_call_view);

/*            itemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setAction(AppConstants.MAKE_CALL);
                intent.putExtra("CallId", mAdapterData.get(getAdapterPosition()).getAccount());
                mContext.sendBroadcast(intent);
                Log.i(TAG, "Monitor OnClick");
            });*/
            Log.i(TAG, "Create ContactsHolder");
            itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setMessage("删除？");
                alertDialogBuilder.setNegativeButton("确定", (dialog, which) -> {
                    MyApp.getBondDeviceDao().deleteByKey(mAdapterData.get(getAdapterPosition()).getId());
                    mAdapterData.remove(getAdapterPosition());
                    FragmentFactory.getInstance().getContactsFragment().updateView();
                });
                alertDialogBuilder.setPositiveButton("取消", (dialog, which) -> {


                });
                alertDialogBuilder.create().show();
                return false;
            });

        }

    }
}
