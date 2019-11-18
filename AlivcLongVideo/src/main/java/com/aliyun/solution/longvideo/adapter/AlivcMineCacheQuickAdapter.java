package com.aliyun.solution.longvideo.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.util.download.AliyunDownloadMediaInfo;
import com.aliyun.solution.longvideo.R;
import com.aliyun.svideo.common.baseAdapter.BaseQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;

import java.util.List;

/**
 * 个人中心缓存adapter
 */
public class AlivcMineCacheQuickAdapter extends BaseQuickAdapter<AliyunDownloadMediaInfo, BaseViewHolder> {

    public AlivcMineCacheQuickAdapter(int layoutResId, @Nullable List<AliyunDownloadMediaInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AliyunDownloadMediaInfo item) {
        helper.setText(R.id.alivc_tv_name, item.getTitle());
        ImageView preView = helper.getView(R.id.alivc_iv_preview);
        TextView scheduleTv = helper.getView(R.id.alivc_tv_schedule);
        new ImageLoaderImpl().loadImage(mContext, item.getCoverUrl(), new ImageLoaderOptions.Builder()
                                        .crossFade()
                                        .roundCorner()
                                        .error(R.mipmap.ic_launcher)
                                        .build()).into(preView);
        scheduleTv.setVisibility(View.GONE);
    }
}
