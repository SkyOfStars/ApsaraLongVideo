package com.aliyun.solution.longvideo.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.alivcplayerexpand.util.database.LoadDbDatasListener;
import com.aliyun.player.alivcplayerexpand.util.database.LongVideoDatabaseManager;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadManager;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.activity.AlivcCacheVideoActivity;
import com.aliyun.solution.longvideo.activity.AlivcPlayerActivity;
import com.aliyun.solution.longvideo.activity.AlivcSettingActivity;
import com.aliyun.solution.longvideo.activity.UserInfoActivity;
import com.aliyun.solution.longvideo.adapter.AlivcMineCacheQuickAdapter;
import com.aliyun.solution.longvideo.adapter.AlivcMineHistoryQuickAdapter;
import com.aliyun.solution.longvideo.base.BaseLazyFragment;
import com.aliyun.solution.longvideo.utils.ObjectToLongVideo;
import com.aliyun.solution.longvideo.utils.UserSpUtils;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;

import java.util.List;

/**
 * fragment_我的
 */
public class AlivcMineFragment extends BaseLazyFragment implements View.OnClickListener {

    private RelativeLayout mRlHeader;
    private RecyclerView mHistoryRecyclerView;
    private RecyclerView mCacheRecyclerView;
    private ImageView mIvHeader;
    private TextView mNickName;
    private AlivcMineCacheQuickAdapter mCacheQuickAdapter;
    private AlivcMineHistoryQuickAdapter mHistoryQuickAdapter;
    private AliyunDownloadManager mAliyunDownloadManager;
    private UserSpUtils mUserSpUtils;

    /**
     * 观看历史数据管理类
     */
    private LongVideoDatabaseManager mLongVideoDatabaseManager;

    public static Fragment getInstance() {
        Fragment fragment = new AlivcMineFragment();
        return fragment;
    }

    @Override
    public void onLazyLoad() {
        setData();
    }

    @Override
    public int getContentView() {
        return R.layout.alivc_long_video_fragment_mine;
    }

    @Override
    public void initView(View view) {
        mUserSpUtils = new UserSpUtils.Builder(getActivity()).create();
        mLongVideoDatabaseManager = LongVideoDatabaseManager.getInstance();
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(getContext().getApplicationContext());

        mHistoryRecyclerView = view.findViewById(R.id.alivc_watch_history_recyclerView);
        mCacheRecyclerView = view.findViewById(R.id.alivc_cache_video_recyclerView);
        mRlHeader = view.findViewById(R.id.alivc_rl_header);
        mRlHeader.setOnClickListener(this);
        view.findViewById(R.id.alivc_ll_setting).setOnClickListener(this);
        view.findViewById(R.id.alivc_cache_video_more).setOnClickListener(this);
        mIvHeader = view.findViewById(R.id.alivc_iv_header);
        mNickName = view.findViewById(R.id.alivc_tv_nickname);
        initView();

    }

    @Override
    public void initListener() {

    }

    /**
     * 初始化
     */
    private void initView() {

        //设置空页面，当空页面显示当时候，底部布局要隐藏
        View emptyCacheVideoView = getLayoutInflater().inflate(R.layout.alivc_rv_cache_empty, null);
        View emptyHistoryView = getLayoutInflater().inflate(R.layout.alivc_rv_cache_empty, null);
        TextView tvTitle = emptyHistoryView.findViewById(R.id.alivc_tv_title);
        tvTitle.setText(getResources().getString(R.string.alivc_longvideo_cachevideo_not_watch_history));

        LinearLayoutManager historyLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.HORIZONTAL, false);
        mHistoryQuickAdapter = new AlivcMineHistoryQuickAdapter(R.layout.alivc_rv_mine_history_item, null);
        mHistoryRecyclerView.setLayoutManager(historyLayoutManager);
        mHistoryRecyclerView.setAdapter(mHistoryQuickAdapter);
        mHistoryQuickAdapter.setEmptyView(emptyHistoryView);
        //
        LinearLayoutManager cachelayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.HORIZONTAL, false);
        mCacheQuickAdapter = new AlivcMineCacheQuickAdapter(R.layout.alivc_rv_mine_history_item, null);
        mCacheRecyclerView.setLayoutManager(cachelayoutManager);
        mCacheRecyclerView.setAdapter(mCacheQuickAdapter);
        mCacheQuickAdapter.setEmptyView(emptyCacheVideoView);

        /*观看历史点击事件*/
        mHistoryQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LongVideoBean longVideoBean = (LongVideoBean) adapter.getData().get(position);
                AlivcPlayerActivity.startAlivcPlayerActivity(getContext(), longVideoBean);

            }
        });
        /*缓存视频点击事件*/
        mCacheQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AlivcCacheVideoActivity.startJump(getActivity());

            }
        });

    }

    private void setData() {
        //设置头像
        if (mUserSpUtils.getAvatar() != null && !mUserSpUtils.getAvatar().isEmpty()) {
            new ImageLoaderImpl().loadImage(getContext(), mUserSpUtils.getAvatar(), new ImageLoaderOptions.Builder()
                                            .circle().error(R.mipmap.ic_launcher).build()).into(mIvHeader);
        }
        //设置昵称
        if (mUserSpUtils.getNickName() != null && !mUserSpUtils.getNickName().isEmpty()) {
            mNickName.setText(mUserSpUtils.getNickName());
        }

    }

    /**
     * 获取缓存的视频
     */
    private void searchVideos() {
        // 初始化DownloadManager
        //获取所有的缓存数据
        mAliyunDownloadManager.findDatasByDb(new LoadDbDatasListener() {
            @Override
            public void onLoadSuccess(List<AliyunDownloadMediaInfo> dataList) {
                //查询所有的结果
                mCacheQuickAdapter.setNewData(dataList);

            }
        });
        /*观看历史*/
        List<LongVideoBean> longVideoBeans = mLongVideoDatabaseManager.selectAllWatchHistory();
        mHistoryQuickAdapter.setNewData(longVideoBeans);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        searchVideos();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.alivc_rl_header) {
            UserInfoActivity.startJump(getActivity(), mUserSpUtils.getNickName(), mUserSpUtils.getAvatar());
        } else if (v.getId() == R.id.alivc_ll_setting) {
            AlivcSettingActivity.startJump(getActivity());
        } else if (v.getId() == R.id.alivc_cache_video_more) {
            AlivcCacheVideoActivity.startJump(getActivity());
        }
    }
}
