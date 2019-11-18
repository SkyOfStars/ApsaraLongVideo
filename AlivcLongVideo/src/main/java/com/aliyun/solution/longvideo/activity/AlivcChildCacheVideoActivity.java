package com.aliyun.solution.longvideo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.alivcplayerexpand.constants.PlayParameter;
import com.aliyun.player.alivcplayerexpand.util.FixedToastUtils;
import com.aliyun.player.alivcplayerexpand.util.Formatter;
import com.aliyun.player.alivcplayerexpand.util.NetWatchdog;
import com.aliyun.player.alivcplayerexpand.util.VidStsUtil;
import com.aliyun.player.alivcplayerexpand.util.database.LoadDbTvListDatasListenerr;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadInfoListener;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadManager;
import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.player.alivcplayerexpand.util.download.StorageUtil;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.source.VidSts;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.adapter.AlivcChildCacheVideoDownloadAdapter;
import com.aliyun.solution.longvideo.utils.ObjectToLongVideo;
import com.aliyun.solution.longvideo.utils.SettingSpUtils;
import com.aliyun.svideo.common.utils.PermissionUtils;
import com.aliyun.svideo.common.utils.ThreadUtils;
import com.aliyun.svideo.common.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频缓存页_子类
 */
public class AlivcChildCacheVideoActivity extends AppCompatActivity implements View.OnClickListener, AliyunDownloadInfoListener {

    private final String TAG = AlivcChildCacheVideoActivity.class.getSimpleName();
    public static final int PERMISSION_REQUEST_CODE = 1000;

    public static final String KEY_ALIVC_CACHE_TV_ID = "alivc_cache_video_tv_id";

    private RecyclerView mCacheVideoRecyclerView;
    private TextView mTvRight;
    private TextView mTvShowHide;
    /**
     * 底部显示缓存父布局
     */
    private FrameLayout mFlCacheBottom;
    /**
     * 底部显示状态父布局
     */
    private LinearLayout mLlStatusBottom;

    /**
     * 显示缓存占用进度条
     */
    private ProgressBar mCacheProgressBar;
    /**
     * 显示缓存数值
     */
    private TextView mTvCacheSize;
    /**
     * 删除
     */
    private TextView mTvDelete;

    /**
     * 全选
     */
    private TextView mTvAllSelected;
    /**
     * 编辑状态下，隐藏底部缓存条，显示底部编辑栏
     */
    private boolean mIsEditing = false;
    /**
     * 是否全选
     */
    private boolean mIsAllSelected = true;
    /**
     * 下载数据列表
     */
    private List<AliyunDownloadMediaInfo> mDownloadMediaInfos = new ArrayList<>();

    private AliyunDownloadManager mAliyunDownloadManager;


    /**
     * 当前网络状态
     * true ,wifi,
     * false 4g
     */
    private boolean mIsNetWorkconnect = true;
    /**
     * 权限申请
     */
    String[] permission = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * tvId,只有系列的时候采用tvId
     */
    private String mTvId;

    private SettingSpUtils spUtils;
    /**
     * 网络状态监听
     */
    private NetWatchdog mNetWatchdog;

    /**
     * 是否可以运营商下载
     */
    private boolean mIsOperatorDownld = false;

    /**
     * 下载Adapter
     */
    private AlivcChildCacheVideoDownloadAdapter mPlayerDownloadAdapter;

    private boolean mCheckResult;
    /**
     * 已观看视频计数
     */
    private int mHasWatchedNumber = 0;


    /**
     * 初始化网络监听
     */
    private void initNetWatchdog() {
        mNetWatchdog = new NetWatchdog(this);
        mNetWatchdog.setNetChangeListener(new MyNetChangeListener(this));
        mNetWatchdog.setNetConnectedListener(new MyNetConnectedListener(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_long_video_activity_cache_video);
        mTvId = getIntent().getStringExtra(KEY_ALIVC_CACHE_TV_ID);
        spUtils = new SettingSpUtils.Builder(this).create();
        mIsOperatorDownld = spUtils.getOperatorDownload();
        mCheckResult = PermissionUtils.checkPermissionsGroup(this, permission);
        mAliyunDownloadManager = AliyunDownloadManager.getInstance(getApplicationContext());
        if (!mCheckResult) {
            PermissionUtils.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
        }else{
            loadDb(true);
        }
        //初始化网络监听器

        setTitle();
        initView();
        initNetWatchdog();
    }

    /**
     * 获取sts信息
     */
    private void getVidSts(final List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos) {
        VidStsUtil.getVidSts(PlayParameter.PLAY_PARAM_VID, new VidStsUtil.OnStsResultListener() {
            @Override
            public void onSuccess(String vid, String akid, String akSecret, String token) {
                VidSts mVidSts = new VidSts();
                mVidSts.setRegion("cn-shanghai");
                mVidSts.setAccessKeyId(akid);
                mVidSts.setSecurityToken(token);
                mVidSts.setAccessKeySecret(akSecret);

                for (AliyunDownloadMediaInfo mediaInfo : aliyunDownloadMediaInfos) {
                    mVidSts.setVid(mediaInfo.getVid());
                    mediaInfo.setVidSts(mVidSts);
                    /*如果它不是完成状态的时候才开始prepared*/
                    if (mediaInfo.getStatus() != AliyunDownloadMediaInfo.Status.Complete && mediaInfo.getStatus() != AliyunDownloadMediaInfo.Status.Start) {
                        if (!TextUtils.isEmpty(mediaInfo.getTvId())) {
                            LongVideoBean longVideoBean = new LongVideoBean();
                            longVideoBean.setTvId(mediaInfo.getTvId());
                            Log.i("scar", "子类开始下载: " + mediaInfo.getTitle() + "status : " + mediaInfo.getStatus() + " quality " + mediaInfo.getQuality() + "__" + mediaInfo.getProgress());

                            mAliyunDownloadManager.prepareDownloadByLongVideoBean(mVidSts, longVideoBean);

                        }
                    }
                }

            }

            @Override
            public void onFail() {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alivc_player_get_sts_failed), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mNetWatchdog != null) {
            mNetWatchdog.startWatch();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAliyunDownloadManager != null) {
            mAliyunDownloadManager.addDownloadInfoListener(this);
        }
        if(mPlayerDownloadAdapter != null){
            mPlayerDownloadAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAliyunDownloadManager != null) {
            mAliyunDownloadManager.removeDownloadInfoListener(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("scar", "onDestroy: ");
        mNetWatchdog.stopWatch();
        //  mAliyunDownloadManager.clearList();


    }

    private void initView() {
        mFlCacheBottom = findViewById(R.id.alivc_fl_cache_bottom);
        mLlStatusBottom = findViewById(R.id.alivc_fl_edit_bottom);
        mCacheProgressBar = findViewById(R.id.alivc_progress_bar_healthy);
        mTvCacheSize = findViewById(R.id.alivc_tv_cache_size);
        mTvDelete = findViewById(R.id.alivc_tv_delete);
        mTvAllSelected = findViewById(R.id.alivc_tv_all_selected);
        mTvShowHide = findViewById(R.id.alivc_not_cache_video);
        mTvAllSelected.setOnClickListener(this);
        mTvDelete.setOnClickListener(this);

        mCacheVideoRecyclerView = findViewById(R.id.alivc_cache_video_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, OrientationHelper.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.alivc_longvideo_rv_cachevideo_divider_vertical));
        mPlayerDownloadAdapter = new AlivcChildCacheVideoDownloadAdapter(AlivcChildCacheVideoActivity.this);
        mCacheVideoRecyclerView.setLayoutManager(linearLayoutManager);
        mCacheVideoRecyclerView.addItemDecoration(dividerItemDecoration);
        //关掉默认动画
        ((DefaultItemAnimator) mCacheVideoRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mCacheVideoRecyclerView.setAdapter(mPlayerDownloadAdapter);


        initData();
        startDownLoad();
    }

    /**
     * reset：自启，查询所有对应item的tvid的视频列表
     * download：查询所有视频列表
     */
    private void startDownLoad() {
        //同时下载数量
        String number = spUtils.getVideoNumber();
        int num = 1;
        if (number == null || number.isEmpty()) {
        } else {
            num = Integer.valueOf(spUtils.getVideoNumber());

        }
        mAliyunDownloadManager.setMaxNum(num);

    }

    /**
     * 保证没有网络的时候也能调用这个数据
     * isDownload 开始下载数据
     */
    private void loadDb(final boolean isDownload) {

        if (!TextUtils.isEmpty(mTvId)) {
            mAliyunDownloadManager.getDownloadMediaInfoWithTvId(mTvId, new LoadDbTvListDatasListenerr() {
                @Override
                public void onLoadTvListSuccess(List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos) {
                    showHideTv(aliyunDownloadMediaInfos);
                    mDownloadMediaInfos.clear();
                    mDownloadMediaInfos.addAll(aliyunDownloadMediaInfos);

                    if (mDownloadMediaInfos.isEmpty()) {
                        mTvRight.setEnabled(false);
                    } else {
                        mTvRight.setEnabled(true);
                    }
                    mPlayerDownloadAdapter.setData(mDownloadMediaInfos);
                    if (isDownload) {
                        getVidSts(mDownloadMediaInfos);
                    }

                    calculationTotal();
                    calculationCache();


                }
            });
        }
    }


    /**
     * 删除数据刷新界面
     */
    private void reFreshData(AliyunDownloadMediaInfo mediaInfo) {
        mPlayerDownloadAdapter.deleteData(mediaInfo);
        List<AliyunDownloadMediaInfo> datas = mPlayerDownloadAdapter.getDatas();
        showHideTv(datas);
        //  如果列表为空，那么重置为编辑，并且不可点击，隐藏底部导航栏
        if (datas.isEmpty()) {
            mTvRight.setText(getResources().getString(R.string.alivc_longvideo_cache_video_edit));
            mTvRight.setEnabled(false);
            statusAndCacheVisibility(View.GONE, View.VISIBLE);
        } else {
            mTvRight.setEnabled(true);
        }
        calculationTotal();
        calculationCache();
    }

    private void initData() {

        //列表点击条目，编辑状态下不可以跳转到播放界面
        mPlayerDownloadAdapter.setOnItemClickListener(new AlivcChildCacheVideoDownloadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AlivcChildCacheVideoDownloadAdapter adapter, View view, int position) {
                AliyunDownloadMediaInfo mediaInfo = adapter.getDatas().get(position);
                if (mIsEditing) {
                    //设置选中状态
                    mediaInfo.setSelected(!mediaInfo.isSelected());

                    //如果有单个未选中的话，则取消全选状态
                    for (int i = 0; i < adapter.getDatas().size(); i++) {
                        if (!adapter.getDatas().get(i).isSelected()) {
                            mIsAllSelected = false;
                            mTvAllSelected.setText(getResources().getString(R.string.alivc_longvideo_cachevideo_all_selected));
                        }
                    }

                    adapter.notifyItemChanged(position, 1);
                    calculationTotal();
                }
            }
        });
        //预览图点击
        mPlayerDownloadAdapter.setOnItemChildClickListener(new AlivcChildCacheVideoDownloadAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(AlivcChildCacheVideoDownloadAdapter adapter, View view, int position) {
                AliyunDownloadMediaInfo mediaInfo = mDownloadMediaInfos.get(position);

                if (view.getId() == R.id.alivc_iv_preview) {
                    AlivcPlayerActivity.startAlivcPlayerActivity(AlivcChildCacheVideoActivity.this, ObjectToLongVideo.downloadMediaInfoToLongVideo(mediaInfo));
                    //设置已观看
                    if (mediaInfo.getWatched() == 0) {
                        mediaInfo.setWatched(1);
                        mAliyunDownloadManager.updateDb(mediaInfo);
                        mHasWatchedNumber++;
                    }
                } else if (view.getId() == R.id.alivc_fl_font) {

                    if (!mIsOperatorDownld && !mIsNetWorkconnect) {
                        FixedToastUtils.show(AlivcChildCacheVideoActivity.this, getResources().getString(R.string.alivc_longvideo_cache_toast_4g));
                        return;
                    }
                    if (mediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Prepare || mediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Stop || mediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Error || mediaInfo.getStatus() == AliyunDownloadMediaInfo.Status.Wait) {
                        if (mCheckResult) {
                            mAliyunDownloadManager.startDownload(mediaInfo);
                        } else {
                            PermissionUtils.requestPermissions(AlivcChildCacheVideoActivity.this, permission, PERMISSION_REQUEST_CODE);
                        }
                    } else {
                        mAliyunDownloadManager.stopDownload(mediaInfo);
                    }
                }
            }
        });

    }

    /**
     * 计算选中的数量
     */
    private void calculationTotal() {
        int totalNumber = 0;
        if (mIsEditing) {

            for (int i = 0; i < mPlayerDownloadAdapter.getDatas().size(); i++) {
                //如果是选中状态则计算数量
                if (mPlayerDownloadAdapter.getDatas().get(i).isSelected()) {
                    totalNumber += mPlayerDownloadAdapter.getDatas().get(i).getNumber();
                }
            }

            if (totalNumber == 0) {
                mTvDelete.setTextColor(ContextCompat.getColor(AlivcChildCacheVideoActivity.this, R.color.alivc_common_font_gray_333333));
                mTvDelete.setText(getResources().getString(R.string.alivc_longvideo_cachevideo_delete));
            } else {
                mTvDelete.setTextColor(ContextCompat.getColor(AlivcChildCacheVideoActivity.this, R.color.alivc_common_bg_red_darker));
                StringBuilder builder = new StringBuilder();
                builder.append(getResources().getString(R.string.alivc_longvideo_cachevideo_delete))
                .append("(")
                .append(totalNumber)
                .append(")");
                mTvDelete.setText(builder);
            }

        }
    }

    /**
     * 计算缓存和可用空间
     */
    private void calculationCache() {
        long cacheSize = 0;

        ArrayList<AliyunDownloadMediaInfo> aliyunDownloadMediaInfos = (ArrayList<AliyunDownloadMediaInfo>) mPlayerDownloadAdapter.getDatas();
        for (AliyunDownloadMediaInfo mediaInfo : aliyunDownloadMediaInfos) {
            cacheSize += mediaInfo.getSize();

        }
        //缓存大小和占用空间大小
        String intentUseStorage = Formatter.getFileSizeDescription(cacheSize);
        long size = StorageUtil.getAvailableExternalMemorySize();
        //   Log.i("scar", "calculationCache: " + Formatter.getFileSizeDescription(size * 1024L));
        mTvCacheSize.setText(String.format(getResources().getString(R.string.alivc_longvideo_video_cache_storage_tips), intentUseStorage, Formatter.getFileSizeDescription(size * 1024L)));
        //占据总容量的百分比
        int newSize = (int) ((cacheSize / 1024.0 / size) * 100);

        mCacheProgressBar.setProgress(newSize);
    }

    /**
     * 全选还是反选
     */
    private void allSelectedOrUnSelected() {
        ArrayList<AliyunDownloadMediaInfo> downloadMediaInfos = (ArrayList<AliyunDownloadMediaInfo>) mPlayerDownloadAdapter.getDatas();

        for (int i = 0; i < downloadMediaInfos.size(); i++) {
            if (mIsAllSelected) {
                downloadMediaInfos.get(i).setSelected(true);
            } else {
                downloadMediaInfos.get(i).setSelected(false);
            }
        }
        mPlayerDownloadAdapter.notifyDataSetChanged();
        calculationTotal();
    }

    /**
     * 全选反选文本,状态
     */
    private void allSelectedStatus() {
        if (mIsAllSelected) {
            mIsAllSelected = false;
            mTvAllSelected.setText(getResources().getString(R.string.alivc_longvideo_cachevideo_all_selected));

        } else {
            mIsAllSelected = true;
            mTvAllSelected.setText(getResources().getString(R.string.alivc_longvideo_cachevideo_un_all_selected));
        }
    }

    /**
     * 删除数据
     */
    private void deleteItem() {

        //要删除本剧集所有
        for (int i = 0; i < mDownloadMediaInfos.size(); i++) {
            if (mDownloadMediaInfos.get(i).isSelected()) {
                //剧集,如果是第二层页面，可以删除单个视频
                mAliyunDownloadManager.deleteFile(mDownloadMediaInfos.get(i));
            }

        }
    }


    /**
     * setting common title
     */
    private void setTitle() {
        TextView tvTitle = findViewById(R.id.alivc_base_tv_middle_title);
        FrameLayout mFlLeftBack = findViewById(R.id.alivc_base_fl_left_back);
        mTvRight = findViewById(R.id.alivc_base_tv_right_edit);
        mTvRight.setOnClickListener(this);
        mFlLeftBack.setOnClickListener(this);
        tvTitle.setText(getResources().getString(R.string.alivc_longvideo_cache_video_title));
        mTvRight.setText(getResources().getString(R.string.alivc_longvideo_cache_video_edit));

    }

    /**
     * 视频缓存子类入口
     */
    public static void startJump(Context context, String tvId) {
        Intent intent = new Intent(context, AlivcChildCacheVideoActivity.class);
        intent.putExtra(KEY_ALIVC_CACHE_TV_ID, tvId);
        context.startActivity(intent);
    }
    /**
     * 底部布局的显示隐藏
     */
    private void statusAndCacheVisibility(int gone, int visible) {
        mLlStatusBottom.setVisibility(gone);
        mFlCacheBottom.setVisibility(visible);
    }
    /**
     * 提示布局显示隐藏
     */
    private void showHideTv(List<AliyunDownloadMediaInfo> mDownloadMediaInfos) {
        if (mDownloadMediaInfos == null || mDownloadMediaInfos.isEmpty()) {
            mTvShowHide.setVisibility(View.VISIBLE);
        } else {
            mTvShowHide.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        //编辑 取消
        if (v.getId() == R.id.alivc_base_tv_right_edit) {
            if (mIsEditing) {
                mIsEditing = false;
                mTvRight.setText(getResources().getString(R.string.alivc_longvideo_cache_video_edit));
                statusAndCacheVisibility(View.GONE, View.VISIBLE);
            } else {
                mIsEditing = true;
                mTvRight.setText(getResources().getString(R.string.alivc_common_cancel));
                statusAndCacheVisibility(View.VISIBLE, View.GONE);
            }
            mPlayerDownloadAdapter.setEditing(mIsEditing);
            //全选
        } else if (v.getId() == R.id.alivc_tv_all_selected) {
            allSelectedStatus();
            allSelectedOrUnSelected();
            //删除
        } else if (v.getId() == R.id.alivc_tv_delete) {
            deleteItem();
        } else if (v.getId() == R.id.alivc_base_fl_left_back) {
            Intent intent = new Intent();
            intent.putExtra(AlivcCacheVideoActivity.RESULT_WATCHED_NUMBER,mHasWatchedNumber);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(AlivcCacheVideoActivity.RESULT_WATCHED_NUMBER,mHasWatchedNumber);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }


    /**
     * 下载监听回调
     * 如果切换到4g，暂停之前直接prepared之后start的下载
     * 如果已经prepared的不需要在
     */
    @Override
    public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
        boolean isHit = true;
        outerLoop:
        for (int i = 0; i < infos.size(); i++) {
            for (int j = 0; j < mDownloadMediaInfos.size(); j++) {

                //同一个视频，直接下载
                if (infos.get(i).getVid().equals(mDownloadMediaInfos.get(j).getVid()) && infos.get(i).getQuality().equals(mDownloadMediaInfos.get(j).getQuality())) {
                    mDownloadMediaInfos.get(j).setTrackInfo(infos.get(i).getTrackInfo());
                    mDownloadMediaInfos.get(j).setQualityIndex(infos.get(i).getQualityIndex());
                    mAliyunDownloadManager.startDownload(infos.get(i));
                    isHit = false;
                    break outerLoop; // 跳出外层循环
                }

            }
        }
        //如果没有默认的清晰度,则选择第一个清晰度开始下载
        //删除原本的数据，插入新数据
        if (isHit) {
            mAliyunDownloadManager.startDownload(infos.get(0));
        }

        mPlayerDownloadAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAdd(AliyunDownloadMediaInfo info) {

    }

    @Override
    public void onStart(AliyunDownloadMediaInfo info) {
        if (info == null) {
            return;
        }
        //开始下载状态更新
        mPlayerDownloadAdapter.updateData(info);

    }

    @Override
    public void onProgress(AliyunDownloadMediaInfo info, int percent) {
        if (info == null) {
            return;
        }

        //更新对应数据的进度条
        mPlayerDownloadAdapter.updateData(info);
    }


    @Override
    public void onStop(AliyunDownloadMediaInfo info) {
        if (info == null) {
            return;
        }
        mPlayerDownloadAdapter.updateData(info);

    }

    @Override
    public void onCompletion(AliyunDownloadMediaInfo info) {
        //下载完成后,更新本地保存path
        if(mDownloadMediaInfos != null && mDownloadMediaInfos.contains(info)){
            AliyunDownloadMediaInfo aliyunDownloadMediaInfo = mDownloadMediaInfos.get(mDownloadMediaInfos.indexOf(info));
            aliyunDownloadMediaInfo.setSavePath(info.getSavePath());
        }
        mPlayerDownloadAdapter.updateData(info);
    }

    @Override
    public void onError(AliyunDownloadMediaInfo info, ErrorCode code, String msg, String requestId) {
        if (info == null || code == null || TextUtils.isEmpty(requestId)) {
            return;
        }
        ToastUtils.show(AlivcChildCacheVideoActivity.this, msg);
        mPlayerDownloadAdapter.updateData(info);
    }

    @Override
    public void onWait(AliyunDownloadMediaInfo outMediaInfo) {
    }


    @Override
    public void onDelete(AliyunDownloadMediaInfo info) {
        //刷新数据
        reFreshData(info);
        FixedToastUtils.show(AlivcChildCacheVideoActivity.this, getResources().getString(R.string.alivc_longvideo_cachevideo_delected_cachevideo));

    }

    @Override
    public void onDeleteAll() {

    }

    @Override
    public void onFileProgress(AliyunDownloadMediaInfo info) {

    }

    /**
     * 切换到4g，不可下载，点击提示，可运营商下载或连接wifi
     * 如果允许4G下载，并且有权限，并且已经prepared成功，需要暂停之前的下载
     */
    private void onWifiTo4G() {
        mIsNetWorkconnect = false;

        /*切换到4G环境下并且不允许运营商下载则 暂停下载*/
        if (!mIsOperatorDownld) {
            FixedToastUtils.show(AlivcChildCacheVideoActivity.this, getResources().getString(R.string.alivc_longvideo_cache_toast_4g));
            for (AliyunDownloadMediaInfo mediaInfo : mDownloadMediaInfos) {
                if (mediaInfo.getStatus() != AliyunDownloadMediaInfo.Status.Complete || mediaInfo.getProgress() != 100) {
                    mAliyunDownloadManager.stopDownload(mediaInfo);
                }
            }
            return;
        }
    }

    /**
     * 切换到wifi，自动下载
     */
    private void on4GToWifi() {
        //如果已经显示错误了，那么就不用显示网络变化的提示了。
        mIsNetWorkconnect = true;
    }

    private void onNetDisconnected() {
        FixedToastUtils.show(AlivcChildCacheVideoActivity.this, getResources().getString(R.string.aliyun_net_error));
        //网络断开。
        // NOTE： 由于安卓这块网络切换的时候，有时候也会先报断开。所以这个回调是不准确的。
    }

    // 连网断网监听
    private NetConnectedListener mNetConnectedListener = null;

    public void setNetConnectedListener(NetConnectedListener listener) {
        this.mNetConnectedListener = listener;
    }

    /**
     * 判断是否有网络的监听
     */
    public interface NetConnectedListener {
        /**
         * 网络已连接
         */
        void onReNetConnected(boolean isReconnect);

        /**
         * 网络未连接
         */
        void onNetUnConnected();
    }

    /**
     * 断网/连网监听
     */
    private class MyNetConnectedListener implements NetWatchdog.NetConnectedListener {
        public MyNetConnectedListener(AlivcChildCacheVideoActivity alivcCacheVideoActivity) {
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            if (mNetConnectedListener != null) {
                mNetConnectedListener.onReNetConnected(isReconnect);
            }
        }

        @Override
        public void onNetUnConnected() {
            if (mNetConnectedListener != null) {
                mNetConnectedListener.onNetUnConnected();
            }
        }
    }

    /**
     * 网络监听
     */
    private static class MyNetChangeListener implements NetWatchdog.NetChangeListener {

        private WeakReference<AlivcChildCacheVideoActivity> viewWeakReference;

        public MyNetChangeListener(AlivcChildCacheVideoActivity cacheVideoActivity) {
            viewWeakReference = new WeakReference<AlivcChildCacheVideoActivity>(cacheVideoActivity);
        }

        @Override
        public void onWifiTo4G() {
            AlivcChildCacheVideoActivity aliyunVodPlayerView = viewWeakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.onWifiTo4G();
            }
        }

        @Override
        public void on4GToWifi() {
            AlivcChildCacheVideoActivity cacheVideoActivity = viewWeakReference.get();
            if (cacheVideoActivity != null) {
                cacheVideoActivity.on4GToWifi();
            }
        }

        @Override
        public void onNetDisconnected() {
            AlivcChildCacheVideoActivity cacheVideoActivity = viewWeakReference.get();
            if (cacheVideoActivity != null) {
                cacheVideoActivity.onNetDisconnected();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            mCheckResult = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    mCheckResult = false;

                    break;
                } else {
                }
            }

            if (!isAllGranted) {
                // 如果所有的权限都授予了
                showPermissionDialog();

            }
        }
    }

    //系统授权设置的弹框
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alivc_longvideo_app_name) + getString(R.string.alivc_longvideo_request_permission_content_text))
        .setPositiveButton(R.string.alivc_longvideo_request_permission_positive_btn_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        })
        .setNegativeButton(R.string.alivc_longvideo_request_permission_negative_btn_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        })
        .setCancelable(false)
        .create()
        .show();

    }
}
