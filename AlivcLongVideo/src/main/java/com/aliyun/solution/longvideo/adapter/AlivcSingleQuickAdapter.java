package com.aliyun.solution.longvideo.adapter;

import android.widget.ImageView;

import com.aliyun.player.alivcplayerexpand.util.TimeFormater;
import com.aliyun.solution.longvideo.R;
import com.aliyun.solution.longvideo.bean.SingleSectionBean;
import com.aliyun.svideo.common.baseAdapter.BaseSectionQuickAdapter;
import com.aliyun.svideo.common.baseAdapter.BaseViewHolder;
import com.aliyun.svideo.common.utils.image.ImageLoaderImpl;
import com.aliyun.svideo.common.utils.image.ImageLoaderOptions;

import java.util.List;

public class AlivcSingleQuickAdapter extends BaseSectionQuickAdapter<SingleSectionBean, BaseViewHolder> {


    public AlivcSingleQuickAdapter(int layoutResId, int sectionHeadResId, List<SingleSectionBean> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, SingleSectionBean item) {
        helper.setText(R.id.alivc_tv_title, item.header);

    }

    @Override
    protected void convert(BaseViewHolder helper, SingleSectionBean item) {
        if (item.t != null) {
            helper.setText(R.id.alivc_content_title, item.t.getTitle())
            .setText(R.id.alivc_tv_duration, TimeFormater.formatMs((long) (Double.valueOf(item.t.getDuration()) * 1000L)));
            ImageView preView = helper.getView(R.id.alivc_iv_preview);
            new ImageLoaderImpl().loadImage(mContext, item.t.getCoverUrl(), new ImageLoaderOptions.Builder()
                                            .crossFade()
                                            .error(R.mipmap.ic_launcher)
                                            .build()).into(preView);
        }
    }
}
