package com.cfk.xiaov.ui.presenter;

import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.ui.view.IVideoFgView;

public class VideoFgPresenter extends BasePresenter<IVideoFgView> {


    public VideoFgPresenter(BaseActivity context) {
        super(context);
    }

    public void getConversations() {
        loadData();
        setAdapter();
    }

    private void loadData() {
    }


    private void setAdapter() {

    }
}
