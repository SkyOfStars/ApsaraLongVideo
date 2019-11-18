package com.aliyun.solution.longvideo.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.aliyun.player.alivcplayerexpand.bean.LongVideoBean;
import com.aliyun.solution.longvideo.R;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;

import java.util.List;

/**
 * 观看历史
 */
public class AlivcMineHistoryQuickAdapter extends BaseQuickAdapter<LongVideoBean, BaseViewHolder> {

    public AlivcMineHistoryQuickAdapter(int layoutResId, @Nullable List<LongVideoBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LongVideoBean item) {
        ImageView preView = helper.getView(R.id.alivc_iv_preview);
        helper.setText(R.id.alivc_tv_schedule, item.getWatchPercent() + "%")
        .setText(R.id.alivc_tv_name, item.getTitle());
        new ImageLoaderImpl().loadImage(mContext, item.getCoverUrl(), new ImageLoaderOptions.Builder()
                                        .crossFade()
                                        .roundCorner()
                                        .error(R.mipmap.ic_launcher)
                                        .build()).into(preView);

    }
}
