package com.aliyun.solution.longvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.alivcplayerexpand.util.NetWatchdog;
import com.aliyun.player.alivcplayerexpand.util.database.LoadDbTvListDatasListenerr;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadInfoListener;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadManager;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.alivcplayerexpand.view.quality.QualityItem;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.source.VidSts;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.adapter.AlivcSeriesCacheQuickAdapter;
import com.aliyun.solution.longvideo.bean.LongVideoStsBean;
import com.aliyun.solution.longvideo.utils.ObjectToLongVideo;
import com.aliyun.solution.longvideo.utils.SettingSpUtils;
import com.aliyun.solution.longvideo.view.CacheFileTipsView;
import com.aliyun.svideo.common.base.AlivcListSelectorDialogFragment;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.okhttp.AlivcOkHttpClient;
import com.aliyun.svideo.common.utils.ThreadUtils;
import com.aliyun.svideo.common.utils.ToastUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

import static com.aliyun.solution.longvideo.base.GlobalNetConstants.GET_LONGVIDEO_STS;

/**
 * 剧集缓存
 */
public class AlivcSeriesCacheActivity extends AppCompatActivity implements View.OnClickListener, AliyunDownloadInfoListener {

    private static String mAccessKeyId = "";
    private static String mSecurityToken = "";
    private static String mAccessKeySecret = "";

    /**
     * 全部剧集 RecyclerView
     */
    private RecyclerView mSeriesAllRecyclerView;
    /**
     * 当前清晰度
     */
    private TextView mSeriesCacheCurrentDefinitionTextView;
    /**
     * 清晰度DialogFragment
     */
    private AlivcListSelectorDialogFragment mAlivcListSelectorDialogFragment;

    /**
     * 当前清晰度,当前正在播放的视频集数,sp设置默认清晰度
     */
    private String mCurrentDefinition = "", mCurrentEpisode, mSettingSpUtilsVideoQuantity;
    /**
     * 多选
     */
    private TextView mBaseRightTitleTextView;

    /**
     * 是否是编辑状态
     */
    private boolean mIsEdit = false;
    /**
     * 剧集缓存Adapter
     */
    private AlivcSeriesCacheQuickAdapter mAlivcSeriesCacheQuickAdapter;

    /**
     * 用于保存选中的集数
     */
    private ArrayList<LongVideoBean> mSelectedList = new ArrayList<>();
    /**
     * 用于保存下载的集数
     */
    private ArrayList<LongVideoBean> mDownloadList = new ArrayList<>();
    /**
     * 当前电视剧的所有集数
     */
    private ArrayList<LongVideoBean> mSeriesVideoList;
    /**
     * 当前视频的所有清晰度
     */
    private ArrayList<String> mCurrentVideoVodLists;
    /**
     * 设置sp工具类
     */
    private SettingSpUtils mSettingSpUtils;
    /**
     * sp设置4g下载是否开启
     */
    private boolean mSettingSpUtilsOperatorDownload;
    /**
     * 获取设置的最大同时下载数
     */
    private String mSettingSpUtilsVideoNumber;
    /**
     * 返回
     */
    private FrameLayout mAlivcBaseLeftBackFrameLayout;
    /**
     * 剧集封面url
     */
    private String mTvCoverUrl;

    private AliyunDownloadManager mAliyunDownloadManager;
    private CacheFileTipsView mCacheFileTipsView;

    public static void startAlivcSeriesCacheActivity(Context context, ArrayList<LongVideoBean> mSeriesVideoList,
                                                     String mCurrentEpisode, ArrayList<String> mVodDefinitionList,String tvCoverUrl) {
        Intent intent = new Intent(context, AlivcSeriesCacheActivity.class);
        intent.putExtra(AlivcPlayerActivity.INTENT_SERIES_VIDEOS, mSeriesVideoList);
        intent.putExtra(AlivcPlayerActivity.INTENT_CURRENT_PLAYING_POSITION, mCurrentEpisode);
        intent.putExtra(AlivcPlayerActivity.INTENT_CURRENT_VIDEO_VOD, mVodDefinitionList);
        intent.putExtra(AlivcPlayerActivity.INTENT_SERIES_TV_COVERURL,tvCoverUrl);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_long_video_activity_series_cache);

        mSettingSpUtils = new SettingSpUtils.Builder(this).create();
        mCurrentEpisode = getIntent().getStringExtra(AlivcPlayerActivity.INTENT_CURRENT_PLAYING_POSITION);
        mSeriesVideoList = (ArrayList<LongVideoBean>) getIntent().getSerializableExtra(AlivcPlayerActivity.INTENT_SERIES_VIDEOS);
        mCurrentVideoVodLists = (ArrayList<String>) getIntent().getSerializableExtra(AlivcPlayerActivity.INTENT_CURRENT_VIDEO_VOD);
        mTvCoverUrl = getIntent().getStringExtra(AlivcPlayerActivity.INTENT_SERIES_TV_COVERURL);
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(getApplicationContext());

        initSetting();

        initView();
        initRecyclerView();
        initListener();

        //设置最大下载数
        int num = 1;
        if (TextUtils.isEmpty(mSettingSpUtilsVideoNumber)) {
        } else {
            num = Integer.valueOf(mSettingSpUtilsVideoNumber);
        }
        mAliyunDownloadManager.setMaxNum(num);

        boolean is4gConnected = NetWatchdog.is4GConnected(getApplicationContext());
        if(is4gConnected){
            ToastUtils.show(this,getString(R.string.alivc_longvideo_doawload_operator));
        }
    }

    /**
     * 初始化设置界面的信息
     */
    private void initSetting() {
        mSettingSpUtilsVideoQuantity = mSettingSpUtils.getVideoQuantity();
        mSettingSpUtilsOperatorDownload = mSettingSpUtils.getOperatorDownload();
        mSettingSpUtilsVideoNumber = mSettingSpUtils.getVideoNumber();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        if (mCacheFileTipsView != null) {
            mCacheFileTipsView.setSelectedList(mSelectedList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAliyunDownloadManager != null){
            mAliyunDownloadManager.removeDownloadInfoListener(this);
        }
    }

    private void initView() {
        mSeriesAllRecyclerView = findViewById(R.id.recyclerview_series_all);
        mAlivcBaseLeftBackFrameLayout = findViewById(R.id.alivc_base_fl_left_back);

        TextView mBaseMiddleTitleTextView = findViewById(R.id.alivc_base_tv_middle_title);
        mBaseMiddleTitleTextView.setText(R.string.alivc_longvideo_series_cache_title);
        mBaseRightTitleTextView = findViewById(R.id.alivc_base_tv_right_edit);
        mBaseRightTitleTextView.setText(R.string.alivc_longvideo_series_cache_title_multiple_selection);

        //设置默认显示的清晰度
        mSeriesCacheCurrentDefinitionTextView = findViewById(R.id.tv_series_cache_current_definition);
        if (mCurrentVideoVodLists != null && mCurrentVideoVodLists.size() > 0) {
            int i = 0;
            for (i = 0; i < mCurrentVideoVodLists.size(); i++) {
                if (mCurrentVideoVodLists.get(i).equals(mSettingSpUtilsVideoQuantity)) {
                    mCurrentDefinition = mCurrentVideoVodLists.get(i);
                    break;
                }
            }
            if (i == mCurrentVideoVodLists.size()) {
                mCurrentDefinition = mCurrentVideoVodLists.get(0);
            }
        }
        mSeriesCacheCurrentDefinitionTextView.setText(QualityItem.getItem(this, mCurrentDefinition, false).getName());

        mCacheFileTipsView = findViewById(R.id.cache_file_tips_view);

        //数据源
        ArrayList<String> selectors = new ArrayList<>();

        for (String vod : mCurrentVideoVodLists) {
            selectors.add(QualityItem.getItem(this, vod, false).getName());
        }

        mAlivcListSelectorDialogFragment = new AlivcListSelectorDialogFragment.Builder(getSupportFragmentManager())
                .setGravity(Gravity.BOTTOM)
                .setCancelableOutside(true)
                .setItemColor(ContextCompat.getColor(this, R.color.alivc_common_font_red_wine))
                .setUnItemColor(ContextCompat.getColor(this, R.color.alivc_common_font_black))
                .setNewData(selectors)
                .setDialogAnimationRes(R.style.Dialog_Animation)
                .setOnListItemSelectedListener(new AlivcListSelectorDialogFragment.OnListItemSelectedListener() {
                    @Override
                    public void onClick(String position) {
                        mCurrentDefinition = position;
                        mAlivcListSelectorDialogFragment.setPosition(position);
                        mSeriesCacheCurrentDefinitionTextView.setText(position);
                    }
                })
                .create();

    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        mSeriesAllRecyclerView.setLayoutManager(gridLayoutManager);

        mAlivcSeriesCacheQuickAdapter = new AlivcSeriesCacheQuickAdapter(R.layout.alivc_long_video_item_series_cache, mCurrentEpisode);
        mSeriesAllRecyclerView.setAdapter(mAlivcSeriesCacheQuickAdapter);
    }

    private void initListener() {
        mBaseRightTitleTextView.setOnClickListener(this);
        mAlivcBaseLeftBackFrameLayout.setOnClickListener(this);
        mSeriesCacheCurrentDefinitionTextView.setOnClickListener(this);

        mAlivcSeriesCacheQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LongVideoBean itemBean = (LongVideoBean) adapter.getData().get(position);
                boolean is4gConnected = NetWatchdog.is4GConnected(AlivcSeriesCacheActivity.this);
                if (is4gConnected && !mSettingSpUtilsOperatorDownload) {
                    ToastUtils.show(AlivcSeriesCacheActivity.this,getString(R.string.alivc_longvideo_cache_toast_4g));
                    return;
                }
                if (itemBean.isDownloaded() || itemBean.isDownloading()) {
                    return;
                }
                onCacheItemClick(itemBean);
            }
        });

        mCacheFileTipsView.setOnCacheViewGroupClick(new CacheFileTipsView.OnCacheViewGroupClickListener() {
            @Override
            public void onCacheViewGroupClick(boolean isEdit) {
                if (isEdit) {
                    VidSts vidSts = initVidSts();
                    if (mSelectedList != null && mSelectedList.size() > 0) {
                        for (LongVideoBean longVideoBean : mSelectedList) {
                            vidSts.setVid(longVideoBean.getVideoId());
                            AliyunDownloadMediaInfo aliyunDownloadMediaInfo = ObjectToLongVideo.longVideoBeanToAliyunDownloadMediaInfo(longVideoBean, vidSts, mSettingSpUtilsVideoQuantity, "mp4");
                            Log.i("scar", "onCacheViewGroupClick: " + aliyunDownloadMediaInfo.getTvId() + aliyunDownloadMediaInfo.getVid());
                            aliyunDownloadMediaInfo.setTvCoverUrl(mTvCoverUrl);
                            mAliyunDownloadManager.insertDb(aliyunDownloadMediaInfo);
                            longVideoBean.setSelected(false);
                            longVideoBean.setDownloading(true);
                        }
                        mSelectedList.clear();
                        AlivcCacheVideoActivity.startJump(AlivcSeriesCacheActivity.this);
                    }
                } else {
                    AlivcCacheVideoActivity.startJump(AlivcSeriesCacheActivity.this);
                }
            }
        });

        mAliyunDownloadManager.addDownloadInfoListener(this);
    }

    private void initData() {
        if (mSeriesVideoList != null && mSeriesVideoList.size() > 0) {
            mAliyunDownloadManager.getDownloadMediaInfoWithTvId(mSeriesVideoList.get(0).getTvId(), new LoadDbTvListDatasListenerr() {
                @Override
                public void onLoadTvListSuccess(final List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos) {
                    ThreadUtils.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mDownloadList != null){
                                mDownloadList.clear();
                            }
                            //从数据库恢复数据,如果有数据,则根据数据库中存储的数据,和当前剧集进行对比,并标志是下载中还是下载完成状态
                            if(aliyunDownloadMediaInfos.size() > 0){
                                for (AliyunDownloadMediaInfo aliyunDownloadMediaInfo : aliyunDownloadMediaInfos) {
                                    String savePath = aliyunDownloadMediaInfo.getSavePath();
                                    if (!TextUtils.isEmpty(savePath)) {
                                        File file = new File(savePath);
                                        if (file.exists() && file.isFile()) {
                                            Log.e("longVideo ", "local video exist " + savePath);
                                        } else {
                                            //视频不存在,可能是手动删除了本地视频,需要删除数据库
                                            if (aliyunDownloadMediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Complete) {
                                                mAliyunDownloadManager.deleteFile(aliyunDownloadMediaInfo);
                                                continue;
                                            }
                                        }
                                    }
                                    for (LongVideoBean longVideoBean : mSeriesVideoList) {
                                        if (longVideoBean.getVideoId().equals(aliyunDownloadMediaInfo.getVid())
                                                && longVideoBean.getTvId().equals(aliyunDownloadMediaInfo.getTvId())) {
                                            AliyunDownloadMediaInfo.Status status = aliyunDownloadMediaInfo.getStatus();
                                            longVideoBean.setDownloading(status == AliyunDownloadMediaInfo.Status.Start || status == AliyunDownloadMediaInfo.Status.Stop);
                                            longVideoBean.setDownloaded(status == AliyunDownloadMediaInfo.Status.Complete);
                                            if (longVideoBean.isDownloaded() || longVideoBean.isDownloading()) {
                                                mDownloadList.add(longVideoBean);
                                            }
                                        }
                                    }
                                }
                            }else{
                                //如果数据库中没有数据,则将剧集中的下载中或下载完成的状态置位false
                                for (LongVideoBean longVideoBean : mSeriesVideoList) {
                                    longVideoBean.setDownloaded(false);
                                    longVideoBean.setDownloading(false);
                                }
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCacheFileTipsView.setDownloadList(mDownloadList);
                                    mAlivcSeriesCacheQuickAdapter.setNewData(mSeriesVideoList);
                                    mAlivcSeriesCacheQuickAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (mSeriesCacheCurrentDefinitionTextView == v) {
            //选择清晰度
            mAlivcListSelectorDialogFragment.show();
            if (TextUtils.isEmpty(mCurrentDefinition)) {
                mCurrentDefinition = QualityItem.getItem(this, getResources().getString(R.string.alivc_longvideo_quality_sd), false).getName();
            }
            mAlivcListSelectorDialogFragment.setPosition(QualityItem.getItem(this, mCurrentDefinition, false).getName());

        } else if (mBaseRightTitleTextView == v) {
            //多选
            mIsEdit = !mIsEdit;
            mBaseRightTitleTextView.setText(mIsEdit ? R.string.alivc_common_cancel : R.string.alivc_longvideo_series_cache_title_multiple_selection);

            mCacheFileTipsView.setEdit(mIsEdit);

            if (mAlivcSeriesCacheQuickAdapter != null) {
                mAlivcSeriesCacheQuickAdapter.isEdit(mIsEdit);
                mAlivcSeriesCacheQuickAdapter.notifyDataSetChanged();
            }
        } else if (v.getId() == R.id.alivc_base_fl_left_back) {
            //返回
            finish();
        }
    }

    /**
     * 缓存item点击事件
     */
    private void onCacheItemClick(LongVideoBean itemBean) {
        if (mIsEdit) {
            itemBean.setSelected(!itemBean.isSelected());
        } else {
            startDownload(itemBean);
        }
        refreshBottomMessage(itemBean);
        mAlivcSeriesCacheQuickAdapter.notifyDataSetChanged();
    }

    private void startDownload(LongVideoBean itemBean) {
        if(TextUtils.isEmpty(mAccessKeyId) || TextUtils.isEmpty(mSecurityToken) || TextUtils.isEmpty(mAccessKeySecret)){
            requestSts(itemBean);
        }else{
            VidSts vidSts = initVidSts();
            vidSts.setVid(itemBean.getVideoId());
            itemBean.setTvCoverUrl(mTvCoverUrl);
            mAliyunDownloadManager.prepareDownloadByLongVideoBean(vidSts, itemBean);
        }
    }

    /**
     * 刷新底部当前缓存的相关信息
     */
    private void refreshBottomMessage(LongVideoBean itemBean) {
        if (mSelectedList == null) {
            return;
        }
        if (mIsEdit) {
            //编辑状态
            if (itemBean.isSelected() && !mSelectedList.contains(itemBean)) {
                mSelectedList.add(itemBean);
            }
            if (!itemBean.isSelected() && mSelectedList.contains(itemBean)) {
                mSelectedList.remove(itemBean);
            }

            mCacheFileTipsView.setSelectedList(mSelectedList);
        }
    }

    private void requestSts(final LongVideoBean itemBean) {
        AlivcOkHttpClient.getInstance().get(GET_LONGVIDEO_STS, new AlivcOkHttpClient.HttpCallBack() {

            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.show(AlivcSeriesCacheActivity.this, e.getMessage());
            }

            @Override
            public void onSuccess(Request request, String result) {
                Gson gson = new Gson();
                LongVideoStsBean longVideoStsBean = gson.fromJson(result, LongVideoStsBean.class);
                LongVideoStsBean.DataBean stsInfoBean = longVideoStsBean.getData();
                mAccessKeyId = stsInfoBean.getAccessKeyId();
                mSecurityToken = stsInfoBean.getSecurityToken();
                mAccessKeySecret = stsInfoBean.getAccessKeySecret();
                VidSts vidSts = initVidSts();
                if (itemBean != null) {
                    vidSts.setVid(itemBean.getVideoId());
                    itemBean.setTvCoverUrl(mTvCoverUrl);
                    mAliyunDownloadManager.prepareDownloadByLongVideoBean(vidSts, itemBean);
                }
            }
        });
    }


    private LongVideoBean findLongVideoBeanByVideoId(String videoId) {
        if (mSeriesVideoList == null) {
            return null;
        }
        for (LongVideoBean longVideoBean : mSeriesVideoList) {
            if (longVideoBean.getVideoId().equals(videoId)) {
                return longVideoBean;
            }
        }
        return null;
    }

    /**
     * init VidSts without vid
     */
    private VidSts initVidSts() {
        VidSts vidSts = new VidSts();
        vidSts.setAccessKeyId(mAccessKeyId);
        vidSts.setSecurityToken(mSecurityToken);
        vidSts.setAccessKeySecret(mAccessKeySecret);
        return vidSts;
    }

    private void refreshDownloadBottom(ArrayList<LongVideoBean> bottomList) {
        if (mCacheFileTipsView != null) {
            mCacheFileTipsView.setDownloadList(bottomList);
        }
    }

    /**
     * 修改LongVideoBean 的下载状态
     */
    private void changeLongVideoBeanState(String vid, boolean isDownloading, boolean isDownloaded, boolean isSelected) {
        LongVideoBean longVideoBeanByVideoId = findLongVideoBeanByVideoId(vid);
        if (longVideoBeanByVideoId != null) {
            if (mDownloadList != null && !mDownloadList.contains(longVideoBeanByVideoId)) {
                mDownloadList.add(longVideoBeanByVideoId);
                refreshDownloadBottom(mDownloadList);
            }
            longVideoBeanByVideoId.setDownloading(isDownloading);
            longVideoBeanByVideoId.setDownloaded(isDownloaded);
            longVideoBeanByVideoId.setSelected(isSelected);
            int index = mSeriesVideoList.indexOf(longVideoBeanByVideoId);
            mAlivcSeriesCacheQuickAdapter.notifyItemChanged(index);
            refreshBottomMessage(longVideoBeanByVideoId);
        }
    }

    /**
     * ------------------------------------------下载回调 ------------------------------------------
     */
    @Override
    public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
        int i = 0;
        for (i = 0; i < infos.size(); i++) {
            if (QualityItem.getItem(AlivcSeriesCacheActivity.this, infos.get(i).getQuality(), false).getName()
                    .equals(QualityItem.getItem(AlivcSeriesCacheActivity.this, mCurrentDefinition, false).getName())) {
                /* 编辑状态需要跳转到下载界面,不是编辑状态直接开始下载 */
                if (!mIsEdit) {
                    mAliyunDownloadManager.startDownload(infos.get(i));
                }
                break;
            }
        }
        //如果没有默认的清晰度,则选择第一个清晰度开始下载
        if (i == infos.size()) {
            if (!mIsEdit) {
                mAliyunDownloadManager.startDownload(infos.get(0));
            }
        }
    }

    @Override
    public void onAdd(AliyunDownloadMediaInfo info) {

    }

    @Override
    public void onStart(AliyunDownloadMediaInfo info) {
        changeLongVideoBeanState(info.getVid(), true, false, false);
    }

    @Override
    public void onProgress(AliyunDownloadMediaInfo info, int percent) {
    }

    @Override
    public void onStop(AliyunDownloadMediaInfo info) {

    }

    @Override
    public void onCompletion(final AliyunDownloadMediaInfo info) {
        changeLongVideoBeanState(info.getVid(), false, true, false);
    }

    @Override
    public void onError(AliyunDownloadMediaInfo info, ErrorCode code, String msg, String requestId) {
        ToastUtils.show(this, code.getValue() + " --- " + msg);
        changeLongVideoBeanState(info.getVid(), false, false, false);
    }

    @Override
    public void onWait(AliyunDownloadMediaInfo outMediaInfo) {

    }

    @Override
    public void onDelete(AliyunDownloadMediaInfo info) {
        changeLongVideoBeanState(info.getVid(), false, false, false);
    }

    @Override
    public void onDeleteAll() {

    }

    @Override
    public void onFileProgress(AliyunDownloadMediaInfo info) {

    }
    /**
     * ------------------------------------------下载回调 ------------------------------------------
     */
}
