package com.aliyun.solution.longvideo.fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.alivcplayerexpand.util.NetWatchdog;
import com.aliyun.player.alivcplayerexpand.util.TimeFormater;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.activity.AlivcPlayerActivity;
import com.aliyun.solution.longvideo.adapter.AlivcSingleQuickAdapter;
import com.aliyun.solution.longvideo.base.BaseLazyFragment;
import com.aliyun.solution.longvideo.bean.HomeSingleSetVideoListBean;
import com.aliyun.solution.longvideo.bean.RandomUserBean;
import com.aliyun.solution.longvideo.bean.SingleBanerBean;
import com.aliyun.solution.longvideo.bean.SingleSectionBean;
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

import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_LONG_VIDEOS_LIST;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_RECOMMNED_LONG_VIDEOS_LIST;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_USER_INFO;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_PAGE_INDEX;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_PAGE_SIZE;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_TOKEN;

/**
 * fragment_单集
 */
public class AlivcSingleSetFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView mSingleRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AlivcSingleQuickAdapter mQuickAdapter;
    /**
     * 是否有下页
     */
    private int mNextRequestPage = 1;

    private int mPageSize = 10;

    private UserSpUtils userSpUtils;

    private ImageView mIVBanner;
    private TextView mTVTitle;
    private TextView mTVDuration;
    private View mHeaderView;
    /**
     * 网络错误页面
     */
    private NetWatchdog mNetWatchdog;
    private MultipleStatusView mMultipleStatusView;

    private ArrayList<SingleSectionBean> sectionBeans;
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
                    loadUser();
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
        Fragment fragment = new AlivcSingleSetFragment();
        return fragment;
    }

    @Override
    public void onLazyLoad() {
        loadUser();
    }

    @Override
    public int getContentView() {
        return R.layout.alivc_long_video_fragment_singelset;
    }

    @Override
    public void initView(View view) {
        if (getActivity() != null) {
            userSpUtils = new UserSpUtils.Builder(getContext()).create();
        }
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

        mQuickAdapter = new AlivcSingleQuickAdapter(R.layout.alivc_long_video_fragment_home_content_item, R.layout.alivc_rv_header_item, null);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, OrientationHelper.VERTICAL, false);
        mSingleRecyclerView.setLayoutManager(gridLayoutManager);
        mSingleRecyclerView.setAdapter(mQuickAdapter);
        getHeaderView();

        mQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SingleSectionBean bean = (SingleSectionBean) adapter.getData().get(position);
                if (bean.t != null) {
                    AlivcPlayerActivity.startAlivcPlayerActivity(getContext(), bean.t);

                }
            }
        });

        //上拉加载
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                /*第一次进入界面的时候不加载更多*/
                mIsRefresh = false;
                if (mNextRequestPage == 1) {
                    mQuickAdapter.setEnableLoadMore(false);
                    return;
                }
                mSwipeRefreshLayout.setRefreshing(false);
                loadVideosList();
            }
        }, mSingleRecyclerView);
        //下拉加载更多
        mSwipeRefreshLayout.setOnRefreshListener(this);
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

    private void getHeaderView() {
        mHeaderView = getActivity().getLayoutInflater().inflate(R.layout.alivc_long_video_fragment_home_header_item, null, false);
        mIVBanner = mHeaderView.findViewById(R.id.alivc_iv_banner);
        mTVTitle = mHeaderView.findViewById(R.id.alivc_tv_title);
        mTVDuration = mHeaderView.findViewById(R.id.alivc_tv_duration);
        mQuickAdapter.addHeaderView(mHeaderView);
    }


    /**
     * 设置数据
     */
    private void setData(boolean isRefresh, List<SingleSectionBean> mutableList) {
        mNextRequestPage++;
        int size = mutableList.isEmpty() ? 0 : mutableList.size();
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
     * 加载单集banner图
     */
    private void loadBanner() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_TOKEN, userSpUtils.getUserToken());
        hashMap.put(KEY_PAGE_INDEX, String.valueOf(mNextRequestPage));
        hashMap.put(KEY_PAGE_SIZE, String.valueOf(mPageSize));

        AlivcOkHttpClient.getInstance().get(GET_RECOMMNED_LONG_VIDEOS_LIST, hashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, String result) {
                mSwipeRefreshLayout.setRefreshing(false);
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                SingleBanerBean bannerBean = gson.fromJson(result, SingleBanerBean.class);

                boolean empty = bannerBean.getData().getVideoList().isEmpty();
                if (!empty) {
                    /*目前返回的是数组，取第一个作为banner*/
                    if (mQuickAdapter.getHeaderLayoutCount() == 0) {
                        mQuickAdapter.addHeaderView(mHeaderView);
                    }
                    setBanner(bannerBean.getData().getVideoList().get(0));
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

        AlivcOkHttpClient.getInstance().get(GET_LONG_VIDEOS_LIST, hashMap, new AlivcOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onSuccess(Request request, String result) {
                mSwipeRefreshLayout.setRefreshing(false);
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();

                HomeSingleSetVideoListBean videoListBean = gson.fromJson(result, HomeSingleSetVideoListBean.class);

                List<HomeSingleSetVideoListBean.DataBean.TagVideoListBean> tagVideoListBeans = videoListBean.getData().getTagVideoList();

                sectionBeans = new ArrayList<>();
                if (!tagVideoListBeans.isEmpty()) {
                    //处理数据，当tag不等于空的时候，处理成带section的列表
                    for (HomeSingleSetVideoListBean.DataBean.TagVideoListBean tagVideoListBean : tagVideoListBeans) {
                        sectionBeans.add(new SingleSectionBean(true, tagVideoListBean.getTag()));
                        for (LongVideoBean longVideoBean : tagVideoListBean.getVideoList()) {
                            sectionBeans.add(new SingleSectionBean(longVideoBean));
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
    private void setBanner(final LongVideoBean videoListBean) {
        /*后台返回的是秒，将它变成毫秒*/
        String duration = TimeFormater.formatMs((long) (Double.valueOf(videoListBean.getDuration()) * 1000L));
        String title = videoListBean.getTitle();
        String url = videoListBean.getCoverUrl();
        mTVTitle.setText(title);
        mTVDuration.setText(duration);
        if (getActivity() != null) {
            new ImageLoaderImpl().loadImage(getActivity(), url, new ImageLoaderOptions.Builder()
                    .crossFade()
                    .error(R.mipmap.ic_launcher)
                    .build()).into(mIVBanner);
        }
        mHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlivcPlayerActivity.startAlivcPlayerActivity(getActivity(), videoListBean);
            }
        });

    }


    /**
     * 加载用户信息
     * 因为进入的时候，token可能是为空的，需要确保token存在
     */
    private void loadUser() {
        AlivcOkHttpClient.getInstance().get(GET_USER_INFO, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {

            }

            @Override
            public void onSuccess(Request request, String result) {

                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                RandomUserBean userBean = gson.fromJson(result, RandomUserBean.class);
                if (userSpUtils != null) {
                    userSpUtils.saveUserToken(userBean.getData().getToken());
                    userSpUtils.saveNickName(userBean.getData().getNickName());
                    userSpUtils.saveAvatar(userBean.getData().getAvatarUrl());
                    userSpUtils.saveUserID(userBean.getData().getUserId());
                }
                if (!TextUtils.isEmpty(userBean.getData().getToken())) {
                    onRefresh();
                }
            }
        });
    }

}
