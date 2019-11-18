package com.aliyun.solution.longvideo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.player.alivcplayerexpand.util.Formatter;
import com.aliyun.player.alivcplayerexpand.util.download.StorageUtil;
import com.aliyun.solution.longvideo.R;


import java.util.ArrayList;

/**
 * 剧集缓存界面底部提示
 */
public class CacheFileTipsView extends RelativeLayout implements View.OnClickListener {

    /**
     * 缓存文件
     */
    private TextView mCacheFileTextView;
    /**
     * 剩余存储空间
     */
    private TextView mStorageResidue;

    /**
     * 缓存文件个数
     */
    private TextView mBadgeTextView;

    /**
     * 底部信息父布局
     */
    private RelativeLayout mSeriesCacheTipsViewGroup;

    /**
     * 是否是编辑状态
     */
    private boolean mIsEdit;

    private ArrayList<LongVideoBean> mSelectedList;

    private ArrayList<LongVideoBean> mDownloadList;

    private OnCacheViewGroupClickListener mOnCacheViewGroupClickListener;

    public CacheFileTipsView(Context context) {
        super(context);
        init();
    }

    public CacheFileTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CacheFileTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_long_video_view_layout_series_cache, this, true);
        findView();
        initListener();
    }

    private void findView() {
        mCacheFileTextView = findViewById(R.id.tv_cache_file);
        mStorageResidue = findViewById(R.id.tv_storage_residue);
        mSeriesCacheTipsViewGroup = findViewById(R.id.rl_series_cache_tips);
        mBadgeTextView = findViewById(R.id.tv_badge);
        mBadgeTextView.setVisibility((mDownloadList == null || mDownloadList.size() <= 0) ? View.GONE : View.VISIBLE);
        mBadgeTextView.setText(mDownloadList == null ? "0" : mDownloadList.size() + "");
    }

    private void initListener() {
        mSeriesCacheTipsViewGroup.setOnClickListener(this);
    }

    /**
     * 进入编辑状态
     */
    private void enterEdit() {
        mBadgeTextView.setVisibility(View.GONE);
        //确定缓存
        if (mSelectedList != null && mSelectedList.size() > 0) {
            mCacheFileTextView.setText(String.format(getResources().getString(R.string.alivc_longvideo_series_cache_confirm_download) + " %d", mSelectedList.size()));
            mCacheFileTextView.setTextColor(getResources().getColor(R.color.alivc_common_white));
            long mUseStorage = 0;
            for (LongVideoBean longVideoBean : mSelectedList) {
                if (longVideoBean.isSelected()) {
                    mUseStorage += Long.valueOf(longVideoBean.getSize());
                }
            }
            String mIntentUseStorage = Formatter.getFileSizeDescription(mUseStorage);
            mStorageResidue.setText(String.format(getResources().getString(R.string.alivc_longvideo_series_cache_storage_tips), mIntentUseStorage, getAvailableSize()));
            mStorageResidue.setTextColor(getResources().getColor(R.color.alivc_common_white));
            mSeriesCacheTipsViewGroup.setBackgroundResource(R.color.alivc_common_bg_red_darker);
        } else {
            mCacheFileTextView.setText(R.string.alivc_longvideo_series_cache_confirm_download);
            mCacheFileTextView.setTextColor(getResources().getColor(R.color.alivc_longvideo_font_black));
            mStorageResidue.setText(String.format(getResources().getString(R.string.alivc_longvideo_series_cache_storage_residue), getAvailableSize()));
            mStorageResidue.setTextColor(getResources().getColor(R.color.alivc_longvideo_font_more));
            mSeriesCacheTipsViewGroup.setBackgroundResource(R.color.alivc_common_white);
        }

    }

    /**
     * 退出编辑状态
     */
    private void exitEdit() {
        mBadgeTextView.setVisibility((mDownloadList == null || mDownloadList.size() <= 0) ? View.GONE : View.VISIBLE);
        mBadgeTextView.setText(mDownloadList == null ? "0" : mDownloadList.size() + "");
        //已缓存文件
        mCacheFileTextView.setText(R.string.alivc_longvideo_series_cache_downloaded);
        mCacheFileTextView.setTextColor(getResources().getColor(R.color.alivc_longvideo_font_black));
        mStorageResidue.setText(String.format(getResources().getString(R.string.alivc_longvideo_series_cache_storage_residue), getAvailableSize()));
        mStorageResidue.setTextColor(getResources().getColor(R.color.alivc_longvideo_font_more));
        mSeriesCacheTipsViewGroup.setBackgroundResource(R.color.alivc_common_white);
    }

    /**
     * 设置是否是编辑状态
     */
    public void setEdit(boolean isEdit) {
        this.mIsEdit = isEdit;
        refresh();
    }

    public void setSelectedList( ArrayList<LongVideoBean> selectedList) {
        this.mSelectedList = selectedList;
        refresh();
    }

    public void setDownloadList( ArrayList<LongVideoBean> downloadedList) {
        this.mDownloadList = downloadedList;
        refresh();
    }

    /**
     * 刷新
     */
    private void refresh() {
        if (mIsEdit) {
            enterEdit();
        } else {
            exitEdit();
        }
    }

    /**
     * 剩余内存剩余大小
     */
    private String getAvailableSize() {
        long availableExternalMemorySize = StorageUtil.getAvailableExternalMemorySize() * 1024L;
        return Formatter.getFileSizeDescription(availableExternalMemorySize);
    }

    @Override
    public void onClick(View v) {
        if (v == mSeriesCacheTipsViewGroup) {
            onCacheViewGroupClick();
        }
    }


    private void onCacheViewGroupClick() {
        if (mOnCacheViewGroupClickListener != null) {
            mOnCacheViewGroupClickListener.onCacheViewGroupClick(mIsEdit);
        }
    }

    public interface OnCacheViewGroupClickListener {
        void onCacheViewGroupClick(boolean isEdit);
    }

    public void setOnCacheViewGroupClick(OnCacheViewGroupClickListener listener) {
        this.mOnCacheViewGroupClickListener = listener;
    }
}
