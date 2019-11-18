package com.aliyun.solution.longvideo.fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.alivcplayerexpand.util.NetWatchdog;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.activity.AlivcPlayerActivity;
import com.aliyun.solution.longvideo.adapter.AlivcSeriesQuickAdapter;
import com.aliyun.solution.longvideo.base.BaseLazyFragment;
import com.aliyun.solution.longvideo.bean.HomeSerierVideoListBean;
import com.aliyun.solution.longvideo.bean.SerierBannerBean;
import com.aliyun.solution.longvideo.bean.SerierSectionBean;
import com.aliyun.solution.longvideo.utils.UserSpUtils;
import com.aliyun.solution.longvideo.view.MultipleStatusView;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_HOME_PAGE_TV_PLAY_LIST;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_RECOMMNED_TV_PLAY_LIST;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_PAGE_INDEX;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_PAGE_SIZE;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_TOKEN;

/**
 * fragment_系列
 */
public class AlivcSeriesFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mSingleRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AlivcSeriesQuickAdapter mQuickAdapter;
    /**
     * 是否有下页
     */
    private int mNextRequestPage = 1;
    /**
     * 每页请求的数量
     */
    private int mPageSize = 10;


    private UserSpUtils userSpUtils;

    private ImageView mIVBanner;
    private TextView mTVTitle;
    private TextView mTVDuration;

    private NetWatchdog mNetWatchdog;
    private MultipleStatusView mMultipleStatusView;
    private View mHeaderView;

    private ArrayList<SerierSectionBean> sectionBeans;
    /**
     * 如果是刷新为true,如果是加载更多为false
     */
    boolean mIsRefresh;

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
                if (isLazyLoaded && (sectionBeans == null || sectionBeans.size() <= 0)) {
                    onRefresh();
                }
            }

            @Override
            public void onNetUnConnected() {
                if (sectionBeans == null || sectionBeans.size() <= 0) {
                    mMultipleStatusView.showNoNetwork();
                }
            }
        });
    }

    public static Fragment getInstance() {
        Fragment fragment = new AlivcSeriesFragment();
        return fragment;
    }

    @Override
    public void onLazyLoad() {

        onRefresh();

    }

    @Override
    public int getContentView() {
        return R.layout.alivc_long_video_fragment_singelset;
    }

    @Override
    public void initView(View view) {
        userSpUtils = new UserSpUtils.Builder(getContext()).create();
        mSwipeRefreshLayout = view.findViewById(R.id.alivc_single_SwipeRefreshLayout);
        mSingleRecyclerView = view.findViewById(R.id.alivc_single_RecyclerView);
        mMultipleStatusView = view.findViewById(R.id.mMultipleStatusView);
        initAdapter();
    }

    @Override
    public void initListener() {
        initNetWatchdog();
        mMultipleStatusView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNetWatchdog.stopWatch();
    }

    private void initAdapter() {

        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mQuickAdapter = new AlivcSeriesQuickAdapter(R.layout.alivc_long_video_fragment_home_content_item, R.layout.alivc_rv_header_item, null);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, OrientationHelper.VERTICAL, false);
        mSingleRecyclerView.setLayoutManager(gridLayoutManager);
        mSingleRecyclerView.setAdapter(mQuickAdapter);
        setHeaderView();

        mQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SerierSectionBean bean = (SerierSectionBean) adapter.getData().get(position);
                LongVideoBean t = bean.t;
                if (t == null) {
                    return;
                }
                AlivcPlayerActivity.startAlivcPlayerActivity(getContext(), t);
            }
        });
        //上拉加载

        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                //加载完毕
                mIsRefresh = false;
                if (mNextRequestPage == 1) {
                    mQuickAdapter.loadMoreEnd();
                    return;
                }
                mSwipeRefreshLayout.setRefreshing(false);
                loadVideosList();

            }
        }, mSingleRecyclerView);
        //下拉加载更多
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * banner view
     */
    private void setHeaderView() {
        mHeaderView = getActivity().getLayoutInflater().inflate(R.layout.alivc_long_video_fragment_home_header_item, mSingleRecyclerView, false);
        mIVBanner = mHeaderView.findViewById(R.id.alivc_iv_banner);
        mTVTitle = mHeaderView.findViewById(R.id.alivc_tv_title);
        mTVDuration = mHeaderView.findViewById(R.id.alivc_tv_duration);
        mQuickAdapter.addHeaderView(mHeaderView);
    }

    @Override
    public void onRefresh() {
        mNextRequestPage = 1;
        mIsRefresh = true;
        mQuickAdapter.setEnableLoadMore(false);
        //进行加载
        loadBanner();
        loadVideosList();
    }


    /**
     * 设置数据
     */
    private void setData(boolean isRefresh, List<SerierSectionBean> mutableList) {
        mNextRequestPage++;
        int size = mutableList == null ? 0 : mutableList.size();

        //是下拉刷新
        if (isRefresh) {
            mQuickAdapter.setNewData(mutableList);
        } else {//上拉刷新
            if (size > 0) {
                mQuickAdapter.addData(mutableList);
            }
            if (size < mPageSize) {
                //第一页如果不够一页就不显示没有更多数据布局
                mQuickAdapter.loadMoreEnd(true);
            } else {
                mQuickAdapter.loadMoreComplete();
            }
        }
    }

    /**
     * 加载系列banner图
     */
    private void loadBanner() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_TOKEN, userSpUtils.getUserToken());
        hashMap.put(KEY_PAGE_INDEX, String.valueOf(mNextRequestPage));
        hashMap.put(KEY_PAGE_SIZE, String.valueOf(mPageSize));

        AlivcOkHttpClient.getInstance().get(GET_RECOMMNED_TV_PLAY_LIST, hashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {

            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                SerierBannerBean bannerBean = gson.fromJson(result, SerierBannerBean.class);

                //后台返回的数组可能为空，如果为空移除banner
                boolean empty = bannerBean.getData().getTvPlayList().isEmpty();
                if (!empty) {
                    /*目前返回的是数组，取第一个作为banner*/
                    if (mQuickAdapter.getHeaderLayoutCount() == 0) {
                        mQuickAdapter.addHeaderView(mHeaderView);
                    }
                    setSeriesBanner(bannerBean.getData().getTvPlayList().get(0));
                } else {
                    mQuickAdapter.removeAllHeaderView();
                }
            }
        });
    }

    /**
     * 加载首页视频列表
     */
    private void loadVideosList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_TOKEN, userSpUtils.getUserToken());
        hashMap.put(KEY_PAGE_INDEX, String.valueOf(mNextRequestPage));
        hashMap.put(KEY_PAGE_SIZE, String.valueOf(mPageSize));

        AlivcOkHttpClient.getInstance().get(GET_HOME_PAGE_TV_PLAY_LIST, hashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(Request request, String result) {
                /*如果是第一页那么就是下拉刷新*/
                mSwipeRefreshLayout.setRefreshing(false);
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                HomeSerierVideoListBean videoListBean = gson.fromJson(result, HomeSerierVideoListBean.class);

                List<HomeSerierVideoListBean.DataBean.TagTvPlayListBean> tagVideoListBeans = videoListBean.getData().getTagTvPlayList();
                boolean empty = tagVideoListBeans.isEmpty();
                sectionBeans = new ArrayList<>();
                if (!empty) {
                    //处理数据，当tag不等于空的时候，处理成带section的列表
                    for (HomeSerierVideoListBean.DataBean.TagTvPlayListBean tagVideoListBean : tagVideoListBeans) {
                        sectionBeans.add(new SerierSectionBean(true, tagVideoListBean.getTag()));
                        for (LongVideoBean listBean : tagVideoListBean.getTvPlayList()) {
                            sectionBeans.add(new SerierSectionBean(listBean));
                        }
                    }
                }
                setData(mIsRefresh, sectionBeans);

            }
        });
    }

    /**
     * 设置头部banner的属性
     */
    private void setSeriesBanner(final LongVideoBean bannerBean) {

        if (isAdded()) {
            String duration = String.format(getResources().getString(R.string.alivc_longvideo_series_set), bannerBean.getTotal());
            mTVDuration.setText(duration);
        }
        String title = bannerBean.getTitle();
        String url = bannerBean.getCoverUrl();

        mTVTitle.setText(title);
        new ImageLoaderImpl().loadImage(getActivity(), url, new ImageLoaderOptions.Builder()
                                        .crossFade()
                                        .error(R.mipmap.ic_launcher)
                                        .build()).into(mIVBanner);

        mIVBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlivcPlayerActivity.startAlivcPlayerActivity(getContext(), bannerBean);
            }

        });
    }
}
