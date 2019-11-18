package com.aliyun.solution.longvideo.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.solution.longvideo.R;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;

import java.util.List;

/**
 * 剧集Adapter
 */
public class AlivcAllSeriesQuickAdapter extends BaseQuickAdapter<LongVideoBean, BaseViewHolder> {

    /**
     * 正在播放的集数
     */
    private String mPlayingPosition;

    public AlivcAllSeriesQuickAdapter(int layoutResId, @Nullable List<LongVideoBean> data, String playingPosition) {
        super(layoutResId, data);
        this.mPlayingPosition = playingPosition;
    }

    @Override
    protected void convert(BaseViewHolder helper, LongVideoBean item) {
        TextView mSeriesEpisodeTextView = helper.getView(R.id.tv_series_episode);
        mSeriesEpisodeTextView.setText(item.getSort());
        mSeriesEpisodeTextView.setTextColor(mPlayingPosition.equals(item.getSort()) ?
                                            mContext.getResources().getColor(R.color.alivc_common_bg_red_darker) :
                                            mContext.getResources().getColor(R.color.alivc_longvideo_title_right_font_black));
    }
}
