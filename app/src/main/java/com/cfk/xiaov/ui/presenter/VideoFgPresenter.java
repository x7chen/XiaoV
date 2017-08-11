package com.cfk.xiaov.ui.presenter;

import android.content.Intent;

import com.cfk.xiaov.model.cache.AccountCache;
import com.cfk.xiaov.model.cache.BondCache;
import com.cfk.xiaov.ui.activity.CallActivity;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IVideoFgView;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.util.ArrayList;

public class VideoFgPresenter extends BasePresenter<IVideoFgView> {

    BaseActivity mContext;
    public VideoFgPresenter(BaseActivity context) {
        super(context);
        mContext = context;
    }

    public void getConversations() {
        loadData();
        setAdapter();
    }

    private void loadData() {
    }


    private void setAdapter() {

    }

    /**
     * 发起呼叫
     */
    private void makeCall(int callType, ArrayList<String> nums){

        Intent intent = new Intent();
        intent.setClass(mContext, CallActivity.class);
        intent.putExtra("HostId", ILiveLoginManager.getInstance().getMyUserId());
        intent.putExtra("CallId", 0);
        intent.putExtra("CallType", callType);
        intent.putStringArrayListExtra("CallNumbers", nums);
        mContext.startActivity(intent);
    }
    public void callBondDevice(){
        ArrayList<String> nums = new ArrayList<>();
        nums.add(BondCache.getBondId());
        makeCall(ILVCallConstants.CALL_TYPE_VIDEO,nums);
    }
}
