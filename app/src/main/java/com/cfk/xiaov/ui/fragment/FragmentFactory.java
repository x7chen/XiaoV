package com.cfk.xiaov.ui.fragment;

/**
 * @创建者 CSDN_LQR
 * @描述 主界面4个Fragment工厂
 */
public class FragmentFactory {

    static FragmentFactory mInstance;

    private FragmentFactory() {
    }

    public static FragmentFactory getInstance() {
        if (mInstance == null) {
            synchronized (FragmentFactory.class) {
                if (mInstance == null) {
                    mInstance = new FragmentFactory();
                }
            }
        }
        return mInstance;
    }


    private DiscoveryFragment mDiscoveryFragment;
    private MeFragment mMeFragment;
    private VideoFragment mVideoFragment;



    public VideoFragment getVideoFragment() {
        if (mVideoFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mVideoFragment == null) {
                    mVideoFragment = new VideoFragment();
                }
            }
        }
        return mVideoFragment;
    }


    public DiscoveryFragment getDiscoveryFragment() {
        if (mDiscoveryFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mDiscoveryFragment == null) {
                    mDiscoveryFragment = new DiscoveryFragment();
                }
            }
        }
        return mDiscoveryFragment;
    }

    public MeFragment getMeFragment() {
        if (mMeFragment == null) {
            synchronized (FragmentFactory.class) {
                if (mMeFragment == null) {
                    mMeFragment = new MeFragment();
                }
            }
        }
        return mMeFragment;
    }
}
