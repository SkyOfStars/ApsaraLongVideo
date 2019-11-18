package com.aliyun.solution.longvideo.fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.adapter.AlivcFragmentPagerAdapter;
import com.aliyun.solution.longvideo.base.BaseLazyFragment;
import com.aliyun.solution.longvideo.view.PagerSlidingTabStrip;

import java.util.ArrayList;

/**
 * fragment_首页
 */
public class AlivcHomeFragment extends BaseLazyFragment {

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    /**
     * fragment,包含单集，系列
     */
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    /**
     * fragment的名称
     */
    private ArrayList<String> mTitles = new ArrayList<>();


    public static Fragment getInstance() {
        Fragment fragment = new AlivcHomeFragment();
        return fragment;
    }


    @Override
    public void onLazyLoad() {

        mTitles.add(getResources().getString(R.string.alivc_longvideo_singleset_title));
        mTitles.add(getResources().getString(R.string.alivc_longvideo_series_title));

        mFragments.add(AlivcSingleSetFragment.getInstance());
        mFragments.add(AlivcSeriesFragment.getInstance());

        AlivcFragmentPagerAdapter pagerAdapter = new AlivcFragmentPagerAdapter(getChildFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(pagerAdapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(1);
        setPagerSlidingTabStripValue();
    }

    @Override
    public int getContentView() {
        return R.layout.alivc_long_video_fragment_home;
    }

    @Override
    public void initView(View view) {
        mPagerSlidingTabStrip = view.findViewById(R.id.alivc_home_pager_tab_trip);
        mViewPager = view.findViewById(R.id.alivc_home_viewpager);
    }

    @Override
    public void initListener() {


    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setPagerSlidingTabStripValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 设置Tab是自动填充满屏幕的
        mPagerSlidingTabStrip.setShouldExpand(true);
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
}
