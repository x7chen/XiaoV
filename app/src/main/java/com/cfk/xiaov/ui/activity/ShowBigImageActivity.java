package com.cfk.xiaov.ui.activity;

import android.net.Uri;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.cfk.xiaov.app.AppConst;
import com.cfk.xiaov.model.cache.MyInfoCache;
import com.cfk.xiaov.ui.base.BaseActivity;
import com.cfk.xiaov.ui.base.BasePresenter;
import com.cfk.xiaov.util.PopupWindowUtils;
import com.cfk.xiaov.util.UIUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import okhttp3.ResponseBody;

/**
 * @创建者 Sean
 * @描述 查看头像
 */
public class ShowBigImageActivity extends BaseActivity {


    @BindView(com.cfk.xiaov.R.id.ibToolbarMore)
    ImageButton mIbToolbarMore;
    @BindView(com.cfk.xiaov.R.id.pv)
    PhotoView mPv;
    @BindView(com.cfk.xiaov.R.id.pb)
    ProgressBar mPb;
    private FrameLayout mView;
    private PopupWindow mPopupWindow;

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(com.cfk.xiaov.R.string.header_pic));
        mIbToolbarMore.setVisibility(View.VISIBLE);
        mPv.enable();// 启用图片缩放功能

        Glide.with(this).load(MyInfoCache.getAvatarUri()).placeholder(com.cfk.xiaov.R.mipmap.default_image).into(mPv);

    }

    @Override
    public void initListener() {
        mIbToolbarMore.setOnClickListener(v -> showPopupMenu());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return com.cfk.xiaov.R.layout.activity_show_big_image;
    }

    private void showPopupMenu() {
        if (mView == null) {
            mView = new FrameLayout(this);
            mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mView.setBackgroundColor(UIUtils.getColor(com.cfk.xiaov.R.color.white));

            TextView tv = new TextView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2Px(45));
            tv.setLayoutParams(params);
            tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tv.setPadding(UIUtils.dip2Px(20), 0, 0, 0);
            tv.setTextColor(UIUtils.getColor(com.cfk.xiaov.R.color.gray0));
            tv.setTextSize(14);
            tv.setText(UIUtils.getString(com.cfk.xiaov.R.string.save_to_phone));
            mView.addView(tv);

            tv.setOnClickListener(v -> {
                File file = new File(Uri.parse(MyInfoCache.getAvatarUri()).getPath());
                UIUtils.showToast(copyToDisk(file) ? UIUtils.getString(com.cfk.xiaov.R.string.save_success) : UIUtils.getString(com.cfk.xiaov.R.string.save_fail));
                mPopupWindow.dismiss();
                mPopupWindow = null;

            });
        }
        mPopupWindow = PopupWindowUtils.getPopupWindowAtLocation(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(() -> PopupWindowUtils.makeWindowLight(ShowBigImageActivity.this));
        PopupWindowUtils.makeWindowDark(ShowBigImageActivity.this);
    }

    private boolean copyToDisk(File file) {
        try {
            InputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new FileOutputStream(new File(AppConst.HEADER_SAVE_DIR, SystemClock.currentThreadTimeMillis() + "_header.jpg"));
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            } catch (IOException e) {
                return false;
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean saveToDisk(ResponseBody body) {
        try {
            InputStream in = null;
            FileOutputStream out = null;
            try {
                in = body.byteStream();
                out = new FileOutputStream(new File(AppConst.HEADER_SAVE_DIR, SystemClock.currentThreadTimeMillis() + "_header.jpg"));
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            } catch (IOException e) {
                return false;
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

