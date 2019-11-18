package com.aliyun.solution.longvideo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.source.VidSts;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.activity.AlivcPlayerActivity;
import com.aliyun.solution.longvideo.adapter.AlivcVipListQuickAdapter;
import com.aliyun.solution.longvideo.base.BaseLazyFragment;
import com.aliyun.solution.longvideo.bean.LongVideoStsBean;
import com.aliyun.solution.longvideo.bean.VipListBean;
import com.aliyun.solution.longvideo.bean.VipListStsBean;
import com.aliyun.solution.longvideo.utils.UserSpUtils;
import com.aliyun.solution.longvideo.view.Viewplayer.VideoPlayer;
import com.aliyun.solution.longvideo.view.Viewplayer.manager.VideoPlayerManager;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_LONGVIDEO_STS;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_VIP_LIST_BY_TAG;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_PAGE_INDEX;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_PAGE_SIZE;
import static com.aliyun.solution.longvideo.base.GlobalNetConstants.KEY_TOKEN;

/**
 * fragment_vip列表具体播放界面
 */
public class AlivcVipListFragment extends BaseLazyFragment implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView mVipVideosRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AlivcVipListQuickAdapter mQuickAdapter;

    private String mTag;
    private UserSpUtils userSpUtils;

    /**
     * 是否有下页
     */
    private int mNextRequestPage = 0;

    private int mPageSize = 10;

    private VidSts mVidSts;
    /**
     * 如果是刷新为true,如果是加载更多为false
     */
    boolean mIsRefresh;

    public static Fragment getInstance(Bundle bundle) {
        Fragment fragment = new AlivcVipListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mQuickAdapter != null) {
            mQuickAdapter.videoPause();
        }
    }

    /**
     * 如果切换fragment就暂停视频
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser && mQuickAdapter != null) {
            mQuickAdapter.videoPause();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onLazyLoad() {
        loadSts();
    }

    @Override
    public int getContentView() {
        return R.layout.alivc_long_video_fragment_vip_list;
    }

    @Override
    public void initView(View view) {
        mTag = getArguments().getString("tagName");
        userSpUtils = new UserSpUtils.Builder(getContext()).create();

        mSwipeRefreshLayout = view.findViewById(R.id.alivc_Vip_SwipeRefreshLayout);
        mVipVideosRecyclerView = view.findViewById(R.id.alivc_vip_RecyclerView);
    }

    @Override
    public void initListener() {

        View emptyView = getLayoutInflater().inflate(R.layout.alivc_rv_cache_empty, null);
        TextView tvTitle = emptyView.findViewById(R.id.alivc_tv_title);
        tvTitle.setText(getResources().getString(R.string.alivc_longvideo_cachevideo_not_video));

        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mQuickAdapter = new AlivcVipListQuickAdapter(null);
        mQuickAdapter.setEmptyView(emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
        mVipVideosRecyclerView.setHasFixedSize(true);
        mVipVideosRecyclerView.setLayoutManager(linearLayoutManager);
        mVipVideosRecyclerView.setAdapter(mQuickAdapter);

        //如果recyclview释放资源，也释放播放器
        mVipVideosRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                VideoPlayer videoPlayer = ((BaseViewHolder) holder).getView(R.id.alivc_videoplayer);
                if (videoPlayer != null && videoPlayer == VideoPlayerManager.instance().getCurrentVideoPlayer()) {
                    VideoPlayerManager.instance().releaseVideoPlayer();
                }
            }
        });

        mQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                VipListStsBean listBean = (VipListStsBean) adapter.getData().get(position);
                if (view.getId() == R.id.alivc_tv_enter) {
                    //进入详情页面
                    AlivcPlayerActivity.startAlivcPlayerActivity(getContext(), listBean.getVideoListBean());
                }
            }
        });
        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {
        mNextRequestPage = 1;
        mIsRefresh = true;
        //加载完毕
        mQuickAdapter.setEnableLoadMore(false);
        loadVipListByTag();
    }

    /**
     * 设置数据
     */
    private void setData(boolean isRefresh, List<VipListStsBean> mutableList) {
//        mNextRequestPage++;
        int size = mutableList.isEmpty() ? 0 : mutableList.size();

        /*保证第一次进入的时候不满一页不会触发上拉加载*/
        if (mNextRequestPage == 1 && size == mPageSize) {
            mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    mIsRefresh = false;
                    mSwipeRefreshLayout.setEnabled(false);
                    mNextRequestPage++;
                    loadVipListByTag();

                }
            }, mVipVideosRecyclerView);
        }
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
     * 根据对应名称加载列表
     */
    private void loadVipListByTag() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(KEY_TOKEN, userSpUtils.getUserToken());
        hashMap.put(KEY_PAGE_INDEX, String.valueOf(mNextRequestPage));
        hashMap.put(KEY_PAGE_SIZE, String.valueOf(mPageSize));
        hashMap.put("tag", mTag);

        AlivcOkHttpClient.getInstance().get(GET_VIP_LIST_BY_TAG, hashMap, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {

            }

            @Override
            public void onSuccess(Request request, String result) {
                mSwipeRefreshLayout.setRefreshing(false);
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                VipListBean listTypeBean = gson.fromJson(result, VipListBean.class);
                List<VipListStsBean> vipListStsBeans = new ArrayList<>();

                for (LongVideoBean listBean : listTypeBean.getData().getLongVideoList().getVideoList()) {
                    if (mVidSts == null) {
                        vipListStsBeans.add(new VipListStsBean(listBean, new VidSts()));
                    } else {
                        VidSts vidSts = new VidSts();
                        vidSts.setVid(listBean.getVideoId());
                        vidSts.setSecurityToken(mVidSts.getSecurityToken());
                        vidSts.setRegion(mVidSts.getRegion());
                        vidSts.setAccessKeyId(mVidSts.getAccessKeyId());
                        vidSts.setAccessKeySecret(mVidSts.getAccessKeySecret());
                        vipListStsBeans.add(new VipListStsBean(listBean, vidSts));

                    }

                }
                setData(mIsRefresh, vipListStsBeans);
            }
        });
    }

    /**
     * 加载sts信息
     */
    private void loadSts() {
        AlivcOkHttpClient.getInstance().get(GET_LONGVIDEO_STS, new AlivcOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                LongVideoStsBean longVideoStsBean = gson.fromJson(result, LongVideoStsBean.class);

                VidSts vidSts = new VidSts();
                vidSts.setRegion("cn-shanghai");
                vidSts.setAccessKeyId(longVideoStsBean.getData().getAccessKeyId());
                vidSts.setAccessKeySecret(longVideoStsBean.getData().getAccessKeySecret());
                vidSts.setSecurityToken(longVideoStsBean.getData().getSecurityToken());
                mVidSts = vidSts;
                onRefresh();
            }
        });

    }
}
