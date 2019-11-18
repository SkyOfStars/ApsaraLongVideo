package com.aliyun.solution.longvideo.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.solution.longvideo.R;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;

/**
 * 剧集缓存Adapter
 */
public class AlivcSeriesCacheQuickAdapter extends BaseQuickAdapter<LongVideoBean, BaseViewHolder> {

    private boolean mIsEdit = false;
    private String mCurrentEpisode;

    public AlivcSeriesCacheQuickAdapter(int layoutResId, String currentEpisode) {
        super(layoutResId, null);
        this.mCurrentEpisode = currentEpisode;
    }

    @Override
    protected void convert(BaseViewHolder helper, LongVideoBean item) {
        TextView mSeriesEpisodeTextView = helper.getView(R.id.tv_series_episode);
        mSeriesEpisodeTextView.setText(item.getSort());
        ImageView mVideoStateBottomImageView = helper.getView(R.id.iv_video_state_bottom);
        ImageView mVideoStateTopImageView = helper.getView(R.id.iv_video_state_top);

        //编辑状态
        if (mIsEdit) {
            mVideoStateBottomImageView.setVisibility(View.VISIBLE);
            if(item.isSelected()){
                mVideoStateBottomImageView.setImageResource(R.drawable.alivc_longvideo_icon_series_cache_selected);
            }else if(item.isDownloaded() || item.isDownloading()){
                mVideoStateBottomImageView.setImageResource(item.isDownloaded() ?
                        R.drawable.alivc_longvideo_icon_series_cache_downloaded : R.drawable.alivc_longvideo_icon_series_cache_downloading);
            }else{
                mVideoStateBottomImageView.setVisibility(View.GONE);
            }
        } else {
            //非编辑状态
            if (item.isDownloaded() || item.isDownloading()) {
                mVideoStateBottomImageView.setVisibility(View.VISIBLE);
                mVideoStateBottomImageView.setImageResource(item.isDownloaded() ?
                        R.drawable.alivc_longvideo_icon_series_cache_downloaded : R.drawable.alivc_longvideo_icon_series_cache_downloading);
            } else {
                mVideoStateBottomImageView.setVisibility(View.GONE);
            }
        }

        //当前正在播放的集数,只会显示在左上角
        mVideoStateTopImageView.setVisibility((mCurrentEpisode.equals(item.getSort())) ? View.VISIBLE : View.GONE);
        if (mCurrentEpisode.equals(item.getSort())) {
            mVideoStateTopImageView.setImageResource(R.drawable.alivc_longvideo_icon_series_cache_playing);
        }


        mSeriesEpisodeTextView.setTextColor(mCurrentEpisode.equals(item.getSort()) ? mContext.getResources().getColor(R.color.alivc_common_bg_red_darker) :
                mContext.getResources().getColor(R.color.alivc_longvideo_title_right_font_black));
    }

    public void isEdit(boolean isEdit) {
        this.mIsEdit = isEdit;
    }
}
