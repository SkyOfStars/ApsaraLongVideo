package com.aliyun.solution.longvideo.adapter;

import android.widget.ImageView;

import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.bean.SerierSectionBean;
import com.aliyun.solution.longvideo.bean.SingleSectionBean;
import com.aliyun.svideo.common.baseAdapter.BaseSectionQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;

import java.util.List;

public class AlivcSeriesQuickAdapter extends BaseSectionQuickAdapter<SerierSectionBean, BaseViewHolder> {


    public AlivcSeriesQuickAdapter(int layoutResId, int sectionHeadResId, List<SerierSectionBean> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SerierSectionBean item) {
        helper.setText(R.id.alivc_tv_title, item.header);

    }

    @Override
    protected void convert(BaseViewHolder helper, SerierSectionBean item) {
        helper.setText(R.id.alivc_content_title, item.t.getTitle())
        .setText(R.id.alivc_tv_duration, String.format(mContext.getResources().getString(R.string.alivc_longvideo_series_set), item.t.getTotal()));

        ImageView preView = helper.getView(R.id.alivc_iv_preview);
        new ImageLoaderImpl().loadImage(mContext, item.t.getCoverUrl(), new ImageLoaderOptions.Builder()
                                        .crossFade()
                                        .error(R.mipmap.ic_launcher)
                                        .build()).into(preView);
    }
}
