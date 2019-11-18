package com.aliyun.solution.longvideo.adapter;

import android.text.TextUtils;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.solution.longvideo.R;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;

/**
 * 播放界面剧集的Adapter
 */
public class AlivcSeriesPlayerEpisodeQuickAdapter extends BaseQuickAdapter<LongVideoBean, BaseViewHolder> {

    private String mCurrentEpisode;

    public AlivcSeriesPlayerEpisodeQuickAdapter(int layoutResId, String currentEpisode) {
        super(layoutResId, null);
        mCurrentEpisode = currentEpisode;
    }

    /**
     * 设置当前选中集数
     */
    public void setCurrentEpisode(String currentEpisode) {
        this.mCurrentEpisode = currentEpisode;
    }

    @Override
    protected void convert(BaseViewHolder helper, LongVideoBean item) {
        TextView seriesEpisodeTextView = helper.getView(R.id.tv_series_episode);
        seriesEpisodeTextView.setText(item.getSort());
        if(!TextUtils.isEmpty(mCurrentEpisode)){
            seriesEpisodeTextView.setTextColor(mCurrentEpisode.equals(item.getSort()) ? mContext.getResources().getColor(R.color.alivc_common_bg_red_darker) :
                    mContext.getResources().getColor(R.color.alivc_longvideo_title_right_font_black));
        }
    }
}
