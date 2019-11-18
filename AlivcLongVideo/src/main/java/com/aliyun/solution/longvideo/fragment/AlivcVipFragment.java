package com.aliyun.solution.longvideo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.aliyun.player.alivcplayerexpand.util.NetWatchdog;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.adapter.AlivcFragmentPagerAdapter;
import com.aliyun.solution.longvideo.base.BaseLazyFragment;
import com.aliyun.solution.longvideo.bean.VipListTypeBean;
import com.aliyun.solution.longvideo.utils.UserSpUtils;
import com.aliyun.solution.longvideo.view.MultipleStatusView;
import com.aliyun.solution.longvideo.view.PagerSlidingTabStrip;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Request;

import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_VIP_TYPE_LIST;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_TOKEN;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_TYPE;

/**
 * fragment_Vip
 */
public class AlivcVipFragment extends BaseLazyFragment {


    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private MultipleStatusView mMultipleStatusView;

    private UserSpUtils userSpUtils;

    private NetWatchdog mNetWatchdog;

    private AlivcFragmentPagerAdapter mAlivcFragmentPagerAdapter;

    private ArrayList<String> titles;

    /**
     * 初始化网络监听
     */
    private void initNetWatchdog() {
        mNetWatchdog = new NetWatchdog(getActivity());
        mNetWatchdog.startWatch();

        mNetWatchdog.setNetConnectedListener(new NetWatchdog.NetConnectedListener() {
            @Override
            public void onReNetConnected(boolean isReconnect) {
                mMultipleStatusView.showContent();
                if (isLazyLoaded) {
                    if (isLazyLoaded && (titles == null || titles.size() <= 0)) {
                        loadVipListType();
                    }
                }
            }

            @Override
            public void onNetUnConnected() {
                if(titles == null || titles.size() <= 0){
                    mMultipleStatusView.showNoNetwork();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNetWatchdog.stopWatch();
    }

    public static Fragment getInstance() {
        Fragment fragment = new AlivcVipFragment();
        return fragment;
    }

    @Override
    public void onLazyLoad() {
        loadVipListType();
    }

    @Override
    public int getContentView() {
        return R.layout.alivc_long_video_fragment_vip;
    }

    @Override
    public void initView(View view) {
        userSpUtils = new UserSpUtils.Builder(getContext()).create();
        mPagerSlidingTabStrip = view.findViewById(R.id.alivc_home_pager_tab_trip);
        mViewPager = view.findViewById(R.id.alivc_home_viewpager);
        mMultipleStatusView = view.findViewById(R.id.mMultipleStatusView);
        initNetWatchdog();

    }

    @Override
    public void initListener() {
        mMultipleStatusView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVipListType();
            }
        });
    }

    private void initView(ArrayList<Fragment> fragments, ArrayList<String> titles) {

        mAlivcFragmentPagerAdapter = new AlivcFragmentPagerAdapter(getChildFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mAlivcFragmentPagerAdapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(10);
        setPagerSlidingTabStripValue(titles);

    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setPagerSlidingTabStripValue(ArrayList<String> titles) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 设置Tab是自动填充满屏幕的
        //如果title个数少于2个,则通过weight铺满屏幕,否则,使用wrap_content方式排列,防止标题字数过多显示不全问题
        mPagerSlidingTabStrip.setShouldExpand((titles == null || titles.size() <= 2));
        // 设置Tab的分割线是透明的
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 1f, dm));
        // 设置Tab Indicator的高度
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4f, dm));
        // 设置Tab标题文字的大小
        mPagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(
                                              TypedValue.COMPLEX_UNIT_SP, 16f, dm));
        // 设置Tab Indicator的颜色
        mPagerSlidingTabStrip.setIndicatorColor(ContextCompat.getColor(getActivity(), R.color.alivc_longvideo_slide_tab_orange));
        // 设置选中Tab文字的颜色
        mPagerSlidingTabStrip.setSelectedTextColor(ContextCompat.getColor(getActivity(), R.color.alivc_longvideo_font_black));
        // 取消点击Tab时的背景色
        mPagerSlidingTabStrip.setTabBackground(0);
    }

    /**
     * 加载vip的type列表
     */
    private void loadVipListType() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_TOKEN, userSpUtils.getUserToken());
        hashMap.put(KEY_TYPE, "3");
        AlivcOkHttpClient.getInstance().get(GET_VIP_TYPE_LIST, hashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.show(getContext(), getResources().getString(R.string.alivc_longvideo_not_network_retry));
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                VipListTypeBean typeBean = gson.fromJson(result, VipListTypeBean.class);
                if (typeBean != null && typeBean.getData() != null) {
                    titles = new ArrayList<>();
                    ArrayList<Fragment> fragments = new ArrayList<>();
                    for (VipListTypeBean.DataBean dataBean : typeBean.getData()) {
                        titles.add(dataBean.getTagName());
                        Bundle bundle = new Bundle();
                        bundle.putString("tagName", dataBean.getTagName());
                        fragments.add(AlivcVipListFragment.getInstance(bundle));
                    }
                    initView(fragments, titles);

                }

            }
        });
    }

}
